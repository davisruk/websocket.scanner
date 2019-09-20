package co.uk.boots.websocket.scanner.com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import co.uk.boots.websocket.scanner.websocket.SendMessageService;


@Component
public class ComPortController implements ComPortDataProcessor, ComPortCommandRunner{

	@Autowired
	private NonBlockingListener comPortListener;
	
	@Autowired
	public SendMessageService sendMessageService;
	
	@Override
	public void reconnectPort() {
		runListener();
	}

	@EventListener
    public void onContextRefreshed(ContextRefreshedEvent event) {
		runListener();		
    }
    
    @EventListener
    public void onContextClosed(ContextClosedEvent event) {
    	if (comPortListener.isRunning())
    		comPortListener.close();
    }
    
	@Override
	public void process(String data) {
		sendMessageService.send(data);
	}
	
	private void runListener() {
		if (!comPortListener.isRunning())
		comPortListener.run();
	}
 
	public void scannerStatusQuery() {
		String message = comPortListener.isPortOpened() ? "[STATUS]:Open" : "[STATUS]:Closed";
		sendMessageService.send(message);
	}
}
