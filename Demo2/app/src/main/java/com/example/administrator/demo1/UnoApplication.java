package com.example.administrator.demo1;

import android.app.Application;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * This class is for creating a global socket with a defined port and ip address, that can be used in all activities.
 */


public class UnoApplication extends Application {


    public static String server = "http://192.168.1.107:8080/";

    private static UnoApplication instance;
    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
    }

    /**
     * this create a static socket for all activities.
     */
    private static Socket gsocket;
    {
        try{
            gsocket = IO.socket(server);

        }catch(URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * method for getting the socket.
     * @return
     */
    public Socket getSocket(){
        return this.gsocket;
    }


}
