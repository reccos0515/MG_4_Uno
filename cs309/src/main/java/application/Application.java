package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import zfrisv.cs309.ServerSocketApplication;

@SpringBootApplication
public class Application implements Runnable {

	public void run() {
		ServerSocketApplication.run();
	}
	
	public static void main(String[] args) {
		new Thread(new Application()).start();
		SpringApplication.run(Application.class);
	}

}
