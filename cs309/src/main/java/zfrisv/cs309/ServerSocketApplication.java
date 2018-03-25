package zfrisv.cs309;

import java.util.ArrayList;

import com.corundumstudio.socketio.AckCallback;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.VoidAckCallback;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.ConnectListener;



public class ServerSocketApplication {
	
	ArrayList<String> users = new ArrayList<String>();
	public static void main(String[] args) {
		
		Configuration config = new Configuration();
        //config.setHostname("10.25.68.206");
        config.setPort(8080);
        
        final SocketIOServer server = new SocketIOServer(config);
        
        server.addConnectListener(new ConnectListener() {
        	
        	public void onConnect(SocketIOClient client) {
        		System.out.println("New client connected");
        	}
        });
       
        server.addEventListener("add user", String.class, new DataListener<String>() {
        		public void onData(SocketIOClient arg0, String username, AckRequest arg2) throws Exception {
				System.out.println("user " + username);
				users.add(username);
				//server.getBroadcastOperations().sendEvent("user joined", username);
				server.getBroadcastOperations().sendEvent("existed users", users);
				System.out.println(users.toString());
        }});
        
        server.start();
        System.out.println("Server started...");
        try {
			Thread.sleep(Integer.MAX_VALUE);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        server.stop();
	}
}
