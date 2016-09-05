package au.com.philology.tcp;

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
