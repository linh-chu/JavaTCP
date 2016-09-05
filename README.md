# JavaTCP
A set of classes implementing TCP Socket communication.

# TCPServer
Listening to incoming connection requests. Sending and processing the received messages.
```
/**
 * Server's implementations
 */
public interface ITCPServer {

    void broadcast(String message);

    void send(String message, String ipAddress, int port);

    void startListening(int port, ITCPConnection itcpConnection);

    void stopListening();

    boolean isListening();

    List<String> getAllClientIPs();

    List<Integer> getAllClientPorts();
```

# TCPClient 
Connecting to a server. Sending and processing the received messages.
```
/**
 * A client's implementations
 */
public interface ITCPClient {

    void send(String message);

    void send(char[] data);

    void connect(String serverIPAddress, int port);

    void disconnect();

    boolean isConnected();

    String getServerIP();

    int getServerPort();
}
```

# Communication Thread
Handling the communication between clients and server.
```
/**
 * Inform the delegate about communication thread status
 */
public interface ICommunicationThread {

    void communicationStarted(CommunicationThread communicationThread);

    void communicationStopped(CommunicationThread communicationThread);
}
```

