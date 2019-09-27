package co.uk.boots.websocket.scanner.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import co.uk.boots.websocket.scanner.com.ScannerStatus;

@Service
public class SendMessageService implements WebSocketScanSender{
	private SimpMessagingTemplate template;
	
	@Autowired
	public SendMessageService (SimpMessagingTemplate template){
		this.template = template;
	}

	@Override
	public void send(ScannerStatus status) {
		// TODO Auto-generated method stub
		try {
			System.out.println("[SendMessageService] Sending:" + status);
			this.template.convertAndSend("/topic/com", status);
			status.setDataMessage("");
		} catch (MessagingException me) {
			me.printStackTrace();
		}
	}
}
