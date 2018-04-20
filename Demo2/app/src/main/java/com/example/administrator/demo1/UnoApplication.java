package com.example.administrator.demo1;

import android.app.Application;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Dakota Moore and group MG4
 */


public class UnoApplication extends Application {


    public static final String TAG = AppController.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static AppController mInstance;


    public static String server = "http://173.23.120.117:8080/";

    private static UnoApplication instance;
    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
    }
    private static Socket gsocket;
    {
        try{
            gsocket = IO.socket(server);

        }catch(URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket(){
        return this.gsocket;
    }

    class AppController extends UnoApplication {

        /*
        public static final String TAG = AppController.class.getSimpleName();
        private RequestQueue mRequestQueue;
        private static AppController mInstance;*/

        @Override
        public void onCreate() {
            super.onCreate();
            mInstance = this;
        }

        /*
        public static synchronized AppController getInstance() {
            return mInstance;
        }*/

        public RequestQueue getRequestQueue() {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(getApplicationContext());
            }

            return mRequestQueue;
        }

        public <T> void addToRequestQueue(Request<T> req, String tag) {
            req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
            getRequestQueue().add(req);
        }

        public <T> void addToRequestQueue(Request<T> req) {
            req.setTag(TAG);
            getRequestQueue().add(req);
        }

        public void cancelPendingRequests(Object tag) {
            if (mRequestQueue != null) {
                mRequestQueue.cancelAll(tag);
            }
        }
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

}
/*
class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}*/
