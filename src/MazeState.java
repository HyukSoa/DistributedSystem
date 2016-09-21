import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Hae on 16/9/7.
 * TODO: 是否更适合写为inner class
 * 这一部分是ServerRMI多线程处理中mutex部分 需要锁住 故单独提取出来。
 * PlayerScoreList中存数据的顺序:PlayerID, Score, PlayerNumberId
 * Maze[][]: 0为空, -1为有treasure, 1及以上为PlayerNumberId
 */
public class MazeState {
    public static int Maze[][] = new int[15][15];
    public static ArrayList PlayerScoreList = new ArrayList();

    public ArrayList GetPlayerScoreList(){return PlayerScoreList;}

    public void SetPlayerScoreList(ArrayList list){ PlayerScoreList = (ArrayList) list.clone(); }

    public static int[][] GetMaze() { return Maze; }

    public static void SetMaze(int game[][]){ Maze = game.clone();}
}
