import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

import static java.lang.Thread.sleep;

/**
 * Created by huanyuhello on 5/9/2016.
 * Server.java
 */
public class Server extends UnicastRemoteObject implements ClientServerInterf, Runnable {

    private GameMsg gameMsg = new GameMsg();
    private Random random = new Random();
    private ClientServerInterf csi = null;
    private HashMap<String, ClientRMIInterface> cri = null;

    private String clientPrefix = "rmi://localhost:8000/";
    private String serverPrefix = "rmi://localhost:7000/";

    /**
     * 必须定义构造方法，即使是默认构造方法，也必须把它明确地写出来，因为它必须抛出RemoteException异常
     */
    public Server() throws RemoteException{
    }

    /**
     * 玩家加入游戏的处理。不用处理第一个玩家加入游戏的情况; 处理了第二个玩家加入游戏。
     */
    public MazeAndScore addPlayer(String playerId) throws RemoteException{
        int[][] localMaze;
        ArrayList localPlayerScoreList;
        int playerCount, mazeLength;
        int[] localPlayerPos ;
        MazeAndScore returnValue = new MazeAndScore(); //返回值

        synchronized (gameMsg.mazeState){
            localMaze = GameMsg.mazeState.GetMaze().clone();
            localPlayerScoreList = (ArrayList) gameMsg.mazeState.GetPlayerScoreList().clone();
            playerCount = localPlayerScoreList.size() / 3;
            mazeLength = localMaze.length;

            //1.非常规处理
            //检测是否是第二个用户
            if (playerCount == 1){
                /**
                 * TODO
                 * call become-backup
                 */
            }
            //检测是否超过游戏棋盘大小
            if (mazeLength > 15){
                /**
                 * TODO: 此处if条件判断不对
                 * can't join game
                 */
            }

            //2.random position
            localPlayerPos = randomNewPosition(localMaze);

            //3.update maze
            localPlayerScoreList.add(playerId);
            localPlayerScoreList.add(0);//initial score
            int numberIdCount = (Integer)localPlayerScoreList.get(playerCount * 3 - 1) + 1;
            localPlayerScoreList.add(numberIdCount);//number id of user
            localMaze[localPlayerPos[0]][localPlayerPos[1]] = numberIdCount;

            gameMsg.mazeState.SetMaze(localMaze);
            gameMsg.mazeState.SetPlayerScoreList(localPlayerScoreList);

        }//释放锁

        returnValue.setPlayerScoreList(localPlayerScoreList);
        returnValue.setMaze(localMaze);
        //returnValue.setCurrentPosition(localPlayerPos);
        return returnValue;
    }

    /**
     * 玩家移动位置的处理。
     */
    public MazeAndScore movePosition(String playerId, MoveAction direction) throws RemoteException{
        int[][] localMaze;
        int[] localPlayerPos = new int[2];
        int[] newTreasurePos = new int[2];
        ArrayList localPlayerScoreList;
        int mazeLength, playerNumId;
        MazeAndScore returnValue = new MazeAndScore();

        synchronized (gameMsg.mazeState){
            localMaze = gameMsg.mazeState.GetMaze().clone();
            localPlayerScoreList = (ArrayList) gameMsg.mazeState.GetPlayerScoreList().clone();
            mazeLength = localMaze.length;
            playerNumId = (Integer) localPlayerScoreList.get(localPlayerScoreList.indexOf(playerId) + 2);

            findPositionLoop:
            for (int row = 0; row < mazeLength; row++){
                for (int column = 0; column < mazeLength; column++){
                    if (localMaze[row][column] == playerNumId){
                        localPlayerPos[0] = row;
                        localPlayerPos[1] = column;
                        break findPositionLoop;
                    }
                }
            }

            switch (direction){
                case goUp:
                    if ((localPlayerPos[0] != 0) // first row, can't go up
                            && (localMaze[localPlayerPos[0]-1][localPlayerPos[1]] < 1)){ // position is empty
                        localMaze[localPlayerPos[0]][localPlayerPos[1]] = 0;
                        localPlayerPos[0]--;//移动位置
                    }
                    break;
                case goDown:
                    if ((localPlayerPos[0] != 14)
                            && (localMaze[localPlayerPos[0]+1][localPlayerPos[1]] < 1)){
                        localMaze[localPlayerPos[0]][localPlayerPos[1]] = 0;
                        localPlayerPos[0]++;
                    }
                    break;
                case goLeft:
                    if ((localPlayerPos[1] != 0)
                            && (localMaze[localPlayerPos[0]][localPlayerPos[1]-1] < 1)){
                        localMaze[localPlayerPos[0]][localPlayerPos[1]] = 0;
                        localPlayerPos[1]--;
                    }
                    break;
                case goRight:
                    if ((localPlayerPos[1] != 14)
                            && (localMaze[localPlayerPos[0]][localPlayerPos[1]+1] < 1)){
                        localMaze[localPlayerPos[0]][localPlayerPos[1]] = 0;
                        localPlayerPos[1]++;
                    }
                    break;
            }

            if (localMaze[localPlayerPos[0]][localPlayerPos[1]] == -1){//treasure
                newTreasurePos = randomNewPosition(localMaze);
                localMaze[newTreasurePos[0]][newTreasurePos[1]] = -1;
                int currentScore = (Integer) localPlayerScoreList.get(localPlayerScoreList.indexOf(playerId)+1);
                localPlayerScoreList.set(localPlayerScoreList.indexOf(playerId)+1,currentScore+1);
            }
            //set current position
            localMaze[localPlayerPos[0]][localPlayerPos[1]] = playerNumId;

            //save to gameMsg
            gameMsg.mazeState.SetPlayerScoreList(localPlayerScoreList);
            gameMsg.mazeState.SetMaze(localMaze);

        }//释放锁
        //returnValue.setCurrentPosition(localPlayerPos);
        returnValue.setMaze(localMaze);
        returnValue.setPlayerScoreList(localPlayerScoreList);
        return returnValue;
    }

    /**
     * 玩家刷新信息的处理。
     */
    public MazeAndScore refreshSate(String playerId) throws RemoteException{
        int[][] localMaze;
        ArrayList localPlayerScoreList;
        MazeAndScore returnValue = new MazeAndScore();

        synchronized (gameMsg.mazeState) {
            localMaze = gameMsg.mazeState.GetMaze().clone();
            localPlayerScoreList = (ArrayList) gameMsg.mazeState.GetPlayerScoreList().clone();
        }//取信息 原子操作

        returnValue.setMaze(localMaze);
        returnValue.setPlayerScoreList(localPlayerScoreList);
        return returnValue;
    }

    /**
     * 玩家退出游戏的处理。
     */
    public void exitGame(String playerId) throws RemoteException{
        int[][] localMaze;
        ArrayList localPlayerScoreList;
        int playerIdIndex, playerNumId, mazeLength;
        int[] localPlayerPos = new int[2];

        synchronized (gameMsg.mazeState){
            localMaze = gameMsg.mazeState.GetMaze().clone();
            localPlayerScoreList = (ArrayList) gameMsg.mazeState.GetPlayerScoreList().clone();
            playerIdIndex = localPlayerScoreList.indexOf(playerId);
            playerNumId = (Integer)localPlayerScoreList.get(playerIdIndex+2);
            mazeLength = localMaze.length;

            findPositionLoop:
            for (int row = 0; row < mazeLength; row++){
                for (int column = 0; column < mazeLength; column++){
                    if (localMaze[row][column] == playerNumId){
                        localPlayerPos[0] = row;
                        localPlayerPos[1] = column;
                        break findPositionLoop;
                    }
                }
            }

            //delete the player from maze
            localMaze[localPlayerPos[0]][localPlayerPos[1]] = 0;
            //delete the player from score list
            for (int countThree = 0; countThree < 3; countThree++) {
                localPlayerScoreList.remove(playerIdIndex);
            }
            //save to GameMsg
            gameMsg.mazeState.SetMaze(localMaze);
            gameMsg.mazeState.SetPlayerScoreList(localPlayerScoreList);
        }

    }

    /**
     * 作为backup server被调用的函数。在backup server上进行数据备份。
     */
    public boolean regularBackup(MazeAndScore primaryMazeScore) throws RemoteException{
        int[][] localMaze = primaryMazeScore.getMaze().clone();
        ArrayList localPlayerScoreList = (ArrayList) primaryMazeScore.getPlayerScoreList().clone();

        synchronized (gameMsg.mazeState){
            gameMsg.mazeState.SetMaze(localMaze);
            gameMsg.mazeState.SetPlayerScoreList(localPlayerScoreList);
        }
        return true;
    }

    /**
     * 作为primary server,调用backup server进行备份。
     * TODO 需要等GameMsg中的BackupServer变量更新完成
     */
    public boolean callRMIBackup(){

        return true;
    }


    private int[] randomNewPosition(int[][] localMaze){
        int[] randomPos = new int[2];
        int mazeLength = localMaze.length;

        randomPos[0] = random.nextInt(mazeLength);
        randomPos[1] = random.nextInt(mazeLength);

        while (0 != localMaze[randomPos[0]][randomPos[1]]){
            randomPos[0] = random.nextInt(mazeLength);
            randomPos[1] = random.nextInt(mazeLength);
        }
        return randomPos;
    }

    /**
     * @param newBackup The player id of chosen player to be backup.
     * @return Call its becomeBackup, call regularBackup, set my BackupServer field.
     */
    private boolean genBackup(String newBackup) {
        ClientRMIInterface crmi;
        try {
            crmi = (ClientRMIInterface) Naming.lookup(clientPrefix + newBackup);
            crmi.becomeBackup();
            csi = (ClientServerInterf) Naming.lookup(serverPrefix + newBackup);
            csi.regularBackup(refreshSate(gameMsg.GetUserName()));
            gameMsg.SetBackupServer(newBackup);
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Call every player in the player score list,
     * if remote exception occurs with player p, call exitGame(p).
     * @return all alive players at this moment, NOT including *THIS*
     */
    private HashSet<String> getAllAlive() {
        ArrayList localPlayerScoreList = (ArrayList) gameMsg.mazeState.GetPlayerScoreList().clone();
        HashSet<String> alivePlayers = new HashSet<>();
        for (int i = 0; i < localPlayerScoreList.size(); i += 3) {
            String playerId = (String) localPlayerScoreList.get(i);
            if (playerId.equals(gameMsg.GetUserName()))  continue;
            try {
                ClientRMIInterface crmi = (ClientRMIInterface) Naming.lookup(clientPrefix + playerId);
                if (crmi != null)    crmi.callClient();
                // if no exception occurs, add this player to recentPlayers.
                alivePlayers.add(playerId);
            } catch (NotBoundException | MalformedURLException | RemoteException e) {  // if it's not available, then:
                e.printStackTrace();
                try {
                    exitGame(playerId);
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return alivePlayers;
    }

    /**
     * @param alivePlayers just checked all alive players (other than myself)
     * @param ps primary server player id
     * @param bs backup server player id
     * @return Update ps and bs to all alive players
     */
    private boolean broadcastServers(HashSet<String> alivePlayers, String ps, String bs) {
        Iterator<String> it = alivePlayers.iterator();
        String playerId;
        while (it.hasNext()) {
            playerId = it.next();
            ClientRMIInterface crmi = null;
            try {
                crmi = (ClientRMIInterface) Naming.lookup(clientPrefix + playerId);
                if (crmi != null)    crmi.updateServer(ps, bs);
            } catch (NotBoundException | MalformedURLException | RemoteException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * If there is only one player, just set its backup server as null;
     * else, generate new backup and broadcast ps and bs.
     */
    private void handleBackupFail() {
        HashSet<String> alivePlayers = getAllAlive();
        if (alivePlayers.isEmpty()) {  // only one player
            gameMsg.SetBackupServer("");
        } else {
            String newBackup = alivePlayers.iterator().next();
            genBackup(newBackup);
            broadcastServers(alivePlayers, gameMsg.GetUserName(), newBackup);
        }
    }

    @Override
    public void run() {

        int timeout = 200;

        while (true) {
            if (gameMsg.GetIsServer() == 1){  // primary server
                // assert gameMsg
                HashSet<String> alivePlayers = getAllAlive();
                String bs = gameMsg.GetBackupServer();
                if (bs.equals("") || !alivePlayers.contains(bs)) {  // there is no backup server
                    handleBackupFail();
                }
                try {
                    sleep(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } else {  // backup server

                String ps = gameMsg.GetPrimServer();
                try {
                    ClientRMIInterface cri = (ClientRMIInterface) Naming.lookup(serverPrefix + ps);
                    cri.callClient();
                } catch (NotBoundException | MalformedURLException | RemoteException e) {  // primary server fails
                    // turn myself to primary server
                    String me = gameMsg.GetBackupServer();
                    assert me.equals(gameMsg.GetUserName());
                    gameMsg.SetPrimServer(me);
                    gameMsg.SetisServer(1);

                    // designate another backup server (same to backup fail)
                    HashSet<String> alivePlayers = getAllAlive();
                    assert !alivePlayers.contains(ps);
                    handleBackupFail();

                    e.printStackTrace();
                }
                try {
                    sleep(timeout/10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}






