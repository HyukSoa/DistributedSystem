import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by huanyuhello on 5/9/2016.
 *
 */
public class Game {

    public Game()
    {
        //GameMsg gameMsg = new GameMsg();

    }
    public static void main(String[] args)
    {
       /* BufferedReader buf = new BufferedReader (new InputStreamReader(System.in));
//        String string = buf.toString();*/
//        System.out.println("In Game: args[0]" + args[0]);
//        System.out.println("In Game: args[1]" + args[1]);
//        System.out.println("In Game: args[2]" + args[2]);
        Client client = new Client(args[2]);
        client.InitGame();
        System.out.println("hello");
    }
}
