package com.entscz.krizovezasoby.util;

import android.util.Log;

public abstract class Promise<ResultType> {
	
	private Thread thread;
	private RuntimeException exception;
	private ResultType result;
	
	public Promise() {
		
		thread = new Thread() {
			@Override
			public void run() {
				try {
					result = execute();
				} catch(Exception e) {
					exception = new RuntimeException(e.getMessage());
				}
			}
		};
		
		thread.start();
		
	}
	
	public abstract ResultType execute();
	
	public ResultType await() {
		
		try {
			thread.join();
		} catch(Exception e) {
			Log.e("Promise", "Failed joining promise thread: ");
			e.printStackTrace();
		}
		
		if(exception!=null) {
			Log.e("Promise","Exception thrown in promise at");
			new Exception().printStackTrace();
			throw exception;
		}
		
		return result;
		
	} 
	
}
