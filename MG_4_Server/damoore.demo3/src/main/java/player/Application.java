package player;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import socket.ServerSocketApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		new Thread(new Runnable() { 
			public void run() {
				ServerSocketApplication.run();
			}
		}).start();
		SpringApplication.run(Application.class, args);
	}
	
}
