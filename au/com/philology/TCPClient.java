package au.com.philology.tcp;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient implements ITCPClient {

    Socket socket;
    CommunicationThread communicationThread;
    ITCPConnection itcpConnection;

    public TCPClient(ITCPConnection itcpConnection) {
        this.itcpConnection = itcpConnection;
    }

    class ClientThread implements Runnable {
        String serverIPAddress;
        int port;

        public ClientThread(String serverIPAddress, int port) {
            this.serverIPAddress = serverIPAddress;
            this.port = port;
        }

        @Override
        public void run()
        {
            try
            {   // Disconnect the previous connection if having any
                disconnect();

                InetAddress serverAddress = InetAddress.getByName(this.serverIPAddress);

                socket = new Socket(serverAddress, this.port);
                communicationThread = new CommunicationThread(socket, itcpConnection, null);
                communicationThread.start();
            } catch (UnknownHostException e) {
                Log.e(TCPClient.class.getName(), e.getMessage());
            } catch (IOException e) {
                Log.e(TCPClient.class.getName(), e.getMessage());
            }
        }
    }

    /**
     * Send a message to server
     * @param message
     */
    @Override
    public void send(String message) {
        if (this.communicationThread != null) {
            this.communicationThread.dataSending(message);
        }
    }

    /**
     * Send char data to server
     * @param data
     */
    @Override
    public void send(char[] data) {
        if (this.communicationThread != null) {
            this.communicationThread.dataSending(data);
        }
    }

    /**
     * Open a socket connection to server
     * @param serverIPAddress
     * @param port
     */
    @Override
    public void connect(String serverIPAddress, int port) {
        // Disconnect the previous connection if having any
        this.disconnect();
        new Thread(new ClientThread(serverIPAddress, port)).start();
    }

    /**
     * Disconnect to server, close socket
     */
    @Override
    public void disconnect() {
        if (socket != null) {
            try {
                if (socket.isConnected()) {
                    if (communicationThread != null) {
                        communicationThread.interrupt();
                        communicationThread = null;
                    }
                    socket.close();
                    socket = null;
                }
            } catch (IOException e) {
                Log.e(TCPClient.class.getName(), e.getMessage());
            }
        }
    }

    /**
     * Check connection
     * @return
     */
    @Override
    public boolean isConnected() {
        if (socket != null) {
            return socket.isConnected();
        } else {
            return false;
        }
    }

    @Override
    public String getServerIP() {
        if (isConnected()) {
            return communicationThread.remoteIP;
        }
        return null;
    }

    @Override
    public int getServerPort() {
        if (isConnected()) {
            return communicationThread.remotePort;
        }
        return -1;
    }
}
