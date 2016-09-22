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
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by huanyuhello on 5/9/2016.
 */
public class Client {

    public static String UserId =new String();
    int Maze[][] = new int[15][15];
    //MazeState mazeState = new MazeState();
    GameMsg gameMsg = new GameMsg();
    MazeState mazeState = new MazeState();
    ClientServerInterf clientServer;
    ClientRMI clientRMI;
    Server server;
    GUI gui;
    int JoinState = 0;
    boolean IsGoing = true;
    ArrayList Score = new ArrayList();
    MazeAndScore JoinUp;
    //MazeState updateData;
    ClientServerInterf clientServerInterf;

    ClientRMIInterface clientRMIInterface;
    Thread thread;

    public Client() {

        try {
            //在RMI服务注册表中查找名称为Trakcer的对象，并调用其上的方法
            TrackerInterface Tracker = (TrackerInterface) Naming.lookup("rmi://localhost:8888/Tracker");
            BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
            boolean judege = false;
            while (!judege) {
                UserId = buf.readLine();
                if (Tracker.helloWorld(UserId)) {
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
        } catch (NotBoundException ignored) {

        } catch (MalformedURLException e) {

        } catch (RemoteException e) {

        } catch (UnknownHostException e) {

        } catch (ConnectException e) {

        } catch (IOException e) {

        }
        ArrayList arrayList = gameMsg.GetUserList();
        //在RMI服务注册表中查找名称为Trakcer的对象，并调用其上的方法
        //System.out.println(arrayList.toString());
        System.out.println("arrayList.size() : " + arrayList.size());
        if (arrayList.size() < 3) {
            JoinState = 1;
            gameMsg.SetisServer(1);
            gameMsg.SetPrimServer(UserId);
        } else {

            for (int index = 1; index < arrayList.size(); index += 2) {

                try {
                    clientRMIInterface = (ClientRMIInterface) Naming.lookup("rmi://localhost:8000/" + arrayList.get(index).toString());
                    String Primary = clientRMIInterface.getPrimaryServer();
                    String Backup = clientRMIInterface.getBackupServer();

                    if ((Primary != null) && (Backup != null)) {
                        gameMsg.SetisServer(0);
                        gameMsg.SetPrimServer(Primary);
                        JoinState = 3;
                        break;
                    } else if ((Primary != null) && (Backup == null)) {
                        gameMsg.SetisServer(0);
                        gameMsg.SetPrimServer(Primary);
                        JoinState = 2;
                        break;
                    } else {
                        gameMsg.SetisServer(1);
                        gameMsg.SetPrimServer(UserId);
                        JoinState = 1;
                    }
                } catch (RemoteException e) {
                    JoinState = 1;
                    gameMsg.SetisServer(1);
                    gameMsg.SetPrimServer(UserId);
                    continue;
                } catch (NotBoundException e) {
                    JoinState = 1;
                    gameMsg.SetisServer(1);
                    gameMsg.SetPrimServer(UserId);
                    continue;
                } catch (MalformedURLException e) {
                    JoinState = 1;
                    gameMsg.SetisServer(1);
                    gameMsg.SetPrimServer(UserId);
                    continue;
                }
            }
        }
        try {
            switch (JoinState) {
                case 1:
                    InitMaze();
                    server = new Server();
                    LocateRegistry.createRegistry(7000);//serverIP
                    Naming.rebind("rmi://localhost:7000/"+UserId,server);
                    gameMsg.SetPrimServer(UserId);
                    thread = new Thread(server);
                    thread.start();
                    System.out.println("JoinState " + JoinState);
                    break;
                case 2:
                    clientServerInterf = (ClientServerInterf) Naming.lookup("rmi://localhost:7000/" + gameMsg.GetPrimServer());

                    JoinUp = clientServerInterf.addPlayer(gameMsg.GetUserName());
                    Maze = JoinUp.getMaze().clone();

                    break;
                case 3:
                    clientServerInterf = (ClientServerInterf) Naming.lookup("rmi://localhost:7000/" + gameMsg.GetPrimServer());
                    JoinUp = clientServerInterf.addPlayer(gameMsg.GetUserName());
                    Maze = JoinUp.getMaze().clone();
                    break;
                default:
                    break;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }


        System.out.println(JoinState);
        System.out.println("GetPrimServer   :" + gameMsg.GetPrimServer());

       try {
            clientRMI = new ClientRMI();
            LocateRegistry.createRegistry(8000);//提供serverIP
            Naming.rebind("rmi://localhost:8000/"+UserId,clientRMI);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    public void InitGame(){

        System.out.println("GetIsServer " +gameMsg.GetIsServer());
        int UserNum = 5;
        if(gameMsg.GetIsServer() == 1)
        {
            Maze = MazeState.GetMaze().clone();
            Score = (ArrayList) MazeState.PlayerScoreList.clone();
            System.out.println("Score list : " +Score.toString());
        }
        else {
            Maze = JoinUp.getMaze().clone();

            Score = (ArrayList) JoinUp.getPlayerScoreList().clone();
            System.out.println("Score list : " +Score.toString());
        }
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
                    break;
                }
            }
        }
        gui = new GUI(Score,Maze);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setVisible(true);

        ArrayList UserList = new ArrayList();
        while (IsGoing) {
            if (gameMsg.GetIsServer() == 1) {
                Maze = MazeState.GetMaze().clone();
                Score = (ArrayList) MazeState.PlayerScoreList.clone();
            }
            else {
                System.out.println("gameMSG :" + gameMsg.GetUserName());
                Maze = JoinUp.getMaze().clone();
                Score = (ArrayList) JoinUp.getPlayerScoreList().clone();
            }

            //gui = new GUI(Score,Maze);
            gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gui.setVisible(true);
            try {
                char c = (char) new BufferedReader(new InputStreamReader(System.in)).read();
                switch (c)
                {
                    case '1':
                        if (gameMsg.GetIsServer() == 1) {
                            JoinUp = server.movePosition(gameMsg.GetUserName(),MoveAction.goUp);
                        }
                        else {
                            System.out.println("gameMSG :" + gameMsg.GetUserName());
                            JoinUp = clientServerInterf.movePosition(gameMsg.GetUserName(), MoveAction.goUp);
                            MazeState.Maze = JoinUp.getMaze().clone();
                            MazeState.PlayerScoreList = (ArrayList) JoinUp.getPlayerScoreList().clone();

                        }
                        break;
                    case '2':
                        if (gameMsg.GetIsServer() == 1) {
                            JoinUp = server.movePosition(gameMsg.GetUserName(),MoveAction.goDown);
                        }
                        else {
                            JoinUp = clientServerInterf.movePosition(gameMsg.GetUserName(), MoveAction.goDown);
                            MazeState.Maze = JoinUp.getMaze().clone();
                            MazeState.PlayerScoreList = (ArrayList) JoinUp.getPlayerScoreList().clone();
                        }
                        break;
                    case '3':
                        if (gameMsg.GetIsServer() == 1) {
                            JoinUp = server.movePosition(gameMsg.GetUserName(),MoveAction.goLeft);
                        }
                        else {
                            JoinUp = clientServerInterf.movePosition(gameMsg.GetUserName(), MoveAction.goLeft);
                            MazeState.Maze = JoinUp.getMaze().clone();
                            MazeState.PlayerScoreList = (ArrayList) JoinUp.getPlayerScoreList().clone();
                        }
                        break;
                    case '4':
                        if (gameMsg.GetIsServer() == 1) {
                            JoinUp = server.movePosition(gameMsg.GetUserName(),MoveAction.goRight);
                        }
                        else {
                            JoinUp = clientServerInterf.movePosition(gameMsg.GetUserName(), MoveAction.goRight);
                            MazeState.Maze = JoinUp.getMaze().clone();
                            MazeState.PlayerScoreList = (ArrayList) JoinUp.getPlayerScoreList().clone();
                        }
                        break;
                    case '0':
                        if (gameMsg.GetIsServer() == 1) {
                            JoinUp = server.refreshSate(gameMsg.GetUserName());
                        }
                        else {
                            JoinUp = clientServerInterf.refreshSate(gameMsg.GetUserName());
                            MazeState.Maze = JoinUp.getMaze().clone();
                            MazeState.PlayerScoreList = (ArrayList) JoinUp.getPlayerScoreList().clone();
                        }
                        break;
                    case '9':
                        if (gameMsg.GetIsServer() == 1) {
                            server.exitGame(gameMsg.GetUserName());
                            gui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                        }
                        else {
                            clientServerInterf.exitGame(gameMsg.GetUserName());
                            gui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
                    System.out.println("Socore list : "+ Score);
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
        Score.add(0);
        Score.add(1);
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