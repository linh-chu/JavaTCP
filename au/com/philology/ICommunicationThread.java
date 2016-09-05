package au.com.philology.tcp;

/**
 * Inform the delegate about communication thread status
 */
public interface ICommunicationThread {

    void communicationStarted(CommunicationThread communicationThread);

    void communicationStopped(CommunicationThread communicationThread);
}
