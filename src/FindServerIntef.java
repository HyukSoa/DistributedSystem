import java.rmi.Remote;
import java.util.ArrayList;


/**
 * Created by huanyuhello on 5/9/2016.
 */
public interface FindServerIntef extends Remote {

    public String FirstGetPrimServer();
    public String FirstGetBackupServer();

}
