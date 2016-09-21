import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by huanyuhello on 5/9/2016.
 */
public class HandPeerCall extends UnicastRemoteObject implements PeerInterface {
    protected HandPeerCall() throws RemoteException {
    }
}
