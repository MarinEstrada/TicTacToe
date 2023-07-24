package com.marinestrada.tictactoe;
//required packages for importation
import java.util.Scanner;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.awt.Font;// to be able to specify fonts
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

//importing to get images
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.awt.Dimension;

import java.awt.event.MouseListener;

//by immplementing runnable a runnable object is used by default
public class TicTacToe implements Runnable{

    //variables for game

    //note how all variables are private at start,
    //is better practice to start all vairables as private by default
    //slowly make variables protected or public as needed

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

    
    private Font font = new Font("Verdana", Font.BOLD, 32);
    private Font smallerFont = new Font("Verdana", Font.BOLD, 20);
    private Font largerFont = new Font("Verdana", Font.BOLD, 50);

    private String waitingString = "Waiting for another player";
    private String unableToCommunicateWithOppStr = "Unable to communicate with opponent.";
    private String wonString = "You won!!";
    private String enemyWonString = "Fatality...";

    //constructor
    public TicTacToe() {
        System.out.println("Please input the IP: ");
        ip = scanner.nextLine();
        System.out.println("Please input the port: ");
        port = scanner.nextInt();
        // if port is out of bounds, it can not exist
        while(port < 1 || port > 65535){ //note, ports can only be positive shorts
            System.out.println("Invalid port, please input a valid port: ");
            port = scanner.nextInt();
        }

        //next step is to load images
        loadImages();

        painter = new Painter();
        painter.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        if(!connect()) initializeServer();

        frame = new JFrame();
        frame.setTitle("Tic-Tac-Toe");
        frame.setContentPane(painter);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);

        thread = new Thread(this, "TicTacToe");
        thread.start();

    }

    //required when implemting runable
    public void run() {
        while(true) {
            tick();
            painter.repaint();
            if(!circle && !accepted) {
                listenForServerRequest();
            }
        }
    }

    private void render(Graphics g){

    }

    private void tick(){
        if(errors >= 10) unableToCommunicatewithOpponent = true;
        
        if(!yourTurn && !unableToCommunicatewithOpponent) {
            try{
                int space = dis.readInt();
                if(circle) spaces[space] = "X";
                else spaces[space] = "O";
                checkForEnemyWin();
                checkForTie();
                yourTurn = true;
            } catch(IOException e) {
                e.printStackTrace();
                errors++;
            }
        }
    }

    private void checkForWin() {

    }

    private void checkForEnemyWin(){

    }

    private void checkForTie() {

    }

    private void listenForServerRequest() {
        Socket socket = null;
        try{
            socket = serverSocket.accept();
            dos = new DataOutputStream(socket.getOutputStream())
            dis = new DataInputStream(socket.getInputStream());
            accepted = true;
            System.out.println("CLIET REQUESTED TO JOIN, WE HAVE ACCCEPTED");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean connect() {
        try {
            socket = new Socket(ip, port);
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            accepted = true;
        } catch (IOException e) {
            System.out.println("Unable to connect to address: " + ip + ":"+" | Starting a server");
            return false;
        }
        System.out.println("Successfully connected to the server");
        return true;

    }

    private void initializeServer() {
        try{
            serverSocket = new ServerSocket(port, 8, InetAddress.getByName(ip));
        } catch (Exception e) {
            e.printStackTrace();
        }
        yourTurn = true;
        circle = false;
    }

    private void loadImages(){
        try {
            board = ImageIO.read(getClass().getResourceAsStream("/board.png"));
            redX = ImageIO.read(getClass().getResourceAsStream("/redX.png"));
            redCircle = ImageIO.read(getClass().getResourceAsStream("/redCircle.png"));
            blueX = ImageIO.read(getClass().getResourceAsStream("/blueX.png"));
            blueCircle = ImageIO.read(getClass().getResourceAsStream("/blueCircle.png"));
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    //main method
    @SuppressWarnings("unused")
    public static void main(String[] args) {
        TicTacToe ticTacToe = new TicTacToe();
    }

    private class Painter extends JPanel implements MouseListener{
        private static final long serialVersionUID = 1L;

        public Painter(){
            setFocusable(true);
            requestFocus();
            setBackground(Color.WHITE);
            addMouseListener(this); //note how this class extends MouseListener
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            render(g);
        }

        @Override
        public void mouseClicked(MouseEvent e){
            if (accepted) {
                if(yourTurn && !unableToCommunicatewithOpponent && !won && !enemyWon){
                    int x = e.getX() / lengthOfSpace;
                    int y = e.getY() / lengthOfSpace;
                    int position = x + y*3; // time 3 for proper row

                    if(spaces[position] == null) {
                        if(!circle) spaces[position] = "X";
                        else spaces[position] = "O";
                        yourTurn = false;
                        repaint();
                        Toolkit.getDefaultToolkit().sync();

                        try{
                            dos.writeInt(position);
                            dos.flush();
                        }catch(IOException e1){
                            errors++;
                            e1.printStackTrace();
                        }

                        System.out.println("DATA WAS SENT");
                        checkForWin();
                        checkForTie();
                    }
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e){
        }

        @Override
        public void mouseReleased(MouseEvent e){
        }

        @Override
        public void mouseEntered(MouseEvent e){
        }

        @Override
        public void mouseExited(MouseEvent e){
        }

    }
}