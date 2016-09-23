import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class HelloImpl extends UnicastRemoteObject implements TrackerInterface{
    public int N_num;
    public int K_num;
    int countArray[][] = new int[20][20];
    ArrayList List = new ArrayList();
    int Count = 0;
    /**
     * 因为UnicastRemoteObject的构造方法抛出了RemoteException异常，因此这里默认的构造方法必须写，必须声明抛出RemoteException异常 
     *
     * @throws RemoteException
     */
    public HelloImpl() throws RemoteException {
    }

    /**
     * 简单的返回“Hello World！"字样 
     *
     * @return 返回“Hello World！"字样 
     * @throws java.rmi.RemoteException
     */
    public boolean helloWorld(String name) throws RemoteException {

        if(List.contains(name)){
            System.out.print("Same");
            return false;
        }
        try {
            List.add(getClientHost());
            List.add(name);
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
        }

        System.out.println(name);
        return true;
    }

    /**
     * 一个简单的业务方法，根据传入的人名返回相应的问候语 
     *
     * @param name 人名
     * @return 返回相应的问候语
     * @throws java.rmi.RemoteException
     */


    public ArrayList GetCurrentList(String name) throws RemoteException {
        System.out.println(name);

        try {
            System.out.println(getClientHost());

            //Count++;
        } catch (ServerNotActiveException e) {
            System.out.println("发生client Crash异常");
            e.printStackTrace();
        }

//********************************************************************************************************************//
        System.out.println(List.toString());


        //String list[] = new String[List.size()];

        //System.out.println(List.toArray().toString());
        return List;
    }

    @Override
    public int GetKForTracker() throws RemoteException {
        return this.K_num;
    }

    @Override
    public int GetNForTracker() throws RemoteException {
        return this.N_num;
    }



}