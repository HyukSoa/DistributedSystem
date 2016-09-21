import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by huanyuhello on 5/9/2016.
 */
public class GameMsg {

    public static String TrackerIP;
    public static String TrackerPort;
    public static int N_num;
    public static int K_num;
    public static ArrayList UserList = new ArrayList();
//    public static ArrayList PlayerScoreList = new ArrayList();
    public static String PrimaryServer = null;
    public static String BackupServer = null;
//    public static int Maze[][] = new int[50][50];
    public static int POS[] = new int[2];
    public static String Port = "8888";
    public static int ServerFlag = 0;//0:client 1:primary server 2:backup server
    public static InetAddress addr;
    public static String UserName;
    public static MazeState mazeState = new MazeState();

    public String GetTrackerIP(){ return TrackerIP;}

    public String GetTrackerPort(){ return TrackerPort;}

    public int GetN_num()
    {
        return N_num;
    }

    public int GetK_Num()
    {
        return K_num;
    }

    public void SetN_num(int N)
    {
        N_num = N;
    }

    public void SetK_Num(int K)
    {
        K_num = K;
    }

    public ArrayList GetUserList() {  return UserList;  }

    public void SetUserList(ArrayList List) { UserList = (ArrayList) List.clone(); }

//    public ArrayList GetPlayerScoreList(){return PlayerScoreList;}

//    public void SetPlayerScoreList(ArrayList list){ PlayerScoreList = (ArrayList) list.clone(); }

//    public int[][] GetMaze() { return Maze; }/* Serialization */

//    public void SetMaze(int game[][]){ Maze = game.clone();}

    public String GetPrimServer() { return PrimaryServer;    }

    public String GetBackupServer() {
        return BackupServer;
    }

    public void SetPrimServer(String string) {
        PrimaryServer = string.toString();
    }

    public void SetBackupServer(String string) {
        BackupServer = string.toString();
    }

    public void SetPosX(int X){
        POS[0] = X;
    }

    public void SetPosY(int Y){
        POS[1] = Y;
    }

    public void SetPos(int[] XY){ POS = XY.clone(); }

    public int GetPosX(){return POS[0];}

    public int GetPosY(){return POS[1];}

    public int[] GetPos() { return POS; }

    public String GetLocalHost() throws UnknownHostException {
        /**获取此 IP 地址的主机名。*/
        addr = InetAddress.getLocalHost();
        return addr.getHostName();
    }

    public String GetPotNum()
    {
        return Port;
    }

    public int GetIsServer(){ return ServerFlag;}

    public void SetisServer(int change){ ServerFlag = change; }

    public void SetUserName(String Name){ UserName = Name.toString();    }

    public String GetUserName(){ return  UserName; }
}
