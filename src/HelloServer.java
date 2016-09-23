import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RemoteObject;
import java.rmi.server.ServerNotActiveException;

import static java.rmi.server.RemoteServer.getClientHost;

public class HelloServer {
    public static void main(String args[]) {

        try {
            //创建一个远程对象 
            HelloImpl Tracker = new HelloImpl();
            Tracker.N_num = 15;
            Tracker.K_num = 10;
            //本地主机上的远程对象注册表Registry的实例，并指定端口为8888，这一步必不可少（Java默认端口是1099），必不可缺的一步，缺少注册表创建，则无法绑定对象到远程注册表上 
            LocateRegistry.createRegistry(8888);

            //把远程对象注册到RMI注册服务器上，并命名为Trakcer
            //绑定的URL标准格式为：rmi://host:port/name(其中协议名可以省略，下面两种写法都是正确的） 
            Naming.rebind("rmi://localhost:8888/Tracker",Tracker);//;bind(,Trakcer);

            System.out.println(">>>>>INFO:远程Trakcer对象绑定成功！");

        } catch (RemoteException e) {
            System.out.println("创建远程对象发生异常！");
            e.printStackTrace();
        } catch (MalformedURLException e) {
            System.out.println("发生URL畸形异常！");
            e.printStackTrace();
        }
    }
}