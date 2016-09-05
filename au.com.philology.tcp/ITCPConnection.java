package au.com.philology.tcp;

/**
 * Inform the delegate about connection status
 */
public interface ITCPConnection {

    void tcpConnectionEstablished(String ipAddress, int port);

    void tcpConnectionLost(String ipAddress, int port);

    String tcpConnectionDataReceived(String message, String ipAddress, int port);
}
