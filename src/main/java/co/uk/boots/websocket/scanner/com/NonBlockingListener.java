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
				// get list of available COM ports - disconnected ports remain (on windows)
				// connecting the same device results in a new port instance
				SerialPort[] ports = SerialPort.getCommPorts();
				if (ports == null || ports.length == 0) {
					// no ports available - stop connection attempts
					attempts = retries;
				} else {
					int i = 0;
					// loop through each port and attempt a connection
					// end loop on first successful connection
					while (i < ports.length && !scannerStatus.isConnected()) {
						// ports available - try to connect to one
						commPort = ports[i++];
						scannerStatus.setConnected(commPort.openPort());
					}
					if (!scannerStatus.isConnected()) {
						try {
							// connection failed on all available ports - try again
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
			
			if (scannerStatus.isConnected()) {
				outputMessage("Port Successfully Opened.");
				scannerStatus.setStatusMessage("[STATUS]:Open");
				processor.process(scannerStatus);
			} else {
				// failed to open a COM port after a number of retries
				// kill the thread
				keepGoing = false;
				outputMessage("Port could not be opened in " + attempts + " attempts");
				scannerStatus.setStatusMessage("[STATUS]:Closed");
				processor.process(scannerStatus);
			}
		}
	}

	public String getName() {
		return name;
	}

	/* control method for thread - call this to kill it */
	public void close() {
		keepGoing = false;
	}

	private void closePort() {
		outputMessage("Closing COM Port");
		if (commPort != null) {
			commPort.closePort();
		}
	}

	// main loop for thread processing - will continue until keepGoing flag is false
	// can be killed through close() method
	@Async
	public void run() {
		scannerStatus = new ScannerStatus();
		keepGoing = true;
		openPort(OPEN_RETRIES);
		try {
			while (keepGoing) {
				StringBuilder builder = new StringBuilder();
				int bytesAvailable = 0;
				while ((bytesAvailable = commPort.bytesAvailable()) == 0 && keepGoing) {
					// No Data Available - try again in 10 milliseconds
					Thread.sleep(10);
				}

				if (keepGoing) {
					if (bytesAvailable == -1) {
						// COM port reports connection error
						scannerStatus.setConnected(false);
						scannerStatus.setStatusMessage("[STATUS] COM Port Error. Reconnecting");
						processor.process(scannerStatus);
						openPort(OPEN_RETRIES);
					} else {
						int bufferCounter = 1;
						while (commPort.bytesAvailable() > 0) {
							// COM port has data available
							// Allow slow devices some more time to fill the buffer
							Thread.sleep(5);

							// Now read the current data from the COM port
							byte[] readBuffer = new byte[commPort.bytesAvailable()];
							int len = commPort.readBytes(readBuffer, readBuffer.length);
							outputMessage("Comm Read Number " + bufferCounter + " was " + len + " bytes");
							outputMessage("Data Read From Port: " + new String(readBuffer));

							// build the data being read
							builder.append(new String(readBuffer));
							bufferCounter++;
							
							// some devices are too slow to send all data
							// we are about to check the port for available bytes again
							// but some devices will not have placed any data on the port
							// give them a chance to do so by sleeping again
							Thread.sleep(5);							
						}
						
						// no more data on the Comm port
						scannerStatus.setDataMessage(builder.toString());
						processor.process(scannerStatus);
					}
				}
			}
			outputMessage("Thread exiting. Closing Port.");
			closePort();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
