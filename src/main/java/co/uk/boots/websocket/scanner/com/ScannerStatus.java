package co.uk.boots.websocket.scanner.com;

public class ScannerStatus {
	boolean connected = false;
	public boolean isConnected() {
		return connected;
	}
	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	boolean attemptingConnection = false;
	String statusMessage = "";
	String dataMessage="";
	
	public String getDataMessage() {
		return dataMessage;
	}
	public void setDataMessage(String dataMessage) {
		this.dataMessage = dataMessage;
	}
	public boolean isAttemptingConnection() {
		return attemptingConnection;
	}
	public void setAttemptingConnection(boolean attemptingConnection) {
		this.attemptingConnection = attemptingConnection;
	}
	public String getStatusMessage() {
		return statusMessage;
	}
	public void setStatusMessage(String message) {
		this.statusMessage = message;
	}
	
	
}
