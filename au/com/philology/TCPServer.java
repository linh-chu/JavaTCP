package au.com.philology.tcp;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TCPServer implements ITCPServer, ICommunicationThread {

    ArrayList<CommunicationThread> allCommunicationThreads = new ArrayList<>();
    ServerSocket serverSocket;
    ITCPConnection itcpConnection;
    public int port;

    /**
     * A runnable opening a listening server socket
     */
    class ServerThread implements Runnable {
        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(port);

                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        CommunicationThread thread = new CommunicationThread(clientSocket,
                                itcpConnection, TCPServer.this);
                        thread.start();
                    } catch (IOException e) {
                        Log.e(TCPServer.class.getName(), e.getMessage());
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*-------------------------------------------*/
    /* ITCPServer Methods */
    /*-------------------------------------------*/
    @Override
    public void broadcast(String message) {
        synchronized (allCommunicationThreads) {
            for (CommunicationThread thread : allCommunicationThreads) {
                thread.dataSending(message);
            }
        }
    }

    /**
     * Send data to a client
     * @param message
     * @param ipAddress
     * @param port
     */
    @Override
    public void send(String message, String ipAddress, int port) {
        synchronized (allCommunicationThreads) {
            for (CommunicationThread thread : allCommunicationThreads) {
                if (thread.remoteIP.equals(ipAddress) && thread.remotePort == port) {
                    thread.dataSending(message);
                    break;
                }
            }
        }
    }

    /**
     * Server open a socket listening
     * @param port
     * @param itcpConnection
     */
    @Override
    public void startListening(int port, ITCPConnection itcpConnection) {
        stopListening();

        this.port = port;
        this.itcpConnection = itcpConnection;
        new Thread(new ServerThread()).start();
    }

    /**
     * Server stops listening
     */
    @Override
    public void stopListening() {
        synchronized (allCommunicationThreads) {
            // Close all the currently communicating sockets
            for (CommunicationThread thread : allCommunicationThreads) {
                try {
                    if (thread.socket != null) {
                        if (thread.socket.isConnected()) {
                            thread.socket.close();
                            thread.socket = null;
                        }
                    }
                } catch (IOException e) {
                    Log.e(TCPServer.class.getName(), e.getMessage());
                }
            }
            allCommunicationThreads.clear();

            // Close server socket
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                    serverSocket = null;
                }
            } catch (IOException e) {
                Log.e(TCPServer.class.getName(), e.getMessage());
            }
        }
    }

    /**
     * Check is server listening
     * @return
     */
    @Override
    public boolean isListening() {
        if (serverSocket != null) {
            return !serverSocket.isClosed();
        }
        return false;
    }

    @Override
    public List<String> getAllClientIPs() {
        List<String> allClients = new ArrayList<String>();

        synchronized (allCommunicationThreads) {
            for (int i = 0; i < allCommunicationThreads.size(); i++) {
                CommunicationThread thread = allCommunicationThreads.get(i);
                if (thread.isAlive()) {
                    allClients.add(thread.remoteIP);
                }
            }
        }

        return allClients;
    }

    @Override
    public List<Integer> getAllClientPorts() {
        List<Integer> allClients = new ArrayList<Integer>();

        synchronized (allCommunicationThreads) {
            for (int i = 0; i < allCommunicationThreads.size(); i++) {
                CommunicationThread thread = allCommunicationThreads.get(i);
                if (thread.isAlive()) {
                    allClients.add(thread.remotePort);
                }
            }
        }

        return allClients;
    }

    /*-------------------------------------------*/
    /* ICommunicationThread Methods */
    /*-------------------------------------------*/
    @Override
    public void communicationStarted(CommunicationThread communicationThread) {
        synchronized (allCommunicationThreads) {
            if (!allCommunicationThreads.contains(communicationThread)) {
                allCommunicationThreads.add(communicationThread);
            }
        }
    }

    @Override
    public void communicationStopped(CommunicationThread communicationThread) {
        synchronized (allCommunicationThreads) {
            if (allCommunicationThreads.contains(communicationThread)) {
                allCommunicationThreads.remove(communicationThread);
            }
        }
    }
}
