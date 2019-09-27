package co.uk.boots.websocket.scanner.com;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fazecast.jSerialComm.SerialPort;

@Component
public class NonBlockingListener {

	private final int OPEN_RETRIES = 10;
	private String name = "TIMEOUT_NON_BLOCKING";
	private SerialPort commPort;
	private boolean keepGoing = false;
	boolean portOpened = false;
	private ScannerStatus scannerStatus;

	public ScannerStatus getScannerStatus() {
		return scannerStatus;
	}

	public boolean isPortOpened() {
		return portOpened;
	}

	public boolean isRunning() {
		return keepGoing;
	}

	@Autowired
	private ComPortController processor;

	private void outputMessage(String message) {
		System.out.println("[COM THREAD] " + message);
	}

	private void openPort(int retries) {
		if (!scannerStatus.isConnected() && keepGoing) {
			outputMessage("Scanning for COM ports.");
			int attempts = 0;
			while (!scannerStatus.isConnected() && attempts < retries) {
				scannerStatus.setAttemptingConnection(true);
				scannerStatus.setStatusMessage("[STATUS] Connection Attempt: " + attempts);
				attempts++;
				processor.process(scannerStatus);
				SerialPort[] ports = SerialPort.getCommPorts();
				if (ports == null || ports.length == 0) {
					attempts = retries;
				} else {
					commPort = ports[0];
					scannerStatus.setConnected(commPort.openPort());
					if (!scannerStatus.isConnected()) {
						try {
							outputMessage("Port Unavailable. Sleeping for 2 seconds.");
							scannerStatus.setStatusMessage("[STATUS] Port Unavailable. Sleeping for 2 seconds.");
							processor.process(scannerStatus);
							Thread.sleep(2000);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			scannerStatus.setAttemptingConnection(false);
			if (attempts == retries) {
				keepGoing = false;
				outputMessage("Port could not be opened in " + attempts + " attempts");
				scannerStatus.setStatusMessage("[STATUS]:Closed");
				processor.process(scannerStatus);
			} else {
				outputMessage("Port Successfully Opened.");
				scannerStatus.setStatusMessage("[STATUS]:Open");
				processor.process(scannerStatus);
			}
		}
	}

	public String getName() {
		return name;
	}

	public void close() {
		keepGoing = false;
	}

	private void closePort() {
		outputMessage("Closing COM Port");
		if (commPort != null) {
			commPort.closePort();
		}
	}
	
	@Async
	public void run() {
		scannerStatus = new ScannerStatus();
		keepGoing = true;
		openPort(OPEN_RETRIES);
		while (keepGoing) {
			StringBuilder builder = new StringBuilder();
			int bytesAvailable = 0;
			while ((bytesAvailable = commPort.bytesAvailable()) == 0 && keepGoing) {
				try {
					// No Data Available - try again in 10 milliseconds
					Thread.sleep(10);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (keepGoing) {
				if (bytesAvailable == -1) {
					scannerStatus.setConnected(false);
					scannerStatus.setStatusMessage("[STATUS] COM Port Error. Reconnecting");
					processor.process(scannerStatus);
					openPort(OPEN_RETRIES);
				} else {
					// Data on the Comm Port
					int bufferCounter = 1; // count for number of reads required for whole data set
					while (commPort.bytesAvailable() > 0) {
						byte[] readBuffer = new byte[commPort.bytesAvailable()];
						int len = commPort.readBytes(readBuffer, readBuffer.length);
						outputMessage("Comm Read Number " + bufferCounter + " was " + len + " bytes");
						outputMessage("Data Read From Port: " + new String(readBuffer));
						builder.append(new String(readBuffer));
						bufferCounter++;
					}
					// no more data on the Comm port
					scannerStatus.setDataMessage(builder.toString());
					processor.process(scannerStatus);
				}
			}
		}
		outputMessage("Thread exiting. Closing Port.");
		closePort();
	}
}
