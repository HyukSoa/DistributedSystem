import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Hae on 16/9/7.
 */
public interface ClientServerInterf extends Remote {
    public MazeAndScore addPlayer(String playerId) throws RemoteException;
    public MazeAndScore movePosition(String playerId, MoveAction direction) throws RemoteException; // 1,2,3,4
    public MazeAndScore refreshSate(String playerId) throws RemoteException; // 0
    public void exitGame(String playerId) throws RemoteException; // 9
    /**Backup Server被调用的RMI函数 进行backup. 原PrimaryBackupServerInterf */
    public boolean regularBackup(MazeAndScore primaryMazeScore) throws RemoteException;
}
