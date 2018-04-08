package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import zfrisv.cs309.ServerSocketApplication;
import damoore.leaderboard.LeaderboardApp;
import damoore.player.*;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		new Thread(new Runnable() {
			public void run() {
				ServerSocketApplication.runSocket();
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				PlayerApp.run();
			}
		}).start();
		/*new Thread(new Runnable() {
			public void run() {
				LeaderboardApp.run();
			}
		}).start();*/
	}

}
