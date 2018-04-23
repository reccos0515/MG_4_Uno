package zfrisv.cs309;

import java.util.ArrayList;

public class Lobby {
	
	static ArrayList<String> members;
	static String name;

	public Lobby(ArrayList<String> members, String name) {
		Lobby.members = members;
		Lobby.name = name;
	}
	
	public ArrayList<String> getMembers(){
		return members;
	}
	
	public String getName() {
		return name;
	}
	
	public void addMember(String member) {
		members.add(member);
	}
	
	public int getSize() {
		return members.size();
	}
	
	public void removeUser(String user) {
		if (members.contains(user)){
			members.remove(user);
			
		}
		
	}
	
	
}
