package eecs285.proj5.gguarino;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by gabrielleguarino on 12/7/15.
 */

public class PlayerSocket
{
    private String ip;
    private int portNum;
    private Socket socket;
    private int playerNum;

    // data streams
    private DataOutputStream output;
    private DataInputStream input;

    public int getPlayerNum()
    {
        return playerNum;
    }

    public boolean send(String str)
    {
        boolean success = false;
        //System.out.println("sent: " + str);

        try
        {
            output.writeBytes(str);
            output.writeByte(0); // append a zero on to the end of the string
            success = true;
        }

        catch (IOException e)
        {
            System.out.println("Error sending string.");
            System.exit(1);
        }

        return success;
    }

    public String receive()
    {
        Vector<Byte> bytes = new Vector<Byte>();
        byte [] bytes2;
        byte b;

        String received = "";

        try
        {
            b = input.readByte();

            while (b != 0)
            {
                bytes.add(b);
                b = input.readByte();
            }

            bytes2 = new byte[bytes.size()];

            for (int i = 0; i < bytes.size(); i++)
            {
                bytes2[i] = bytes.elementAt(i).byteValue();
            }

            received = new String(bytes2);
        }

        catch (IOException e)
        {
            System.out.println("Error receiving string.");
            System.exit(2);
        }

        //System.out.println("received: " + received);
        return received;
    }

    public void startServer()
    {
        ServerSocket serverSock;
        playerNum = 1;

        try
        {
            serverSock = new ServerSocket(portNum);
            System.out.println("Waiting for client to connect...");
            socket = serverSock.accept();

            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());


            System.out.println("Client connection accepted.");
        }

        catch (IOException ioe)
        {
            System.out.println("Error starting server.");
            System.exit(3);
        }
    }

    public void startClient()
    {
        playerNum = 2;

        try
        {
            socket = new Socket(ip, portNum);
            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());
        }

        catch (IOException ioe)
        {
            System.out.println("Error starting client.");
            System.exit(4);
        }
    }

    public PlayerSocket(String inIp, int inPort)
    {
        ip = inIp;
        portNum = inPort;
        socket = null;
        output = null;
        input = null;
    }
}
