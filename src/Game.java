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
        String string = buf.toString();*/
        Client client = new Client();
        client.InitGame();
        System.out.println("hello");

    }

}
