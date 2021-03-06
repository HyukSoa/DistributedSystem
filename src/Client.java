import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
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
    ClientRMI clientRMI;
    Server server;
    GUI gui;
    int JoinState = 0;
    boolean IsGoing = true;
    ArrayList Score = new ArrayList();
    MazeAndScore JoinUp;
    //MazeState updateData;
    public static ClientServerInterf clientServerInterf;

    ClientRMIInterface clientRMIInterface;
    Thread thread;
    int timeout = 0;
    boolean interupt = true;
    public Client(String ID) {


        //在RMI服务注册表中查找名称为Trakcer的对象，并调用其上的方法
        TrackerInterface Tracker = null;
        try {
            Tracker = (TrackerInterface) Naming.lookup("rmi://localhost:8888/Tracker");
            UserId = ID;
            Tracker.helloWorld(UserId);
            System.out.println("Set name Successful");
            gameMsg.SetUserName(UserId);
            System.out.println(UserId);
            gameMsg.SetUserList(Tracker.GetCurrentList(UserId));
            System.out.println(Tracker.GetCurrentList(UserId).toString());
            gameMsg.SetN_num(Tracker.GetNForTracker());
            System.out.println(Tracker.GetNForTracker());
            gameMsg.SetK_Num(Tracker.GetNForTracker());
            System.out.println(Tracker.GetNForTracker());
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
        //BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
        /*boolean judege = false;
        while (!judege) {

            UserId = buf.readLine();
            if () {
                judege = true;
            } else {
                System.out.println("error wrong name");
            }
        }*/

        try {
            clientRMI = new ClientRMI();
//           ClientRMIInterface crmii = (ClientRMIInterface) UnicastRemoteObject.exportObject(clientRMI, 0);
            Registry registry = LocateRegistry.getRegistry();//提供serverIP
            try {
                registry.bind("rmi://localhost/"+UserId, clientRMI);
                System.out.println("User "+UserId+" registered clientRMI!");
            } catch (AlreadyBoundException e) {
                e.printStackTrace();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
//        if (gameMsg.GetIsServer() == 2) {
//            try {
//                clientRMI.becomeBackup();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }


        ArrayList arrayList = gameMsg.GetUserList();
        //在RMI服务注册表中查找名称为Trakcer的对象，并调用其上的方法
        //System.out.println(arrayList.toString());
        System.out.println("arrayList.size() : " + arrayList.size());
        if (arrayList.size() < 3) {
            JoinState = 1;
            gameMsg.SetisServer(1);
            gameMsg.SetPrimServer(UserId);
        } else {
            try {
                Thread.sleep(timeout+50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JoinState = 1;
            for (int index = arrayList.size() - 3 ; index > 0; index -= 2) {

                try {
                    Registry rg = LocateRegistry.getRegistry();
                    System.out.println("Looking up "+arrayList.get(index).toString());
                    clientRMIInterface = (ClientRMIInterface) rg.lookup("rmi://localhost/" + arrayList.get(index).toString());

                    String Primary = clientRMIInterface.getPrimaryServer();
                    String Backup = clientRMIInterface.getBackupServer();
                    //GameMsg.PrimaryServer = Primary;
                    //GameMsg.BackupServer = Backup;
                    System.out.println("Primary = "+Primary);
                    System.out.println("Backup = "+Backup);
                    JoinState = 2;
                    while (Primary == null) {
                        // find an alive player, but his primary server is null, which means that
                        // he doesn't know the primary server either. Keep asking him.
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Primary = clientRMIInterface.getPrimaryServer();
                        Backup = clientRMIInterface.getBackupServer();
                    }
                    gameMsg.SetisServer(0);
                    gameMsg.SetPrimServer(Primary);
                    if (Backup != null)
                        gameMsg.SetBackupServer(Backup);
                    break;
                } catch (RemoteException |NotBoundException e) {
                    // Met a dead player
                    JoinState = 1;
                    continue;
                }
            }
        }
        while(interupt) {
            try {
                Registry rg = LocateRegistry.getRegistry();

                switch (JoinState) {

                    case 1:
                        InitMaze();
                        gameMsg.SetisServer(1);
                        gameMsg.SetPrimServer(UserId);
                        server = new Server();
                        try {
                            rg.bind("rmi://localhost/server" + UserId, server);
                        } catch (AlreadyBoundException e) {
                            e.printStackTrace();
                        }
                        System.out.println("User " + UserId + " registered " + "as primary server!");
                        thread = new Thread(server);
                        thread.start();
                        System.out.println("JoinState " + JoinState);
                        interupt = false;
                        break;
                    case 2:
                        Thread.sleep(timeout);
                        clientServerInterf = (ClientServerInterf) rg.lookup("rmi://localhost/server" + GameMsg.PrimaryServer);
                        JoinUp = clientServerInterf.addPlayer(gameMsg.GetUserName());
                        Maze = JoinUp.getMaze().clone();
                        interupt = false;
                        break;
                    default:
                        break;
                }
            } catch (RemoteException | NotBoundException e) {
                //e.printStackTrace();
                interupt = true;
                //System.out.println("Interupt : " + interupt);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("JoinState  : " + JoinState);
        System.out.println("GetPrimServer   :" + gameMsg.GetPrimServer());

        System.out.println("GetBackupServer   :" + gameMsg.GetBackupServer());

    }


    public void InitGame(){

        System.out.println("GetIsServer " +gameMsg.GetIsServer());
        int UserNum = 0;
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

        if (gameMsg.GetIsServer() == 1) {
            Maze = MazeState.GetMaze().clone();
            Score = (ArrayList) MazeState.PlayerScoreList.clone();
        }
        else {
            System.out.println("gameMSG :" + GameMsg.UserName);
            Maze = JoinUp.getMaze().clone();
            Score = (ArrayList) JoinUp.getPlayerScoreList().clone();
        }

        while (IsGoing) {
            if (gameMsg.GetIsServer() == 1) {
                Maze = MazeState.GetMaze().clone();
                Score = (ArrayList) MazeState.PlayerScoreList.clone();
            }
            else {
                System.out.println("gameMSG :" + GameMsg.UserName);
                Maze = JoinUp.getMaze().clone();
                Score = (ArrayList) JoinUp.getPlayerScoreList().clone();
            }

            try {
                if (gameMsg.GetIsServer() == 1) {
                    if(server == null)
                    {
                        System.out.println("Old Server is NULL");
                        server = clientRMI.backserver;
                    }
                    JoinUp = server.refreshSate(GameMsg.UserName);

                }
                else {
                    JoinUp = clientServerInterf.refreshSate(GameMsg.UserName);
                    MazeState.Maze = JoinUp.getMaze().clone();
                    MazeState.PlayerScoreList = (ArrayList) JoinUp.getPlayerScoreList().clone();
                }
                //gui = new GUI(Score,Maze);
                gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                gui.setVisible(true);

                char c = (char) new BufferedReader(new InputStreamReader(System.in)).read();
                if(server == null)
                {
                    System.out.println("Old Server is NULL");
                    server = clientRMI.backserver;
                }
                switch (c)
                {
                    case '1':
                        if (gameMsg.GetIsServer() == 1) {
                            JoinUp = server.movePosition(GameMsg.UserName,MoveAction.goUp);
                        }
                        else {
                            //System.out.println("gameMSG :" + gameMsg.GetUserName());
                            JoinUp = clientServerInterf.movePosition(GameMsg.UserName, MoveAction.goUp);
                            MazeState.Maze = JoinUp.getMaze().clone();
                            MazeState.PlayerScoreList = (ArrayList) JoinUp.getPlayerScoreList().clone();

                        }
                        break;
                    case '2':
                        if (gameMsg.GetIsServer() == 1) {
                            JoinUp = server.movePosition(GameMsg.UserName,MoveAction.goDown);
                        }
                        else {
                            JoinUp = clientServerInterf.movePosition(GameMsg.UserName, MoveAction.goDown);
                            MazeState.Maze = JoinUp.getMaze().clone();
                            MazeState.PlayerScoreList = (ArrayList) JoinUp.getPlayerScoreList().clone();
                        }
                        break;
                    case '3':
                        if (gameMsg.GetIsServer() == 1) {
                            JoinUp = server.movePosition(GameMsg.UserName,MoveAction.goLeft);
                        }
                        else {
                            JoinUp = clientServerInterf.movePosition(GameMsg.UserName, MoveAction.goLeft);
                            MazeState.Maze = JoinUp.getMaze().clone();
                            MazeState.PlayerScoreList = (ArrayList) JoinUp.getPlayerScoreList().clone();
                        }
                        break;
                    case '4':
                        if (gameMsg.GetIsServer() == 1) {
                            JoinUp = server.movePosition(GameMsg.UserName,MoveAction.goRight);
                        }
                        else {
                            JoinUp = clientServerInterf.movePosition(GameMsg.UserName, MoveAction.goRight);
                            MazeState.Maze = JoinUp.getMaze().clone();
                            MazeState.PlayerScoreList = (ArrayList) JoinUp.getPlayerScoreList().clone();
                        }
                        break;
                    case '0':
                        if (gameMsg.GetIsServer() == 1) {
                            JoinUp = server.refreshSate(GameMsg.UserName);
                        }
                        else {
                            JoinUp = clientServerInterf.refreshSate(GameMsg.UserName);
                            MazeState.Maze = JoinUp.getMaze().clone();
                            MazeState.PlayerScoreList = (ArrayList) JoinUp.getPlayerScoreList().clone();
                        }
                        break;
                    case '9':
                        if (gameMsg.GetIsServer() == 1) {
                            server.exitGame(GameMsg.UserName);
                            gui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                        }
                        else {
                            clientServerInterf.exitGame(GameMsg.UserName);
                            gui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                        }
                        break;
                    default:
                        System.out.println("error input :" + c);

                        break;
                }
                if((c!='2')&&(c!='3')&&(c!='4')&&(c!='1')&&(c!='0')&&(c!='9')){

                }
                else {
                    gui.repaint();
                    //System.out.println("Socore list : "+ Score);
                    //UserList = (ArrayList) updateData.SendMovement((int)c).clone();
                }
            } catch (RemoteException e) {
                System.out.println("Cannot connect server, continue..");
                Registry rg = null;
                try {
                    rg = LocateRegistry.getRegistry();
                    clientServerInterf = (ClientServerInterf) rg.lookup("rmi://localhost/server"+gameMsg.GetPrimServer());
                } catch (RemoteException | NotBoundException e1) {
                    e1.printStackTrace();
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