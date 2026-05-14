package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        System.out.println("--- Transport Reservation Server Starting ---");

        InventoryManager inventory = new InventoryManager();
        inventory.loadInventoryFromFile("data/inventory.csv");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running and listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("A new client has connected! Handing off to a new thread...");

                //Create a new ClientHandler and start it on a new background thread
                ClientHandler clientHandler = new ClientHandler(clientSocket, inventory);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }

        } catch (IOException e) {
            System.out.println("Server Exception: " + e.getMessage());
        }
    }
}