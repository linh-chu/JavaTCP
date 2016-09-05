package au.com.philology.tcp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A thread for communicating between server and a client
 */
public class CommunicationThread extends Thread {

    ITCPConnection itcpConnection;
    ICommunicationThread iCommunicationThread;
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    int remotePort;
    String remoteIP;

    public CommunicationThread(Socket socket,
                               ITCPConnection itcpConnection,
                               ICommunicationThread iCommunicationThread) {
        this.socket = socket;
        this.iCommunicationThread = iCommunicationThread;
        this.itcpConnection = itcpConnection;
        // Get IP and Port of the communicating socket
        String[] remoteAddress = this.socket.getRemoteSocketAddress().toString().split(":");
        this.remoteIP = remoteAddress[0].split("/")[1];
        this.remotePort = Integer.parseInt(remoteAddress[1]);
        try {
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(this.socket.getOutputStream())), true);
        } catch (IOException e) {
            Log.e(CommunicationThread.class.getName(), e.getMessage());
        }
    }

    /**
     * Read and process coming data
     */
    @Override
    public void run() {
        if (itcpConnection != null)
            itcpConnection.tcpConnectionEstablished(remoteIP, remotePort);

        if (iCommunicationThread != null)
            iCommunicationThread.communicationStarted(this);

        while (!Thread.currentThread().isInterrupted()) {
            try {
                String inputLine = this.in.readLine();
                if (inputLine != null) {
                    if (!inputLine.trim().equals("")) {
                        if (itcpConnection != null) {
                            // Call dataReceived method
                            String response = itcpConnection.tcpConnectionDataReceived(inputLine,
                                    this.remoteIP, this.remotePort);
                            // Respond back to the sender
                            dataSending(response);
                        }
                    }
                } else {
                    if (this.socket != null) this.socket.close();
                    break;
                }
            } catch (IOException e) {
                Log.e(CommunicationThread.class.getName(), e.getMessage());
            }
        }

        if (iCommunicationThread != null)
            iCommunicationThread.communicationStopped(this);

        if (itcpConnection != null)
            itcpConnection.tcpConnectionLost(remoteIP, remotePort);
    }

    /**
     * Sending a message
     * @param message
     */
    public void dataSending(String message)
    {
        this.out.write(message + "\n");
        this.out.flush();
    }

    /**
     * Sending data
     * @param data
     */
    public void dataSending(char[] data)
    {
        this.out.write(data);
        this.out.flush();
    }
}
