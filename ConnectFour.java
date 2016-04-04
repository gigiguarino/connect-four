package eecs285.proj5.gguarino;

/*
// Created by gabrielleguarino on 12/3/15.
*/



import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import java.net.URL;

public class ConnectFour
{
    PlayerSocket player;
    int playerNum;
    int otherPlayerNum;
    boolean done;
    boolean won;
    int winner;

    private JFrame window;
    private JFrame winWindow;
    private JFrame startWindow;
    private JFrame exitWindow;
    private JFrame waitingWindow;

    private JPanel board;
    private JPanel board2;

    private JButton playAgainButton;
    private JButton playButton;
    private JButton exit1;
    private JButton exit2;

    private clickListener listen;
    private eventListener listen2;

    private JLabel turn;
    boolean yourTurn;
    boolean canPlay;
    boolean playAgain;
    boolean otherPlayAgain;
    boolean initiatingGame;
    boolean notAgain;


    // player 1 is red
    // player 2 is yellow

    // chips
    private ImageIcon blank;
    private ImageIcon redChip;
    private ImageIcon yellowChip;
    private ImageIcon player1turn;
    private ImageIcon player2turn;

    private JLabel[] grid; // images
    private int[] grid2; // playerNum in spots
    // 1  2  3  4  5  6  7
    // 8  9  10 11 12 13 14
    // 15 16 17 18 19 20 21
    // 22 23 24 25 26 27 28
    // 29 30 31 32 33 34 35
    // 36 37 38 39 40 41 42

    private int numCols = 7;
    private int numRows = 6;
    private int numSpotsCol[]; // number of spots in each column

    // chat
    JTextField chatInput;
    JLabel[] chats;
    boolean chatFull;
    int numChat = 13;
    int numChatFull;
    int maxChat = 20;





    /*
    // GAMEPLAY
    // this section contains all the gameplay logic
    // and actually handles the placing of chips and text
    // onto the board
    */

    void playerWon(int player)
    {
        initiatingGame = true;
        winWindow = new JFrame("Game Over");
        playAgainButton = new JButton("Play Again?");
        playAgainButton.addActionListener(listen2);
        exit1 = new JButton("Exit");
        exit1.addActionListener(listen2);
        winWindow.setLayout(new BorderLayout());
        winWindow.setAlwaysOnTop(true);
        JLabel text;
        canPlay = false;

        if (playerNum == player)
        {
            text = new JLabel("You Won!", SwingConstants.CENTER);
        }

        else if (player == 0)
        {
            text = new JLabel("It's a tie!", SwingConstants.CENTER);
        }

        else
        {
            text = new JLabel("You Lost!", SwingConstants.CENTER);
        }

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(playAgainButton, BorderLayout.NORTH);
        bottom.add(exit1, BorderLayout.SOUTH);

        winWindow.add(text, BorderLayout.NORTH);
        winWindow.add(bottom, BorderLayout.SOUTH);
        winWindow.setSize(150, 100);
        winWindow.setLocationRelativeTo(window);
        winWindow.setVisible(true);
    }

    void addChip(int col, int numSpots, int currentPlayer)
    {
        if (numSpots <= 0) {
            // can't do this
            // no room in column
            return;
        }

        int row = numSpots - 1;
        int num = col + numCols*row;

        if (currentPlayer == 1)
        {
            grid[num].setIcon(redChip);
            grid2[num] = currentPlayer;
        }

        else
        {
            grid[num].setIcon(yellowChip);
            grid2[num] = currentPlayer;
        }

        won = checkForWin(currentPlayer, num);

        if (won)
        {
            playerWon(winner);
        }
    }

    public void updateHeader()
    {
        if (playerNum == 1)
        {
            if (yourTurn)
            {
                turn.setIcon(player1turn);
            }

            else
            {
                turn.setIcon(player2turn);
            }
        }

        else
        {
            if (yourTurn)
            {
                turn.setIcon(player2turn);
            }

            else
            {
                turn.setIcon(player1turn);
            }
        }
    }

    public boolean isNumeric(String s)
    {
        if (s.length() != 0)
        {
            if (s.substring(s.length() - 1).equals(" "))
            {
                return false;
            }
        }

        try
        {
            double d = Double.parseDouble(s);
        }

        catch (NumberFormatException e)
        {
            return false;
        }

        return true;
    }

    public void addLongerString(String s, int playerNum)
    {
        String current;
        int num = 0;
        int z = 0;

        while (num <= s.length())
        {
            if (z == 0)
            {
                z = 1;
                current = s.substring(num, maxChat);
                num = num + maxChat;
                addText(current, playerNum, true);
            }

            else if (num <= s.length() - maxChat)
            {
                current = s.substring(num, num + maxChat);
                num = num + maxChat;
                addText(current, playerNum, false);
            }

            else
            {
                current = s.substring(num);
                num = num + maxChat;
                addText(current, playerNum, false);
            }
        }
    }

    public class GameRound implements Runnable
    {
        public void run()
        {
            while (!done)
            {
                updateHeader();
                System.out.print(""); // have to do this to keep this thread going
                int i = 0;
                String recovered = "NULL";

                if (!won)
                {
                    recovered = player.receive();
                    //System.out.println(recovered.length());
                }

                boolean no = false;

                if (recovered.equals("Y") || recovered.equals("N") || recovered.equals(""))
                {
                    no = true;
                }

                if (!isNumeric(recovered) && !recovered.equals("NULL") && !recovered.equals("Y") && !no)
                {
                    //System.out.print(recovered);
                    if (recovered.length() > maxChat)
                    {
                        addLongerString(recovered, otherPlayerNum);
                    }

                    else
                    {
                        addText(recovered, otherPlayerNum, true);
                    }
                }

                if (!yourTurn && canPlay && isNumeric(recovered) && !won)
                {
                    // other player's turn
                    // wait for response for other player
                    // drop other player's chip in

                    i = Integer.parseInt(recovered);

                    if (numSpotsCol[i-1] > 0)
                    {
                        addChip(i - 1, numSpotsCol[i - 1], otherPlayerNum);
                        numSpotsCol[i - 1]--;
                        yourTurn = true;
                        updateHeader();
                    }
                }

                if (numSpotsCol[0] == 0 && numSpotsCol[1] == 0 && numSpotsCol[2] == 0 &&
                        numSpotsCol[3] == 0 && numSpotsCol[4] == 0 && numSpotsCol[5] == 0 && numSpotsCol[6] == 0 && !initiatingGame)
                {
                    playerWon(0);
                    won = true;
                }
            }
        }
    }











    /*
    // Check for wins
    // checks row
    // checks column
    // checks right diagonal
    // checks left diagonal
     */

    boolean checkForWinROW(int playerNum, int addedChipSpace)
    {
        int row = addedChipSpace/numCols;

        int spots[] = new int[numCols];
        boolean spotsTaken[] = new boolean[numCols];

        spots[0] = (row * numCols);

        for (int i = 1; i < numCols; i++)
        {
            spots[i] = spots[i - 1] + 1;
        }

        for (int i = 0; i < numCols; i++)
        {
            if (grid2[spots[i]] == playerNum)
            {
                spotsTaken[i] = true;
            }

            else
            {
                spotsTaken[i] = false;
            }
        }

        if ((spotsTaken[0] && spotsTaken[1] && spotsTaken[2] && spotsTaken[3]) ||
                (spotsTaken[1] && spotsTaken[2] && spotsTaken[3] && spotsTaken[4]) ||
                (spotsTaken[2] && spotsTaken[3] && spotsTaken[4] && spotsTaken[5]) ||
                (spotsTaken[3] && spotsTaken[4] && spotsTaken[5] && spotsTaken[6]))
        {
            // this player has won!!!
            return true;
        }

        return false;
    }

    boolean checkForWinCOL(int playerNum, int addedChipSpace)
    {
        int col = addedChipSpace%numCols;

        int spots[] = new int[numRows];
        boolean spotsTaken[] = new boolean[numRows];

        spots[0] = col;

        for (int i = 1; i < numRows; i++)
        {
            spots[i] = spots[i - 1] + numCols;
        }

        for (int i = 0; i < numRows; i++)
        {
            if (grid2[spots[i]] == playerNum)
            {
                spotsTaken[i] = true;
            }

            else
            {
                spotsTaken[i] = false;
            }
        }

        if ((spotsTaken[0] && spotsTaken[1] && spotsTaken[2] && spotsTaken[3]) ||
                (spotsTaken[1] && spotsTaken[2] && spotsTaken[3] && spotsTaken[4]) ||
                (spotsTaken[2] && spotsTaken[3] && spotsTaken[4] && spotsTaken[5]))
        {
            // this player has won!!!
            return true;
        }

        return false;
    }

    boolean checkForWinRDIAG(int playerNum, int addedChipSpace)
    {
        // didn't know how to accurately check for diagonal wins so i made
        // arrays of the spots in the diagonal

        int[][] rightDiag = {{20, 26, 32, 28}, {13, 19, 25, 31, 37}, {6, 12, 18, 24, 30, 36},
                {5, 11, 17, 23, 29, 35}, {4, 10, 16, 22, 28}, {3, 9, 15, 21}};

        boolean one = false;
        boolean two = false;
        boolean three = false;

        for (int i = 0; i < numRows; i++)
        {
            for (int j = 0; j < rightDiag[i].length; j++)
            {
                if (grid2[rightDiag[i][j]] == playerNum && three)
                {
                    one = false;
                    two = false;
                    three = false;
                    return true;
                }

                else if (grid2[rightDiag[i][j]] == playerNum && two)
                {
                    one = false;
                    two = false;
                    three = true;
                }

                else if (grid2[rightDiag[i][j]] == playerNum && one)
                {
                    one = false;
                    two = true;
                    three = false;
                }

                else if (grid2[rightDiag[i][j]] == playerNum)
                {
                    one = true;
                    two = false;
                    three = false;
                }

                else
                {
                    one = false;
                    two = false;
                    three = false;
                }
            }
        }

        return false;
    }

    boolean checkForWinLDIAG(int playerNum, int addedChipSpace)
    {
        // didn't know how to accurately check for diagonal wins so i made
        // arrays of the spots in the diagonal

        int[][] leftDiag = {{14, 22, 30, 38}, {7, 15, 23, 31, 39},
                {0, 8, 16, 24, 32, 40}, {1, 9, 17, 25, 33, 41}, {2, 10, 18, 26, 34},
                {3, 11, 19, 27}};

        boolean one = false;
        boolean two = false;
        boolean three = false;

        for (int i = 0; i < numRows; i++)
        {
            for (int j = 0; j < leftDiag[i].length; j++)
            {
                if (grid2[leftDiag[i][j]] == playerNum && three)
                {
                    one = false;
                    two = false;
                    three = false;
                    return true;
                }

                else if (grid2[leftDiag[i][j]] == playerNum && two)
                {
                    one = false;
                    two = false;
                    three = true;
                }

                else if (grid2[leftDiag[i][j]] == playerNum && one)
                {
                    one = false;
                    two = true;
                    three = false;
                }

                else if (grid2[leftDiag[i][j]] == playerNum)
                {
                    one = true;
                    two = false;
                    three = false;
                }

                else
                {
                    one = false;
                    two = false;
                    three = false;
                }
            }
        }

        return false;
    }

    boolean checkForWin(int playerNum, int addedChipSpace)
    {
        if (checkForWinROW(playerNum, addedChipSpace))
        {
            //System.out.println("Row win");
            winner = playerNum;
            return true;
        }

        if (checkForWinCOL(playerNum, addedChipSpace))
        {
            //System.out.println("Column win");
            winner = playerNum;
            return true;
        }

        if (checkForWinLDIAG(playerNum, addedChipSpace))
        {
            //System.out.println("Left diag win");
            winner = playerNum;
            return true;
        }

        if (checkForWinRDIAG(playerNum, addedChipSpace))
        {
            //System.out.println("Right diag win");
            winner = playerNum;
            return true;
        }

        return false;
    }










    /*
    // New game
    */


    public class waitForStart implements Runnable
    {
        public void run()
        {
            // you want to play again
            // get input from other player if they want to play again

            while (initiatingGame)
            {
                if (playAgain)
                {
                    player.send("Y");
                }

                else
                {
                    player.send("N");
                }

                String s = player.receive();

                if (s.equals("Y"))
                {
                    //System.out.println(playerNum + " yes");
                    otherPlayAgain = true;
                    initiatingGame = false;
                }

                else if (s.equals("N"))
                {
                    //System.out.println(playerNum + " no");
                    otherPlayAgain = false;
                    initiatingGame = false;
                }
            }

            //System.out.println("gets here");
        }
    }

    void startNewGame()
    {
        initiatingGame = true;
        winWindow.dispose();
        //System.out.println("in startNewGame()");

        if (playAgain)
        {
            player.send("Y");
        }

        else
        {
            player.send("N");
            winWindow.setVisible(false);
            window.setVisible(false);
        }

        waitForStart waiting = new waitForStart();
        Thread t = new Thread(waiting);
        t.start();

        try
        {
            t.join();
        }

        catch (InterruptedException e)
        {
            System.out.println("Error starting new game");
            System.exit(1);
        }

        if (!initiatingGame)
        {
            if (playAgain && otherPlayAgain)
            {
                startNewGame2();
            }

            else if (playAgain && !otherPlayAgain)
            {
                exitWindow = new JFrame();
                exitWindow.setLayout(new BorderLayout());
                JLabel label = new JLabel("Other player doesn't want to play.", SwingConstants.CENTER);
                exit2 = new JButton("Exit");
                exit2.addActionListener(listen2);

                exitWindow.add(label, BorderLayout.NORTH);
                exitWindow.add(exit2, BorderLayout.SOUTH);
                exitWindow.setSize(225, 75);
                exitWindow.setLocationRelativeTo(window);
                exitWindow.setVisible(true);
            }

            else if (!playAgain && otherPlayAgain)
            {
                window.dispose();
                window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
            }

            else
            {
                window.dispose();
                window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
            }
        }
    }

    public void startNewGame2()
    {
        //System.out.println("In startNewGame2");
        done = false;
        won = false;
        initiatingGame = false;
        playAgain = false;
        otherPlayAgain = false;


        if (playerNum == 1)
        {
            yourTurn = true;
        }

        else
        {
            yourTurn = false;
        }

        updateHeader();

        // clear board
        for (int i = 0; i < (numRows * numCols); i++)
        {
            grid[i].setIcon(blank);
            grid2[i] = 0;
        }

        // reset numbers
        for (int i = 0; i < numCols; i++)
        {
            numSpotsCol[i] = numRows;
        }

        canPlay = true;
    }












    /*
    // Chat
    // players send chats back and forth
    */

    void addText(String text, int playerNum, boolean first)
    {
        // there are 17 slots where i can put text in
        // 0 - 16 in the linked list

        String chatTxt = text;

        if (first)
        {
            if (playerNum == 1)
            {
                chatTxt = " P1: " + chatTxt;
            }

            else
            {
                chatTxt = chatTxt + " :P2 ";
            }
        }

        else
        {
            if (playerNum == 1)
            {
                chatTxt = "       " + chatTxt;
            }

            else
            {
                chatTxt = chatTxt + "       ";
            }
        }

        //System.out.println(chatTxt);

        if (numChatFull == numChat)
        {
            // all the chat boxes are full
            // move them all up one
            // 2 goes to 1
            // 4 foes to 3
            // put the next text in the last spot
            String txt;
            int align;

            for (int i = 0; i < numChat - 1; i++)
            {
                txt = chats[i+1].getText();
                align = chats[i+1].getHorizontalAlignment();
                chats[i].setText(txt);
                chats[i].setHorizontalAlignment(align);
            }

            if (playerNum == 1)
            {
                chats[numChat - 1].setText(chatTxt);
                chats[numChat - 1].setHorizontalAlignment(SwingConstants.LEFT);
            }

            else
            {
                chats[numChat - 1].setText(chatTxt);
                chats[numChat - 1].setHorizontalAlignment(SwingConstants.RIGHT);
            }
        }

        else
        {
            //System.out.println("chat not full");

            if (playerNum == 1)
            {
                chats[numChatFull].setText(chatTxt);
                chats[numChatFull].setHorizontalAlignment(SwingConstants.LEFT);
                numChatFull++;
            }

            else
            {
                chats[numChatFull].setText(chatTxt);
                chats[numChatFull].setHorizontalAlignment(SwingConstants.RIGHT);
                numChatFull++;
            }
        }
    }








    /*
    // EVENTS
    // mouse click events - when placing chips
    // text events - when players chat
    */

    public class clickListener extends MouseAdapter
    {
        public int columnNum(MouseEvent event)
        {
            for (int i = 0; i < numCols*numRows; i++)
            {
                if (event.getSource() == grid[i])
                {
                    return ((i % 7) + 1);
                }
            }

            return 0;
        }

        public void mouseClicked(MouseEvent event)
        {
            int num1;
            String num2;

            for (int i = 0; i < numCols; i++)
            {
                if (columnNum(event) == (i + 1) && yourTurn && !won && canPlay && numSpotsCol[i] > 0)
                {
                    addChip(i, numSpotsCol[i], playerNum);
                    numSpotsCol[i]--;
                    num1 = i + 1;
                    num2 = String.valueOf(num1);
                    player.send(num2);
                    yourTurn = false;
                    updateHeader();
                }
            }

        }
    }

    public class eventListener implements ActionListener
    {
        public void actionPerformed(ActionEvent event)
        {
            if (event.getSource() == chatInput)
            {
                String txt = chatInput.getText() + " ";

                //System.out.println("Got text input");
                if (txt.length() > maxChat)
                {
                    addLongerString(txt, playerNum);
                }

                else
                {
                    addText(txt, playerNum, true);
                }

                player.send(txt);
                chatInput.setText("");
            }

            if (event.getSource() == playAgainButton && !notAgain)
            {
                playAgain = true;
                startNewGame();
            }

            if (event.getSource() == playButton)
            {
                startWindow.dispose();
                canPlay = true;
            }

            if (event.getSource() == exit1)
            {
                playAgain = false;
                startNewGame();
            }

            if (event.getSource() == exit2)
            {
                exitWindow.dispose();
                window.dispose();
                window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
            }
        }
    }

    public class windowListener implements ComponentListener
    {
        public void componentHidden(ComponentEvent event) {};
        public void componentResized(ComponentEvent event) {};
        public void componentShown(ComponentEvent event) {};


        public void componentMoved(ComponentEvent event)
        {
            //System.out.println("moved");

            if (event.getSource() == window && !canPlay)
            {
                startWindow.setLocationRelativeTo(window);
            }
        }
    }








    /*
    // INITIALIZERS
    // chat initializer - initializes the chat box between players
    // board initializer - creates the board and buttons to
    // handle all the chip placement
    // start game initializer - creates the start of the game
    // and calls the other initializers
    */

    void chatInitializer()
    {
        numChatFull = 0;
        chatFull = false;
        JPanel chat = new JPanel();
        chat.setLayout(new GridLayout(numChat + 1, 1));

        // fill a bunch of blank texts for now
        // and then as texts come in they will fill the spaces

        chats = new JLabel[numChat];

        for (int i = 0; i < numChat; i++)
        {
            JLabel blank = new JLabel("");
            chats[i] = blank;
            chat.add(blank);
        }

        chatInput = new JTextField(15);
        chatInput.addActionListener(listen2);
        chat.add(chatInput);
        window.add(chat, BorderLayout.EAST);
    }

    void boardInitializer()
    {
        board = new JPanel(new GridLayout(6,7));
        board2 = new JPanel(new BorderLayout());

        // add buttons to board

        numSpotsCol = new int[numCols];
        grid = new JLabel[numCols*numRows];
        grid2 = new int[numCols*numRows];

        for (int i = 0; i < numCols; i++)
        {
            numSpotsCol[i] = numRows;
        }

        for (int i = 0; i < (numCols*numRows); i++)
        {
            JLabel gridSquare = new JLabel();
            gridSquare.setIcon(blank);
            gridSquare.addMouseListener(listen);
            board.add(gridSquare);
            grid[i] = gridSquare;
            grid2[i] = 0;
        }

        turn = new JLabel();
        turn.setIcon(player1turn);
        board2.add(turn, BorderLayout.NORTH);
        board2.add(board, BorderLayout.SOUTH);
    }

    void startWindow()
    {
        startWindow = new JFrame("Welcome!");
        startWindow.setLayout(new BorderLayout());

        // rules

        String playerTxt1 = "You are Player ";
        playerTxt1 = playerTxt1 + playerNum;
        playerTxt1 = playerTxt1 + "!";
        JLabel playerTxt2 = new JLabel("Player 1 goes first.", SwingConstants.CENTER);
        JLabel playerTxt3 = new JLabel(playerTxt1, SwingConstants.CENTER);

        playButton = new JButton("Play!");
        playButton.addActionListener(listen2);

        JPanel top = new JPanel (new BorderLayout());
        top.add(playerTxt3, BorderLayout.NORTH);
        top.add(playerTxt2, BorderLayout.SOUTH);

        startWindow.add(top, BorderLayout.NORTH);
        startWindow.add(playButton, BorderLayout.SOUTH);
        startWindow.setSize(150, 100);
        startWindow.setLocationRelativeTo(window);
        startWindow.setAlwaysOnTop(true);
    }

    void startGameInitialize()
    {
        window.setLayout(new BorderLayout());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ComponentListener c = new windowListener();
        window.addComponentListener(c);

        canPlay = false;
        won = false;
        done = false;
        initiatingGame = false;
        playAgain = false;
        otherPlayAgain = false;
        listen = new clickListener();
        listen2 = new eventListener();

        startWindow();
        boardInitializer();
        window.add(board2, BorderLayout.WEST);
        chatInitializer();

        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        startWindow.setVisible(true);
        //System.out.println("Window should be visible");

        GameRound game = new GameRound();
        Thread t;
        t = new Thread(game);
        t.start();

        try
        {
            t.join();
        }

        catch(InterruptedException e)
        {
            System.out.println("Error playing rounds.");
            System.exit(2);
        }

        System.out.println("ending main...");
    }








    /*
    // Connect Four game constructor
    // starting window with rules and how to play
    // also wait for client on this window
    */

    ConnectFour(PlayerSocket playerIn)
    {
        player = playerIn;
        playerNum = player.getPlayerNum();

        //System.out.println(playerNum);

        if (playerNum == 1)
        {
            yourTurn = true;
            otherPlayerNum = 2;
        }

        else
        {
            yourTurn = false;
            otherPlayerNum = 1;
        }

        URL url1 = getClass().getResource("/images/blankGrid.jpeg");
        URL url2 = getClass().getResource("/images/redChip.jpeg");
        URL url3 = getClass().getResource("/images/yellowChip.jpeg");
        URL url4 = getClass().getResource("/images/player1turn.jpeg");
        URL url5 = getClass().getResource("/images/player2turn.jpeg");
        blank = new ImageIcon(url1, "blank");
        redChip = new ImageIcon(url2, "red chip");
        yellowChip = new ImageIcon(url3, "yellow chip");
        player1turn = new ImageIcon(url4, "player1turn");
        player2turn = new ImageIcon(url5, "player2turn");

        window = new JFrame("Connect Four");
        startGameInitialize();
    }
}

