package com.entscz.krizovezasoby.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

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

	private static HashMap<String, Bitmap> bitmaps = new HashMap<>();
	
	static {
		CookieHandler.setDefault(new CookieManager());
	}

	public static Promise<String> GET(String url) {
		
		return new Promise<String>() {
			@Override
			public String execute() {
				
				try {

					HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
					
					if(conn.getResponseCode()>=400) {
						InputStream es = conn.getErrorStream();
						throw new RuntimeException(StreamReader.readStream(es));
					}
					
					InputStream is = conn.getInputStream();
					return StreamReader.readStream(is);
					
				} catch(Exception e) {
					Log.e("Requests", "GET "+url+" failed: "+e.getMessage());
					throw new RuntimeException(e.getMessage());
				}
				
			}
		};
		
	}

	public static Promise<Bitmap> GET_BITMAP(String url) {

		return new Promise<Bitmap>() {
			@Override
			public Bitmap execute() {

				if(bitmaps.containsKey(url)){
					return bitmaps.get(url);
				}

				try {

					HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();

					if(conn.getResponseCode()>=400) {
						InputStream es = conn.getErrorStream();
						throw new RuntimeException(StreamReader.readStream(es));
					}

					InputStream is = conn.getInputStream();
//					return StreamReader.readStream(is);
					Bitmap bitmap = BitmapFactory.decodeStream(is);
					bitmaps.put(url, bitmap);
					return bitmap;

				} catch(Exception e) {
					Log.e("Requests", "GET "+url+" failed: "+e.getMessage());
					throw new RuntimeException(e.getMessage());
				}

			}
		};

	}

	/** Use POST(String url, Params params) instead **/
	@Deprecated
	public static Promise<String> POST(String url, String params) {
		
		return new Promise<String>() {
			@Override
			public String execute() {
				
				try {

					HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
					conn.setDoOutput(true);
					
					OutputStream os = conn.getOutputStream();
					os.write(params.getBytes());
					
					if(conn.getResponseCode()>=400) {
						InputStream es = conn.getErrorStream();
						throw new RuntimeException(StreamReader.readStream(es));
					}
					
					InputStream is = conn.getInputStream();
					return StreamReader.readStream(is);
					
				} catch(Exception e) {
					Log.e("Requests", "POST "+url+" failed: "+e.getMessage());
					throw new RuntimeException(e.getMessage());
				}
				
			}
		};
		
	}

	public static Promise<String> POST(String url, Params params) {

		return new Promise<String>() {
			@Override
			public String execute() {

				try {

					HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
					conn.setDoOutput(true);

					OutputStream os = conn.getOutputStream();
					os.write(params.toString().getBytes());

					if(conn.getResponseCode()>=400) {
						InputStream es = conn.getErrorStream();
						throw new RuntimeException(StreamReader.readStream(es));
					}

					InputStream is = conn.getInputStream();
					return StreamReader.readStream(is);

				} catch(Exception e) {
					Log.e("Requests", "POST "+url+" failed: "+e.getMessage());
					throw new RuntimeException(e.getMessage());
				}

			}
		};

	}

	public static Promise<String> POST_DATA(String url, String fieldName, String fileName, String contentType, byte[] data) {

		return new Promise<String>() {
			@Override
			public String execute() {

				try {

					String boundary = Long.toHexString(System.currentTimeMillis());

					HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
					conn.setDoOutput(true);
					conn.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);

					OutputStream os = conn.getOutputStream();
//					os.write(params.getBytes());

					String prefix = "--"+boundary+"\n"+
							"Content-Disposition: form-data; name=\""+fieldName+"\"; filename=\""+fileName+"\"\n"+
							"Content-Type: "+contentType+"\n"+
							"\n";

					String postfix = "\n--"+boundary+"--\n";

					os.write(prefix.getBytes());
					os.write(data);
					os.write(postfix.getBytes());

					if(conn.getResponseCode()>=400) {
						InputStream es = conn.getErrorStream();
						throw new RuntimeException(StreamReader.readStream(es));
					}

					InputStream is = conn.getInputStream();
					return StreamReader.readStream(is);

				} catch(Exception e) {
					Log.e("Requests", "POST "+url+" failed: "+e.getMessage());
					throw new RuntimeException(e.getMessage());
				}

			}
		};

	}
	
}
