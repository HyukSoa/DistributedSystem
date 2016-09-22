import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by huanyuhello on 22/9/2016.
 */
public class ClientRMI extends UnicastRemoteObject implements ClientRMIInterface {
    GameMsg gameMsg = new GameMsg();
    Server backserver;
    Thread backthread;
    protected ClientRMI() throws RemoteException {
    }

    @Override
    public boolean becomeBackup() throws RemoteException {
        gameMsg.SetisServer(2);
        gameMsg.SetBackupServer(Client.UserId);
        backserver=new Server();
        backthread = new Thread(backserver);
        backthread.start();
        return true;
    }

    @Override
    public String getPrimaryServer() throws RemoteException {
        return gameMsg.GetPrimServer();
    }

    @Override
    public String getBackupServer() throws RemoteException {
        return gameMsg.GetBackupServer();
    }

    @Override
    public boolean callClient() throws RemoteException {
        return true;
    }

    @Override
    public void updateServer(String primaryServerId, String backupServerId) throws RemoteException {
        gameMsg.SetPrimServer(primaryServerId);
        gameMsg.SetBackupServer(backupServerId);
    }
}
