package com.example.administrator.demo1.MultiplayerChat;

/**
 * This is the message format will be implemented in the chat activity.
 */

public class Message {
    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_LOG = 1;

    private String mMessage;
    private String mUsername;

    private Message() {}


    public String getMessage() {
        return mMessage;
    }

    public String getUsername() {
        return mUsername;
    }

    /**
     * builder buildes the message contains user's username, text input.
     */
    public static class Builder {

        private String mUsername;
        private String mMessage;


        public Builder username(String username) {
            mUsername = username;
            return this;
        }

        public Builder message(String message) {
            mMessage = message;
            return this;
        }

        public Message build() {
            Message message = new Message();
            message.mUsername = mUsername;
            message.mMessage = mMessage;
            return message;
        }
    }
}
