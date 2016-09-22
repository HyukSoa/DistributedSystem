import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
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
        LocateRegistry.createRegistry(7000);//serverIP
        try {
            Naming.rebind("rmi://localhost:7000/"+Client.UserId, backserver);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
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
