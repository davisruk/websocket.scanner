package co.uk.boots.websocket.scanner.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import co.uk.boots.websocket.scanner.com.ComPortController;

@Controller
public class WebSocketController {

	@Autowired
	ComPortController comPortController;
	
	@MessageMapping("/reconnectScanner")
	public void reConnectScanner() {
		comPortController.reconnectPort();
	}
	
	@MessageMapping("/scannerStatus")
	public void scannerStatus() {
		comPortController.scannerStatusQuery();
	}

}
