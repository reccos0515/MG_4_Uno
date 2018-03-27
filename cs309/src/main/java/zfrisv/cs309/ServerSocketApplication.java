package zfrisv.cs309;

import java.util.ArrayList;

import com.corundumstudio.socketio.AckCallback;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.VoidAckCallback;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.ConnectListener;



public class ServerSocketApplication {
	
	private static ArrayList<String> users = new ArrayList<String>();
	public static void run() {
		
		Configuration config = new Configuration();
        //config.setHostname("10.25.68.206");
        config.setPort(8080);
        
        final SocketIOServer server = new SocketIOServer(config);
        
        server.addConnectListener(new ConnectListener() {
        	
        	public void onConnect(SocketIOClient client) {
        		System.out.println("New client connected");
        	}
        });
        
        server.addEventListener("user left", String.class, new DataListener<String>() {
        	public void onData(SocketIOClient arg0, String username, AckRequest arg2) throws Exception {
				System.out.println("user " + username);
				for(int i=0;i<users.size();i++) {
					if(username.equals(users.get(i))) {
						users.remove(i);
					}
				}
				server.getBroadcastOperations().sendEvent("existed users", users);
				System.out.println(users.toString());
        }});
       
        server.addEventListener("add user", String.class, new DataListener<String>() {
        		public void onData(SocketIOClient arg0, String username, AckRequest arg2) throws Exception {
				System.out.println("user " + username);
				users.add(username);
				//server.getBroadcastOperations().sendEvent("user joined", username);
				server.getBroadcastOperations().sendEvent("existed users", users);
				System.out.println(users.toString());
        }});
        
        //To users in room1, set game in server, haven't tested yet.Need to be debugged.Utilize namespace function in socketio.
		final SocketIONamespace room1 = server.addNamespace("/room1");
		final ArrayList<UnoPlayer> players = new ArrayList<UnoPlayer>();
		final UnoDeck deck1 =new UnoDeck();
		final ArrayList<UnoHand> hands = deck1.dealHands(3);
		room1.addEventListener("Join a game", String.class, new DataListener<String>() {	
            public void onData(SocketIOClient arg0, String username, AckRequest arg2) {				
				int i=0;	
				if(i<3 	&& players.get(i)==null) {
					players.add(new UnoPlayer(i,hands.get(i),username));
					i++;		
				}else if(i==3){
					ArrayList<UnoCards> disposal_Stack = new ArrayList<UnoCards>();
					UnoGame game1 = new UnoGame(deck1,players,disposal_Stack, 0,0);
					room1.getBroadcastOperations().sendEvent("set game", game1);
				}
            }

		});
        
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
