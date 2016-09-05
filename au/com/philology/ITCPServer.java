package au.com.philology.tcp;

import java.util.List;

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
}
