package co.uk.boots.websocket.scanner.websocket;

import co.uk.boots.websocket.scanner.com.ScannerStatus;

public interface WebSocketScanSender {
	public void send (ScannerStatus status);
}
