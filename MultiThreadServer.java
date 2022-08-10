package com.example.hw6;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;


public class MultiThreadServer extends Application {
    private Statement stmt = null;
    private Connection conn = null;
    String url = "jdbc:sqlite:C:/Users/chris/ItemData";
    /**This method initializes a connection with the database being used**/
    public void startDB(){
        try {
            // Load the JDBC driver - needed in old jdbc

            Class.forName("org.sqlite.JDBC");
            System.out.println("Driver loaded");
            // Establish a connection
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
            // Create a statement
            stmt = conn.createStatement();

            conn.close(); // Closes the connection
        }catch (Exception ex){
            System.out.println(ex);

        }


    }
    // Text area for displaying contents
    private TextArea ta = new TextArea();

    /** Variables used to kee track of the Number of clients connected to the server*/
    private int clientNo = 0;

    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) {
        /** This method starts the connection to the database**/
        startDB();


        /** Create a scene and place it in the stage*/
        Scene scene = new Scene(new ScrollPane(ta), 450, 200);
        primaryStage.setTitle("MultiThreadServer"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage

        /**communicates with the clients**/
        new Thread( () -> {
            try {

                // Create a server socket
                ServerSocket serverSocket = new ServerSocket(8001);
                ta.appendText("MultiThreadServer started at "
                        + new Date() + '\n');

                while (true) {
                    /** Listen for a new connection request*/
                    Socket socket = serverSocket.accept();

                    /** Increase the number of clients each time they join*/
                    clientNo++;

                    Platform.runLater( () -> {
                        /** Display the client number along with the Date that they connected to the server*/
                        ta.appendText("Starting thread for client " + clientNo +
                                " at " + new Date() + '\n');

                        /**Finds the client's host name along with the IP address*/
                        InetAddress inetAddress = socket.getInetAddress();
                        ta.appendText("Client " + clientNo + "'s host name is "
                                + inetAddress.getHostName() + "\n");
                        ta.appendText("Client " + clientNo + "'s IP Address is"
                                + inetAddress.getHostAddress() + "\n");
                    });

                    // Create and start a new thread for the connection
                    new Thread(new HandleAClient(socket)).start();
                }
            }
            catch(IOException ex) {
                System.err.println(ex);
            }
        }).start();
    }

    // Define the thread class for handling new connection
    class HandleAClient implements Runnable {

        private Socket socket; // A connected socket

        /** Constructs a thread */
        public HandleAClient(Socket socket) {
            this.socket = socket;
        }


        /** Runs a thread */
        public void run() {

            try {
                /** Create data input and output streams to communicate bacck and forth through*/
                DataInputStream inputFromClient = new DataInputStream(
                        socket.getInputStream());
                DataOutputStream outputToClient = new DataOutputStream(
                        socket.getOutputStream());


                /**serves the clients continously**/
                while (true) {
                    /**receives the item code and quantity from the client**/
                    String itemCode = inputFromClient.readUTF().trim();
                    System.out.println("from client "+itemCode);
                    int quantity = inputFromClient.readInt();
                    /**establish another connected to the database when the item code is received**/
                    System.out.println(itemCode);
                    conn = DriverManager.getConnection(url);
                    System.out.println("Connection to SQLite has been established.");

                    /**creates a statement object**/

                    stmt = conn.createStatement();
                    String item = "";
                    /**queries the database for the item **/
                    ResultSet resultSet = stmt.executeQuery
                            ("select item_code, item_name, unit_price from Item where item_code ='"+itemCode+"'");

                    // This iterates through the results and print the item data
                    if (resultSet.next()){
                        String serverItemCode = resultSet.getString(1);
                        String serverItemName = resultSet.getString(2);
                        String serverItemPrice = resultSet.getString(3);
                        outputToClient.writeUTF(serverItemCode);
                        outputToClient.writeUTF(serverItemName);
                        outputToClient.writeUTF(serverItemPrice);

                    }

                    Platform.runLater(() -> {
                        ta.appendText("Item Code received: " +
                                itemCode + '\n');
                    });
                }
            }
            catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}