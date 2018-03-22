package com.example.administrator.demo1;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import io.socket.client.Socket;

/**
 * Created by Conghui.
 */

class CustomMessageEvent {

    private String username;
    private int numUser;
    private ArrayList<String> usernameArr;
    private io.socket.client.Socket gsocket;

    public void setUsername(String username) {
        this.username = username;
    }
    public String getusername(){
        return username;
    }

    public int getNumUser() {
        return numUser;
    }

    public void setNumUser(int numUser) {
        this.numUser = numUser;
    }

    public void setArrUsername(ArrayList<String> arr){
        this.usernameArr= arr;
    }
    public ArrayList<String> getArrUsername(){
        return usernameArr;
    }

    public Socket getGsocket() {
        return gsocket;
    }

    public void setGsocket(Socket gsocket) {
        this.gsocket = gsocket;
    }
}
