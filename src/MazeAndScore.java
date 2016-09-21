import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Hae on 16/9/7.
 * RMI调用后的返回类,包括maze和score list。
 * 原名为MazeAndPosition,改为MazeAndScore。Client中调用Server RMI部分返回值请相应修改。
 */
public class MazeAndScore implements Serializable {

    private static final long serialVersionUID = 1L;
    private ArrayList PlayerScoreList = new ArrayList();
    private int Maze[][] = new int[15][15];
    //public  int currentPosition[] = new int[2];

    public ArrayList getPlayerScoreList(){
        return PlayerScoreList;
    }

    public void setPlayerScoreList(ArrayList List){
        PlayerScoreList = (ArrayList)List.clone();
    }

    public int[][] getMaze(){
        return Maze;
    }

    public void setMaze(int game[][]){
        Maze = game.clone();
    }

//    public int[] getCurrentPosition(){
//        return currentPosition;
//    }
//
//    public void setCurrentPosition(int position[]){
//        currentPosition = position.clone();
//    }

}
