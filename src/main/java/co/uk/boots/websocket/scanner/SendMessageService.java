package co.uk.boots.websocket.scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import co.uk.boots.websocket.scanner.com.ComPortDataProcessor;

@Service
public class SendMessageService implements ComPortDataProcessor{
	private SimpMessagingTemplate template;
	
	@Autowired
	public SendMessageService (SimpMessagingTemplate template){
		this.template = template;
	}

	@Override
	public void process(String data) {
		// TODO Auto-generated method stub
		System.out.println("[Messaging Thread] Sending:" + data);
		try {
			this.template.convertAndSend("/topic/greetings", data);
		} catch (MessagingException me) {
			me.printStackTrace();
		}
	}
	
	
}
