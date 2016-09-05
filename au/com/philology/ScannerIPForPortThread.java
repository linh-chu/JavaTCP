package au.com.philology.tcp;

import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

/**
 * A thread to scan open socket server from a list of IP addresses and a given PORT
 */
public class ScannerIPForPortThread extends Thread
{
    int port;
    int ipStart;
    int ipEnd;
    int timeout;
    IScannerDelegate scannerDelegate;

    public ScannerIPForPortThread(int port, int ipStart, int ipEnd,
                                  int timeout, IScannerDelegate scannerDelegate)
    {
        this.port = port;
        this.ipStart = ipStart;
        this.ipEnd = ipEnd;
        this.timeout = timeout;
        this.scannerDelegate = scannerDelegate;
    }

    @Override
    public void run()
    {
        String iIPv4 = TCPUtils.getLocalAddress();
        iIPv4 = iIPv4.substring(0, iIPv4.lastIndexOf("."));
        iIPv4 += ".";

        if (scannerDelegate != null) {
            scannerDelegate.scannerDelegateStartScanningIPForPort();
        }

        int tmpStart = this.ipStart >= 1 && this.ipStart <= 254 ? this.ipStart : 1;
        int tmpEnd = this.ipEnd >= tmpStart && this.ipEnd <= 254 ? this.ipEnd : 254;
        int tmpTimeout = this.timeout >= 10 && this.timeout <= 1000 ? this.timeout : 50;

        for (int i = tmpStart; i <= tmpEnd; i++)
        {
            if (this.isInterrupted()) {
                break;
            }

            Log.d(ScannerIPForPortThread.class.getName(), iIPv4 + i);
            Socket mySocket = new Socket();
            final String ip = iIPv4 + i;
            SocketAddress address = new InetSocketAddress(ip, port);

            if (scannerDelegate != null) {
                scannerDelegate.scannerDelegateIsScanningIPForPort(ip, port);
            }

            try {
                mySocket.connect(address, tmpTimeout);
                mySocket.close();
                if (scannerDelegate != null) {
                    scannerDelegate.scannerDelegateIPFoundForPort(ip, port);
                }

            } catch (IllegalArgumentException e) {
                Log.e(ScannerIPForPortThread.class.getName(), e.getMessage());
            } catch (SocketException e) {
                Log.e(ScannerIPForPortThread.class.getName(), e.getMessage());
            } catch (UnsupportedOperationException e) {
                Log.e(ScannerIPForPortThread.class.getName(), e.getMessage());
            } catch (IOException e) {
                Log.e(ScannerIPForPortThread.class.getName(), e.getMessage());
            }
        }

        if (scannerDelegate != null) {
            scannerDelegate.scannerDelegateCompleteScanningIPForPort();
        }
    }
}