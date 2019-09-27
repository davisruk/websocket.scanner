package co.uk.boots.websocket.scanner.com;

public class ScannerStatus {
	boolean portOpened = false;
	boolean attemptingConnection = false;
	String statusMessage = "";
	String dataMessage="";
	
	public String getDataMessage() {
		return dataMessage;
	}
	public void setDataMessage(String dataMessage) {
		this.dataMessage = dataMessage;
	}
	public boolean isPortOpened() {
		return portOpened;
	}
	public void setPortOpened(boolean portOpened) {
		this.portOpened = portOpened;
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
