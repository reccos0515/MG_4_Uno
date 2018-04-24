package com.example.administrator.demo1;

import android.app.Application;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Dakota Moore and group MG4
 */
public class UnoApplication extends Application {

    public static final String TAG = UnoApplication.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static UnoApplication mInstance;
    public static String server = "http://10.26.54.214:8080/";

    private static UnoApplication instance;
    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
        mInstance = this;
    }
    private static Socket gsocket;
    {
        try{
            gsocket = IO.socket(server);

        }catch(URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public Socket getSocket(){
        return this.gsocket;
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

    public static synchronized UnoApplication getInstance() {
        return mInstance;
    }

}