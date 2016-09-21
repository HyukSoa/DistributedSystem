import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Hae on 16/9/17.
 * Backup Server被调用的RMI函数 进行backup
 */
public interface PrimaryBackupServerInterf extends Remote {

    public boolean regularBackup(MazeAndScore primaryMazeScore) throws RemoteException;

}
