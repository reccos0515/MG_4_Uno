package com.damoore;

public class UnoPlayer {

		private String playerName;
		private int handsPlayed;
		private int handsWon;
		
		public UnoPlayer(String playerName, int handsPlayed, int handsWon)
		{
			this.playerName = playerName;
			this.handsPlayed = handsPlayed;
			this.handsWon = handsWon;
		}
		
		public String getPlayerName() {
			return playerName;
		}
		
		public int getHandsPlayed() {
			return handsPlayed;
		}
		
		public int getHandsWon() {
			return handsWon;
		}
}
