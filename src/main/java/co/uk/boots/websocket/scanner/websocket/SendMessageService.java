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
		// temp - just return entire status eventually
		try {
			if (status.getDataMessage().length() > 0) {
				System.out.println("[SendMessageService] Sending:" + status.getDataMessage());
				this.template.convertAndSend("/topic/com", status.getDataMessage());
			} else {
			System.out.println("[SendMessageService] Sending:" + status.getStatusMessage());
			this.template.convertAndSend("/topic/com", status.getStatusMessage());
			}
		} catch (MessagingException me) {
			me.printStackTrace();
		}
	}
}
