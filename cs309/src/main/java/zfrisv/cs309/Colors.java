package zfrisv.cs309;

public class Colors {
	public static Colors NONE ;
	public static Colors RED;
	public static Colors GREEN;
	public static Colors YELLOW;
	public static Colors BLUE;
	public static Colors[] colors;
	public Colors() {
		colors = new Colors[5];
		colors[0]=NONE;
		colors[1]=RED;
		colors[2]=GREEN;
		colors[3]=YELLOW;
		colors[4]=BLUE;
		
	}
	
	public static Colors[] values(){
		return colors;
	}
}
