package au.com.philology.tcp;

/**
 * Inform the delegate about scanning status
 */
public interface IScannerDelegate
{
    // IP for port
    void scannerDelegateIsScanningIPForPort(String serverAddress, int port);

    void scannerDelegateIPFoundForPort(String serverAddress, int port);

    void scannerDelegateCompleteScanningIPForPort();

    void scannerDelegateStartScanningIPForPort();

    // Port for IP
    void scannerDelegateStartScanningPortForIP(String ip, int startPort, int endPort);

    void scannerDelegateIsScanningPortForIP(String ip, int port);

    void scannerDelegatePortFoundForIP(String ip, int port);

    void scannerDelegateCompleteScanningPortForIP(String ip, int startPort, int endPort);
}
