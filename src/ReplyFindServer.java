import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by huanyuhello on 5/9/2016.
 */
public class ReplyFindServer extends UnicastRemoteObject implements FindServerIntef {
    GameMsg gameMsg;
    public ReplyFindServer() throws RemoteException {
        //PrimaryServer = new String();
        gameMsg = new GameMsg();
    }

    @Override
    public String FirstGetPrimServer() {
        return gameMsg.GetPrimServer();
    }

    @Override
    public String FirstGetBackupServer() {
        return gameMsg.GetBackupServer();
    }



}
