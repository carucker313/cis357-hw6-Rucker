package com.example.hw6;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class HelloController {
    /**Intitializing all the variables used in the program**/
    DataInputStream in = null;
    DataOutputStream out = null;
    Socket socket = null;
    double subTotal =0;
    double total = 0;
    double salesTax = 0.06;
    double change =0;
    double tenderedAmt = 0;

            ArrayList<String> storedItems = new ArrayList<>();
    /**This method computes the final price including tax and returns what the val
     * @return DOUBLE**/
    public static double computePrice(double price, double tax) {

        double salesTax = price * tax;
        double total = salesTax + price;
        return Math.round(total*100)/100.0;

    }


    /**initializing all of the GUI controls used in the controller to handle events **/
    @FXML
    private TextField txtItemCode;

    @FXML
    private Button btnConnect;

    @FXML
    private TextArea txtReceipt;

    @FXML
    private Button btnAddItem;

    @FXML
    private TextArea txtArea;

    @FXML
    private TextField txtQuantity;

    @FXML
    private TextField txtConnect;

    @FXML
    private TextField txtPay;

    @FXML
    private Button btnPay;
    @FXML
    private Label txtWarning;

    @FXML
    void txtFieldConnect(ActionEvent event) {

    }
    /**This button clicked event connects the client to the server if the proper value was entered.
     *  If not then it will prompt the user to enter again until connected to the server**/
    @FXML
    void btnConnect(ActionEvent event) {

        try {
            String hostName = txtConnect.getText();
            System.out.println("CONNECTED TO SERVER " + hostName);
            txtWarning.setText("CONNECTED TO SERVER");
            txtWarning.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");
            txtWarning.setVisible(true);

            // Create a socket to connect to the server
            Socket socket = new Socket(hostName.trim(), 8001);

            // Create an input stream to receive data from the server
            in = new DataInputStream(socket.getInputStream());

            // Create an output stream to send data to the server
            out = new DataOutputStream(socket.getOutputStream());


        }
        /**Throws an error if unable to connect to the server**/
        catch (IOException ex) {
            txtWarning.setStyle("-fx-text-fill: red; -fx-font-size: 18px;");
            txtWarning.setText("Connection ERROR");
            txtWarning.setVisible(true);

        }
    }
    /**This button add event checks to see if the Item exist in the DB, if it does then
     * add the item to textarea with a running total/subtotal**/
    @FXML
    void btnAdd(ActionEvent event) throws IOException {
    try {
        txtWarning.setVisible(false);
        String itemCode = txtItemCode.getText().trim().toUpperCase();
        if (itemCode.equals("A001".trim())||itemCode.equals("A002")||itemCode.equals("A003")||itemCode.equals("A004")|| itemCode.equals("A005") || itemCode.equals("A006") || itemCode.equals("A007")
        || itemCode.equals("B001") || itemCode.equals("B002") || itemCode.equals("B003")){
            txtWarning.setVisible(false);
            int quantity = Integer.parseInt(txtQuantity.getText().trim());
            //send to client
            out.writeUTF(itemCode);
            out.writeInt(quantity);
            out.flush();

            //receive from client
            String clientItemCode= in.readUTF();
            String clientItemName = in.readUTF();
            double clientItemPrice= Double.parseDouble(in.readUTF());
            subTotal=  (clientItemPrice*quantity)+subTotal;
            total = computePrice(subTotal,salesTax);
            /**appens all of the items to the text area, while keeping track of the entered items by storing them into an
             * arraylist. The storedItems array will be used to keep track of the receipt**/
            txtArea.appendText(clientItemName+" \t\t\t\t\t\t" + quantity+ " \t\t\t\t\t" +subTotal +"\t\t\t\t"+total+"\n");
            storedItems.add(clientItemName+" \t\t\t\t\t\t" + quantity+ " \t\t\t\t\t$" +clientItemPrice*quantity+"\n");

            System.out.println("from server: " + clientItemCode +" "+ clientItemName +" " + clientItemPrice);
            /**if the user enters the wrong values then prompt them to re-enter valid data s**/
        }else{
            System.out.println("Enter again");
            txtWarning.setText("INVALID PRODUCT CODE: ENTER AGAIN");
            txtWarning.setVisible(true);
        }
        /**if the user enters the wrong values then prompt them to re-enter valid data s**/
    }catch (Exception ex){
        System.out.println("Enter a valid quantity");
        txtWarning.setText("!!ERROR");
        txtWarning.setVisible(true);
    }

    }

    /**This event is called when the user wants to purchase the item, if they enter an amount larger than the total**/
    @FXML
    void btnPayAmount(ActionEvent event) throws IOException{
        tenderedAmt = Double.parseDouble(txtPay.getText().trim());



        /**checks if the user has enough money to make the purchases**/
        if (tenderedAmt>=total){
            txtWarning.setVisible(false);

            /**creating the new scene for the receipt**/
            FXMLLoader fxmlLoader = new FXMLLoader(HW6Rucker.class.getResource("hello-view.fxml"));
            VBox vb = new VBox();
            vb.setPadding(new Insets(20,20,20,20));

            Scene newScene = new Scene(fxmlLoader.load(),592,500);
            Scene newScene2 = new Scene(vb,500,500);

            Label txtReceipt = new Label();
            txtReceipt.setText("Receipt: ");
            vb.getChildren().add(txtReceipt);
            TextArea txtAreaReceipt = new TextArea();
            txtAreaReceipt.setText("Name:\t\t\t\t\t\tQuantity:\t\t\t\t\tPrice:\n");

            /**spitting all of the stored items and appending them to the text area in the scene that shows the receipt s**/
            for (int i = 0; i <storedItems.size() ; i++) {
                storedItems.get(i).split(",");
                txtAreaReceipt.appendText(storedItems.get(i));

            }


            Button btnNewSale = new Button();
            btnNewSale.setText("New Sale");
            vb.getChildren().addAll(txtAreaReceipt,btnNewSale);

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

            HBox hb = new HBox();
            vb.getChildren().add(hb);


            Label lblChangeDisplay = new Label();
            Label lblChange = new Label();
            lblChangeDisplay.setText("Change: ");
            System.out.println(total);

            //Computing the change for the user
            change = tenderedAmt-total;
            lblChange.setText(String.valueOf(Math.round(change*100)/100.0));
            VBox vb2 = new VBox();
            VBox vb3 = new VBox();
            Label lblTenderedDisplay = new Label();
            Label lblTenderedAmount = new Label();
            lblTenderedDisplay.setText("Tendered: ");
            lblTenderedAmount.setText(String.valueOf(tenderedAmt));
            vb3.getChildren().addAll(lblTenderedDisplay,lblTenderedAmount);


            vb2.getChildren().addAll(lblChangeDisplay,lblChange);
            hb.getChildren().addAll(vb2,vb3);
            hb.setSpacing(20);
            /**this button waits for the user click so it can send them back to the main page to continue making sales. while also clearing all  of the data**/
            btnNewSale.setOnAction(e->{
                stage.setScene(newScene);
                storedItems.clear();
                txtAreaReceipt.clear();


            });
            stage.setScene(newScene2);
            stage.show();

        }
        else{
            System.out.println("not enough");
            txtWarning.setStyle("\"-fx-text-fill: red; -fx-font-size: 16px;\"");
            txtWarning.setText("!!INSUFFICIENT FUNDS");

        }

    }

}
