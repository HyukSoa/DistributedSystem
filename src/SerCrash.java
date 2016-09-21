
import java.rmi.RemoteException;

/**
 * Created by huanyuhello on 5/9/2016.
 */
public class SerCrash implements ClientServerInterf {
    @Override
    public MazeAndScore addPlayer(String playerId) throws RemoteException {
        return null;
    }

    @Override
    public MazeAndScore movePosition(String playerId, MoveAction direction) throws RemoteException {
        return null;
    }

    @Override
    public MazeAndScore refreshSate(String playerId) throws RemoteException {
        return null;
    }

    @Override
    public void exitGame(String playerId) throws RemoteException {

    }
}
