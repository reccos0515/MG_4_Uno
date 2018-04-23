package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import zfrisv.cs309.ServerSocketApplication;

/**
 * Class to run both the Spring Boot Application and the SocketIO Application
 * @author Dakota Moore
 */
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
