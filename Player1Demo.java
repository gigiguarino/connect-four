package eecs285.proj5.gguarino;

/**
 * Created by gabrielleguarino on 12/7/15.
 */
public class Player1Demo
{
    public static void main(String args[])
    {
        PlayerSocket theServer;
        theServer = new PlayerSocket("127.0.0.1", 8080);
        theServer.startServer();
        ConnectFour game = new ConnectFour(theServer);
    }
}
