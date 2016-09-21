import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by huanyuhello on 5/9/2016.
 */
public class Client {
    int Maze[][] = new int[20][20];
    //MazeState mazeState = new MazeState();
    GameMsg gameMsg = new GameMsg();
    MazeState mazeState = new MazeState();
    ClientServerInterf clientServer;
    Server Server;
    GUI gui;
    int JoinState = 0;
    boolean IsGoing = true;
    ArrayList Score = new ArrayList();
    MazeAndScore JoinUp;
    //MazeState updateData;
    ClientServerInterf clientServerInterf;
    String UserId =new String();
    FindServerIntef findServerIntef;
    public Client() {


        try {
            //在RMI服务注册表中查找名称为Trakcer的对象，并调用其上的方法
            TrackerInterface Tracker = (TrackerInterface) Naming.lookup("rmi://localhost:8888/Tracker");
            BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
            boolean judege = false;
            while (!judege) {
                UserId = buf.readLine();

                if (Tracker.helloWorld(UserId) == true) {
                    judege = true;
                } else {
                    System.out.println("error wrong name");
                }

            }
            System.out.println("Set name Successful");
            gameMsg.SetUserName(UserId);
            System.out.println(UserId);
            gameMsg.SetUserList(Tracker.GetCurrentList(UserId));
            System.out.println(Tracker.GetCurrentList(UserId).toString());
            gameMsg.SetN_num(Tracker.GetNForTracker());
            System.out.println(Tracker.GetNForTracker());
            gameMsg.SetK_Num(Tracker.GetNForTracker());
            System.out.println(Tracker.GetNForTracker());

            ArrayList arrayList = gameMsg.GetUserList();
            //在RMI服务注册表中查找名称为Trakcer的对象，并调用其上的方法
            //System.out.println(arrayList.toString());

            if (arrayList.size() == 2) {
                JoinState = 1;
            } else {

                for (int index = 0; index < arrayList.size(); index++) {

                    findServerIntef = (FindServerIntef) Naming.lookup("rmi://" + arrayList.get(index).toString() + "/find");
                    System.out.println(findServerIntef);
                    if ((findServerIntef.FirstGetPrimServer() != null) && (findServerIntef.FirstGetBackupServer() != null)) {
                        gameMsg.SetisServer(0);
                        JoinState = 3;
                        break;
                    } else if ((findServerIntef.FirstGetBackupServer() == null) && (findServerIntef.FirstGetBackupServer() != null)) {

                        JoinState = 2;
                        break;
                    } else {
                        gameMsg.SetisServer(1);
                        JoinState = 1;
                    }
                }
            }
            switch (JoinState) {
                case 1:
                    InitMaze();
                    Server = new Server();
                    gameMsg.SetPrimServer(gameMsg.GetLocalHost());
                    System.out.println(JoinState);
                    break;
                case 2:
                    JoinUp = clientServerInterf.addPlayer(gameMsg.GetUserName());
                    Maze = JoinUp.getMaze().clone();
                    break;
                case 3:
                    JoinUp = clientServerInterf.addPlayer(gameMsg.GetUserName());
                    Maze = JoinUp.getMaze().clone();
                    break;
                default:
                    break;
            }


        } catch (NotBoundException e) {

        } catch (MalformedURLException e) {

        } catch (RemoteException e) {

        } catch (UnknownHostException e) {

        } catch (ConnectException e) {

        } catch (IOException e) {

        }
    }


    public void InitGame(){

        int UserNum = 0;
        Maze = gameMsg.mazeState.GetMaze().clone();
        Score = (ArrayList) gameMsg.mazeState.GetPlayerScoreList().clone();
        for(int i =0;i<Score.size();i++)
        {
            if(Score.get(i).equals(UserId)) {
                UserNum = (int) Score.get(i + 2);
                break;
            }
        }
        for (int i = 0; i < Maze.length; i++) {
            for (int j = 0; j < Maze[i].length; j++) {
                if (Maze[i][j] == UserNum) {
                    gameMsg.SetPosX(i);
                    gameMsg.SetPosY(j);
                }
            }
        }

        gui = new GUI(Score,Maze);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setVisible(true);

        ArrayList UserList = new ArrayList();

        while (IsGoing) {
            Maze = gameMsg.mazeState.GetMaze().clone();
            Score = (ArrayList) gameMsg.mazeState.GetPlayerScoreList().clone();
            //gui = new GUI(Score,Maze);
            gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gui.setVisible(true);
            try {
                char c = (char) new BufferedReader(new InputStreamReader(System.in)).read();
                switch (c)
                {
                    case '1':
                        if (gameMsg.GetIsServer() == 1) {
                            JoinUp = Server.movePosition(gameMsg.GetUserName(),MoveAction.goUp);
                        }
                        else {
                            JoinUp = clientServer.movePosition(gameMsg.GetUserName(), MoveAction.goUp);
                        }
                        break;
                    case '2':
                        if (gameMsg.GetIsServer() == 1) {
                            JoinUp = Server.movePosition(gameMsg.GetUserName(),MoveAction.goDown);
                        }
                        else {
                            JoinUp = clientServer.movePosition(gameMsg.GetUserName(), MoveAction.goDown);
                        }
                        break;
                    case '3':
                        if (gameMsg.GetIsServer() == 1) {
                            JoinUp = Server.movePosition(gameMsg.GetUserName(),MoveAction.goLeft);
                        }
                        else {
                            JoinUp = clientServer.movePosition(gameMsg.GetUserName(), MoveAction.goLeft);
                        }
                        break;
                    case '4':
                        if (gameMsg.GetIsServer() == 1) {
                            JoinUp = Server.movePosition(gameMsg.GetUserName(),MoveAction.goUp);
                        }
                        else {
                            JoinUp = clientServer.movePosition(gameMsg.GetUserName(), MoveAction.goUp);
                        }
                        break;
                    case '0':
                        if (gameMsg.GetIsServer() == 1) {
                            JoinUp = Server.movePosition(gameMsg.GetUserName(),MoveAction.goRight);
                        }
                        else {
                            JoinUp = clientServer.movePosition(gameMsg.GetUserName(), MoveAction.goRight);
                        }
                        break;
                    case '9':
                        if (gameMsg.GetIsServer() == 1) {
                            JoinUp = Server.movePosition(gameMsg.GetUserName(),MoveAction.goUp);
                        }
                        else {
                            JoinUp = clientServer.movePosition(gameMsg.GetUserName(), MoveAction.goUp);
                        }
                        break;
                    default:
                        System.out.println("error input");
                        break;
                }
                if((c!='2')&&(c!='3')&&(c!='4')&&(c!='1')&&(c!='0')&&(c!='9')){
                    System.out.println("Input Error");
                }
                else {
                    gui.repaint();
                    System.out.println(Score);
                    //UserList = (ArrayList) updateData.SendMovement((int)c).clone();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void InitMaze()
    {
        int N_Num = 15;
        int K_Num = 15;
        for (int i=0;i<N_Num;i++)
        {
            for (int j=0; j<N_Num;j++){
                Maze[i][j] = 0;
            }
        }

        while (K_Num >0){
            int Len = GetRandom(N_Num);
            int Wid = GetRandom(N_Num);
            if(Maze[Len][Wid] != 0)
            {
                continue;
            }
            Maze[Len][Wid] = -1;
            K_Num--;
        }

        int LocalX = GetRandom(N_Num);
        int LocalY = GetRandom(N_Num);

        Maze[LocalX][LocalY] = 1;

        MazeState.SetMaze(Maze);
        for(int i = 0; i<N_Num;i++)
        {
            for (int j = 0;j<N_Num;j++)
            {
                System.out.print(Maze[i][j]+ " " );
            }
            System.out.println();
        }
        //System.out.println(Maze.);
        Score.add(UserId);
        Score.add((int)0);
        Score.add((int)1);
        mazeState.SetPlayerScoreList(Score);
    }

    public int GetRandom(int Limit) {
        Random rand = new Random();
        int i = rand.nextInt(); //int范围类的随机数
        i = rand.nextInt(Limit); //生成0-Limit以内的随机数
        //i = (int)(Math.random() * Limit); //0-Limit以内的随机数，用Matn.random()方式
        return  i;
    }

}