import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by huanyuhello on 21/9/2016.
 */
public interface CallClient extends Remote {

    public boolean Alive() throws RemoteException;

}
