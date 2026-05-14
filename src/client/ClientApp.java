package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientApp {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 8080;

    public static void main(String[] args) {
        System.out.println("--- Launching Passenger Terminal ---");

        UserInterface ui = new UserInterface();

        //Socket connection
        try (
                Socket socket = new Socket(SERVER_IP, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            System.out.println("Connected to the central reservation server successfully.");

            //Read and discard initial server raw instruction string
            in.readLine();

            boolean isRunning = true;
            while (isRunning) {
                //Show menu
                ui.displayMenu();

                //Get user choice
                int choice = ui.getMenuChoice();

                //Process choices
                switch (choice) {
                    case 1:
                        //Translate option 1 to the server as "VIEW"
                        out.println("VIEW");

                        String serverResponse = in.readLine();
                        if (serverResponse.equals("--- Available Seats ---")) {
                            System.out.println("\n" + serverResponse);
                            String seatLine;
                            //Keep printing seats until the server signals completion
                            while (!(seatLine = in.readLine()).equals("END_OF_LIST")) {
                                System.out.println(seatLine);
                            }
                        }
                        break;

                    case 2:
                        //Get the seat number input from the UI
                        String seatNumber = ui.getSeatSelection();

                        //Translate option 2 into the server as "BOOK [SeatNumber]"
                        out.println("BOOK " + seatNumber);

                        //Print response (Success or Error message)
                        System.out.println("\nSERVER RESPONSE: " + in.readLine());
                        break;

                    case 3:
                        //Send "QUIT" command to server
                        out.println("QUIT");
                        System.out.println("\nSERVER RESPONSE: " + in.readLine()); //Reads "Goodbye!"
                        isRunning = false;
                        break;
                }
            }

        } catch (IOException e) {
            System.out.println("\nCRITICAL NETWORK ERROR: Lost connection to the server.");
            System.out.println("Details: " + e.getMessage());
            System.out.println("Please confirm the ServerApp is running and accessible.");
        }

        System.out.println("\nTerminal session closed.");
    }
}