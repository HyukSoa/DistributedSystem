import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RemoteObject;
import java.util.ArrayList;

/**
 * Created by huanyuhello on 5/9/2016.
 * dis: 用于client联系tracker的方法
 *
 */
public interface TrackerInterface  extends Remote{
    public boolean helloWorld(String name) throws RemoteException;

    public ArrayList GetCurrentList(String name) throws RemoteException;

    public int GetKForTracker() throws RemoteException;

    public int GetNForTracker() throws RemoteException;

}
