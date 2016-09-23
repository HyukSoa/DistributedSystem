import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.ArrayList;

public interface TrackerInterface extends Remote {


    public boolean helloWorld(String name) throws RemoteException;


    public ArrayList GetCurrentList(String name) throws RemoteException;


    public int GetKForTracker() throws RemoteException;

    public int GetNForTracker() throws RemoteException;

}