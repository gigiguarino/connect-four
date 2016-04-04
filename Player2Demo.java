package eecs285.proj5.gguarino;

/**
 * Created by gabrielleguarino on 12/7/15.
 */
public class Player2Demo
{
    public static void main(String args[])
    {
        PlayerSocket theClient;
        theClient = new PlayerSocket("127.0.0.1", 8080);
        theClient.startClient();
        ConnectFour game = new ConnectFour(theClient);
    }
}
