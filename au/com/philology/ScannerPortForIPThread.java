package au.com.philology.tcp;

import android.os.Debug;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;


/**
 * A thread to scan open socket server from a list of ports and a given IP addresses
 */
public class ScannerPortForIPThread extends Thread
{
	String ip;
	int startPort;
	int endPort;
	int timeout;
	IScannerDelegate scannerDelegate;

	public ScannerPortForIPThread(String ip, int timeout, int startPort, int endPort, IScannerDelegate scanDelegate)
	{
		this.ip = ip;
		this.startPort = startPort;
		this.endPort = endPort;
		this.timeout = timeout;
		this.scannerDelegate = scanDelegate;
	}

	@Override
	public void run()
	{
		int tmpStart = this.startPort >= 0 && this.startPort <= 255 * 255 ? this.startPort : 0;
		int tmpEnd = this.endPort >= tmpStart && this.endPort <= 255 * 255 ? this.endPort : 255 * 255;
		int tmpTimeout = this.timeout >= 10 && this.timeout <= 1000 ? this.timeout : 50;

		if (scannerDelegate != null) {
            scannerDelegate.scannerDelegateStartScanningPortForIP(ip, tmpStart, tmpEnd);
		}

		for (int i = tmpStart; i <= tmpEnd; i++) {
			if (this.isInterrupted()) {
				break;
			}

			Socket mySocket = new Socket();
			SocketAddress address = new InetSocketAddress(ip, i);

			try {
				if (scannerDelegate != null) {
                    scannerDelegate.scannerDelegateIsScanningPortForIP(ip, i);
				}

				mySocket.connect(address, tmpTimeout);
				mySocket.close();

				if (scannerDelegate != null) {
                    scannerDelegate.scannerDelegatePortFoundForIP(ip, i);
				}

			} catch (IllegalArgumentException e) {
                Log.e(ScannerPortForIPThread.class.getName(), e.getMessage());
			} catch (SocketException e) {
                Log.e(ScannerPortForIPThread.class.getName(), e.getMessage());
			} catch (UnsupportedOperationException e) {
                Log.e(ScannerPortForIPThread.class.getName(), e.getMessage());
			} catch (IOException e) {
                Log.e(ScannerPortForIPThread.class.getName(), e.getMessage());
			}
		}

		if (scannerDelegate != null) {
            scannerDelegate.scannerDelegateCompleteScanningPortForIP(ip, tmpStart, tmpEnd);
		}
	}
}