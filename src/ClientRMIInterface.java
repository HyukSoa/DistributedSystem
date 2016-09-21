import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Hae on 16/9/21.
 * Client提供的RMI函数
 */
public interface ClientRMIInterface extends Remote{

    /** becomeBackup */
    public boolean becomeBackup() throws RemoteException;

    /** getPrimaryServer */
    public String getPrimaryServer() throws RemoteException;

    /** getBackupServer */
    public String getBackupServer() throws RemoteException;

    /** callClient*/
    public boolean callClient() throws RemoteException;

    /** updateServer */
    public void updateServer(String primaryServerId, String backupServerId) throws RemoteException;

}
