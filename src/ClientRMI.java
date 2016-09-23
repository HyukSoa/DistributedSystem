import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by huanyuhello on 22/9/2016.
 */
public class ClientRMI extends UnicastRemoteObject implements ClientRMIInterface {
    GameMsg gameMsg = new GameMsg();
    Server backserver;
    Thread backthread;
    protected ClientRMI() throws RemoteException {
    }

    /**
     * @param ps primary server player id
     * @param bs backup server player id
     * @return Update ps and bs to all alive players
     */
    private boolean broadcastServers(String ps, String bs) {
        String playerId;
        ArrayList localPlayerScoreList = (ArrayList) gameMsg.mazeState.GetPlayerScoreList().clone();
        for (int i=0; i<localPlayerScoreList.size(); i+=3) {
            playerId = localPlayerScoreList.get(i).toString();
            if (!Server.cri.containsKey(playerId)) {
                try {
                    Registry rg = LocateRegistry.getRegistry();
                    ClientRMIInterface clientRmiI = (ClientRMIInterface) rg.lookup("rmi://localhost/"+playerId);
                    Server.cri.put(playerId, clientRmiI);
                } catch (RemoteException | NotBoundException e) {
                    e.printStackTrace();
                }

            }
            ClientRMIInterface crmi = Server.cri.get(playerId);

            try {
                crmi.updateServer(ps, bs);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public boolean becomeBackup() throws RemoteException {
        gameMsg.SetisServer(2);
        gameMsg.SetBackupServer(Client.UserId);
        backserver=new Server();
//        ClientRMIInterface csi = (ClientRMIInterface) UnicastRemoteObject.exportObject(backserver, 0);
        Registry rg = LocateRegistry.getRegistry();//serverIP

        try {
            rg.bind("rmi://localhost/server"+Client.UserId, backserver);
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }

        backthread = new Thread(backserver);
        backthread.start();
        broadcastServers(getPrimaryServer(), getBackupServer());
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
