package com.entscz.krizovezasoby.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.entscz.krizovezasoby.LoginManager;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class Requests {

	// TODO debug thing, remove for release?
	public static int THROTTLE_TIME = 0;

	public static class Params{
		private HashMap<String, String> params;
		public Params(){
			params = new HashMap<>();
		}
		public Params add(String name, Object value){
			params.put(name, value.toString());
			return this;
		}
		@Override
		public String toString() {
			ArrayList<String> pairs = new ArrayList<>();
			try {
				for (String s : params.keySet()) {
					String key = URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
					String value = URLEncoder.encode(params.get(s), StandardCharsets.UTF_8.toString());
					pairs.add(key+"="+value);
				}
			} catch(Exception e){}
			return ArrayUtils.join(pairs, "&");
		}
	}

	public static class HTTPError extends RuntimeException {
		public final int statusCode;
		public final String message;
		public HTTPError(int statusCode, String message){
			super(statusCode+" "+message);
			this.statusCode = statusCode;
			this.message = message;
		}
	}

	public static class NetworkError extends RuntimeException {
		public NetworkError(){
			super("Chyba připojení k serveru!");
		}
	}

//	public static void init(Context context){
//		cookieManager = new CookieManager();
//		CookieHandler.setDefault(cookieManager);
//	}
//
	private static HashMap<String, Bitmap> bitmapCache = new HashMap<>();
//	private static CookieManager cookieManager;
	
//	static {
//		CookieHandler.setDefault(new CookieManager());
//	}

	public static Promise<String> GET(String url) {

		return new Promise<String>() {
			@Override
			public String execute() {
				
				try {

					if(THROTTLE_TIME>0) Timer.sleep(THROTTLE_TIME);

					HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();

					int code = conn.getResponseCode();

					if(code==401){
						LoginManager.login(true);
						Log.i(getClass().getName(), "Received 401 - requesting reauth!");
						return GET(url).await();
					}

					if(code>=400) {
						InputStream es = conn.getErrorStream();
						String msg = StreamReader.readStream(es);
						Log.e(getClass().getName(), "HTTP error: "+code+" "+msg);
						throw new HTTPError(code, msg);
					}
					
					InputStream is = conn.getInputStream();
					return StreamReader.readStream(is);
					
				} catch(HTTPError e) {
					throw e;
				} catch(Exception e) {
					Log.e(getClass().getName(), "GET "+url+" failed: "+e.getMessage());
					throw new NetworkError();
				}
				
			}
		};
		
	}

	public static Promise<Bitmap> GET_BITMAP(String url) {

		return new Promise<Bitmap>() {
			@Override
			public Bitmap execute() {

				if(bitmapCache.containsKey(url)){
					return bitmapCache.get(url);
				}

				try {

					if(THROTTLE_TIME>0) Timer.sleep(THROTTLE_TIME);

					HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();

					int code = conn.getResponseCode();

					if(code==401){
						LoginManager.login(true);
						Log.i(getClass().getName(), "Received 401 - requesting reauth!");
						return GET_BITMAP(url).await();
					}

					if(code>=400) {
						InputStream es = conn.getErrorStream();
						String msg = StreamReader.readStream(es);
						Log.e(getClass().getName(), "HTTP error: "+code+" "+msg);
						throw new HTTPError(code, msg);
					}

					InputStream is = conn.getInputStream();
					Bitmap bitmap = BitmapFactory.decodeStream(is);
					bitmapCache.put(url, bitmap);
					return bitmap;

				} catch(HTTPError e) {
					throw e;
				} catch(Exception e) {
					Log.e(getClass().getName(), "GET_BITMAP "+url+" failed: "+e.getMessage());
					throw new NetworkError();
				}

			}
		};

	}

	public static Promise<String> POST(String url, Params params) {

		return new Promise<String>() {
			@Override
			public String execute() {

				try {

					if(THROTTLE_TIME>0) Timer.sleep(THROTTLE_TIME);

					HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
					conn.setDoOutput(true);

					OutputStream os = conn.getOutputStream();
					os.write(params.toString().getBytes());

					int code = conn.getResponseCode();

					if(code==401){
						LoginManager.login(true);
						Log.i(getClass().getName(), "Received 401 - requesting reauth!");
						return POST(url, params).await();
					}

					if(code>=400) {
						InputStream es = conn.getErrorStream();
						String msg = StreamReader.readStream(es);
						Log.e(getClass().getName(), "HTTP error: "+code+" "+msg);
						throw new HTTPError(code, msg);
					}

					InputStream is = conn.getInputStream();
					return StreamReader.readStream(is);

				} catch(HTTPError e) {
					throw e;
				} catch(Exception e) {
					Log.e("Requests", "POST "+url+" failed: "+e.getMessage());
					throw new NetworkError();
				}

			}
		};

	}

	public static Promise<String> POST_DATA(String url, String fieldName, String fileName, String contentType, byte[] data) {

		return new Promise<String>() {
			@Override
			public String execute() {

				try {

					if(THROTTLE_TIME>0) Timer.sleep(THROTTLE_TIME);

					String boundary = Long.toHexString(System.currentTimeMillis());

					HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
					conn.setDoOutput(true);
					conn.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);

					OutputStream os = conn.getOutputStream();

					String prefix = "--"+boundary+"\n"+
							"Content-Disposition: form-data; name=\""+fieldName+"\"; filename=\""+fileName+"\"\n"+
							"Content-Type: "+contentType+"\n"+
							"\n";

					String postfix = "\n--"+boundary+"--\n";

					os.write(prefix.getBytes());
					os.write(data);
					os.write(postfix.getBytes());

					int code = conn.getResponseCode();

					if(code==401){
						LoginManager.login(true);
						Log.i(getClass().getName(), "Received 401 - requesting reauth!");
						return POST_DATA(url, fieldName, fileName, contentType, data).await();
					}

					if(code>=400) {
						InputStream es = conn.getErrorStream();
						String msg = StreamReader.readStream(es);
						Log.e(getClass().getName(), "HTTP error: "+code+" "+msg);
						throw new HTTPError(code, msg);
					}

					InputStream is = conn.getInputStream();
					return StreamReader.readStream(is);

				} catch(HTTPError e) {
					throw e;
				} catch(Exception e) {
					Log.e(getClass().getName(), "POST_DATA "+url+" failed: "+e.getMessage());
					throw new NetworkError();
				}

			}
		};

	}
	
}
