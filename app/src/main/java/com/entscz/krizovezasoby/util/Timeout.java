package com.entscz.krizovezasoby.util;

public class Timeout {

    public interface TimeoutCallback {
        void execute();
    }

    public static Timeout start(int ms, TimeoutCallback callback){
        Timeout timeout = new Timeout(callback);
        timeout.start(ms);
        return timeout;
    }

    private TimeoutCallback callback;
    private Thread thread;

    private Timeout(TimeoutCallback callback){
        this.callback = callback;
    }

    private void start(int ms){

        thread = new Thread(()->{

            try {
                Thread.sleep(ms);
            } catch(Exception e) {
                return;
            }

            new Thread(()->{
                callback.execute();
            }).start();

        });

        thread.start();

    }

    public void stop() {
        thread.interrupt();
    }

}
