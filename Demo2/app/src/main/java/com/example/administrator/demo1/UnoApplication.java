package com.example.administrator.demo1;

import android.app.Application;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Conghui.
 */


public class UnoApplication extends Application {

    public static String server = "http://10.29.179.110:8080/";
    //public static String server = "http://192.168.31.187:8080/";my
    //public static String server = "http://10.25.68.206:9092";//our server

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


}
