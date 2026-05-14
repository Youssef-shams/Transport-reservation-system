package server;

import models.TransportSeat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private InventoryManager inventory;

    //Constructor passes the specific client connection and the shared inventory
    public ClientHandler(Socket socket, InventoryManager inventory) {
        this.clientSocket = socket;
        this.inventory = inventory;
    }

    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            out.println("Welcome to the Transport Reservation System! Type VIEW to see seats, BOOK [SeatNumber] to reserve, or QUIT to exit.");

            String request;
            //The server stays in this loop, constantly listening to this specific client
            while ((request = in.readLine()) != null) {
                System.out.println("Received command: " + request);

                if (request.equalsIgnoreCase("QUIT")) {
                    out.println("Goodbye!");
                    break; //Exits the loop and disconnects this client
                }
                else if (request.equalsIgnoreCase("VIEW")) {
                    //Send a list of all available seats
                    out.println("--- Available Seats ---");
                    for (Map.Entry<String, TransportSeat> entry : inventory.getAllSeats().entrySet()) {
                        if (!entry.getValue().isBooked()) {
                            out.println(entry.getValue().getSeatDetails());
                        }
                    }
                    out.println("END_OF_LIST");
                }
                else if (request.toUpperCase().startsWith("BOOK ")) {
                    String[] parts = request.split(" ");

                    if (parts.length < 2) {
                        out.println("ERROR: Please specify a seat number. Example: BOOK 1A");
                        continue; //Skips the rest of this loop iteration and waits for a new command
                    }

                    //Extract the seat number
                    String seatNumber = parts[1].toUpperCase();
                    TransportSeat targetSeat = inventory.getSeat(seatNumber);

                    if (targetSeat == null) {
                        out.println("ERROR: Seat " + seatNumber + " does not exist.");
                    } else {
                        synchronized (targetSeat) {
                            if (!targetSeat.isBooked()) {
                                targetSeat.bookSeat();
                                double finalPrice = targetSeat.calculatePrice();

                                //Send success message to the client
                                out.println("SUCCESS: You have successfully booked seat " + seatNumber + " for $" + finalPrice);

                                //Save the permanent record to the text file
                                inventory.logBookingToFile(seatNumber, finalPrice);
                                System.out.println("Logged booking for seat " + seatNumber + " to file.");

                            } else {
                                out.println("ERROR: Sorry, seat " + seatNumber + " is already taken.");
                            }
                        }
                    }
                }
                else {
                    out.println("ERROR: Unknown command. Use VIEW, BOOK [SeatNumber], or QUIT.");
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected unexpectedly.");
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}