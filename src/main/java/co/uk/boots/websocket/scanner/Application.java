package co.uk.boots.websocket.scanner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import co.uk.boots.websocket.scanner.com.ComPortDataProcessor;
import co.uk.boots.websocket.scanner.com.NonBlockingListener;

@SpringBootApplication
public class Application implements ComPortDataProcessor{

	@Bean
	public ThreadPoolTaskExecutor threadPoolTaskExecutor(){
	    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	    executor.setCorePoolSize(15);
	    executor.setMaxPoolSize(42);
	    executor.setQueueCapacity(11);
	    executor.setThreadNamePrefix("threadPoolExecutor-");
	    executor.setWaitForTasksToCompleteOnShutdown(true);
	    executor.initialize();
	    return executor;
	}
	
	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(Application.class, args);
		Application app = run.getBean(Application.class);
		app.run();
	}
	
	@Override
	public void process(String data) {
		// TODO Auto-generated method stub
		
	}

	private void run() {
		ExecutorService service = Executors.newFixedThreadPool(1);
		Runnable r = () -> {
			new NonBlockingListener(this);
		};
		
		service.submit(r);
		service.shutdown();
		try {
			service.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
