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

public class Requests {
	
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

				try {

					HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();

					if(conn.getResponseCode()>=400) {
						InputStream es = conn.getErrorStream();
						throw new RuntimeException(StreamReader.readStream(es));
					}

					InputStream is = conn.getInputStream();
//					return StreamReader.readStream(is);
					return BitmapFactory.decodeStream(is);

				} catch(Exception e) {
					Log.e("Requests", "GET "+url+" failed: "+e.getMessage());
					throw new RuntimeException(e.getMessage());
				}

			}
		};

	}
	
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
