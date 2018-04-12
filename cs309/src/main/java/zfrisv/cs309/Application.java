package zfrisv.cs309;

import org.springframework.boot.SpringApplication;
import java.lang.Thread;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import zfrisv.cs309.*;

/**
 * Launches the server application.
 * @author Dakota Moore
 *
 */
@SpringBootApplication
public class Application {
	
	public static void main(String[] args) {
		/*new Thread(new Runnable() { 
			public void run() {
				ServerSocketApplication.run();
			}
		}).start();*/
        SpringApplication.run(Application.class, args);
    }
	
}
