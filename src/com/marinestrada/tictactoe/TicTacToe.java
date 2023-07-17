package com.marinestrada.tictactoe;
//required packages for importation
import java.util.Scanner;
import javax.swing.*;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

//by immplementing runnable a runnable object is used by default
public class TicTacToe implements Runnable{

    //variables for game
    private String ip = "localhost";
    private int port = 2222; //default port
    private Scanner scanner = new Scanner(System.in); // will allow user to use non default port
    private JFrame frame;
    private final int WIDTH = 506;
    private final int HEIGHT = 527;
    private Thread thread;

    private Painter painter;
    private Socket socket; //will be used to connect to the server
    private DataOutputStream dos;
    private DataInputStream dis;

    private ServerSocket serverSocket; //for serverside, will wait for requests

    //gui stuff
    private BufferedImage board;
    private BufferedImage redX;
    private BufferedImage BlueX;
    private BufferedImage redCircle;
    private BufferedImage blueCircle;

    private String[] spaces = new String[9]; // will represent 9 spaces of board

    private boolean yourTurn = false;
    private boolean circle = true;
    private boolean accepted = false;
    private boolean unableToCommunicatewithOpponent = false;
    private boolean won = false;
    private boolean enemyWon = false;

    private int lengthOfSpace = 160; // each square should be lenghOfSpace^2
    private int errors = 0; // if too mnay errors accumulate something is wrong
    private int firstSpot = -1; //used to draw winning line accross 3 sequential shapes
    private int secondSpot = -1;//used to draw winning line accross 3 sequential shapes

    //constructor
    public TicTacToe() {

    }

    //required when implemting runable
    public void run() {

    }
    
    //main method
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        TicTacToe ticTacToe = new TicTacToe();
    }

    public class Painter{

    }
}