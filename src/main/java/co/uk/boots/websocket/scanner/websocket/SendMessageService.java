package co.uk.boots.websocket.scanner.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SendMessageService implements WebSocketScanSender{
	private SimpMessagingTemplate template;
	
	@Autowired
	public SendMessageService (SimpMessagingTemplate template){
		this.template = template;
	}

	@Override
	public void send(String data) {
		// TODO Auto-generated method stub
		System.out.println("[SendMessageService] Sending:" + data);
		try {
			this.template.convertAndSend("/topic/greetings", data);
		} catch (MessagingException me) {
			me.printStackTrace();
		}
	}
	
	
}
