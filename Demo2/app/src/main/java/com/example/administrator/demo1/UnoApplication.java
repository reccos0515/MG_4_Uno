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

    //public static String server = "http://192.168.0.12/";//omie's place
    //public static String server = "http://10.26.1.248:8080/";//library
    public static String server = "http://10.26.3.120:8080/";//library
    //public static String server = "localhost";
    //public static String server = "http://10.29.180.154:8080/";//library
    //public static String server = "http://10.25.68.206:9092";//our server

    private final Socket gsocket;
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
