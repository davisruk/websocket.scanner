package co.uk.boots.websocket.scanner.com;

import javax.annotation.PreDestroy;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fazecast.jSerialComm.SerialPort;

@Component
@Scope("prototype")
public class NonBlockingListener {

	private final int OPEN_RETRIES = 10;
	private String name = "TIMEOUT_NON_BLOCKING";
	private SerialPort commPort;
	private ComPortDataProcessor processor;
	private boolean keepGoing = true;
	
	public NonBlockingListener(ComPortDataProcessor processor) {
		this.processor = processor;
		run();
	}
	
	private void outputMessage (String message) {
		System.out.println ("[COM THREAD] " + message);
	}
	
	private void openPort(int retries) {
		boolean portOpened = false;
		outputMessage ("Scanning for COM ports.");
		int attempts = 0;
		while (!portOpened && attempts < retries) {
			attempts++;
			processor.process ("Connection Attempt: " + attempts);
			commPort = SerialPort.getCommPorts()[0];
			portOpened = commPort.openPort();
			if (!portOpened) {
				try {
					outputMessage ("Port Unavailable. Sleeping for 2 seconds.");
					processor.process("Port Unavailable. Sleeping for 2 seconds.");
					Thread.sleep(2000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		if (attempts == retries) {
			keepGoing = false;
			outputMessage ("Port could not be opened in " + attempts + " attempts");
			processor.process ("COM thread reports no available ports found.");
		} else {
			outputMessage ("Port Successfully Opened.");
			processor.process ("COM thread reports port opened successfully.");
		}
	}
	
	public String getName() {
		return name;
	}

	@PreDestroy
	public void close() {
		outputMessage("Closing COM Port");
		commPort.closePort();
	}

	public void run() {
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
	
			if (bytesAvailable == -1) {
				processor.process("COM Port Error. Reconnecting");
				openPort(OPEN_RETRIES);
			} else if (keepGoing) {
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
				processor.process(builder.toString());
			}
		}
		outputMessage ("Thread exiting. Closing Port.");
		commPort.closePort();
	}
}
