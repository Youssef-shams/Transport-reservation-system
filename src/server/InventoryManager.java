package server;

import models.EconomySeat;
import models.TransportSeat;
import models.VIPSeat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InventoryManager {
    //HashMap to store all seats by their seat number
    private Map<String, TransportSeat> seatMap;

    public InventoryManager() {
        seatMap = new HashMap<>();
    }

    //Reading from a CSV
    public void loadInventoryFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                //Split the CSV line by commas
                String[] data = line.split(",");

                String seatNumber = data[0];
                String seatType = data[1];
                double basePrice = Double.parseDouble(data[2]);

                //Create the correct object based on the file data
                if (seatType.equalsIgnoreCase("VIP")) {
                    double luxuryTax = Double.parseDouble(data[3]);
                    seatMap.put(seatNumber, new VIPSeat(seatNumber, basePrice, luxuryTax));
                } else if (seatType.equalsIgnoreCase("Economy")) {
                    seatMap.put(seatNumber, new EconomySeat(seatNumber, basePrice));
                }
            }
            System.out.println("Success: Loaded " + seatMap.size() + " seats into inventory.");

        } catch (IOException e) {
            System.out.println("Error reading inventory file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error parsing price data in inventory file.");
        }
    }

    //Method to fetch a specific seat
    public TransportSeat getSeat(String seatNumber) {
        return seatMap.get(seatNumber);
    }

    //Method to get all available seats
    public Map<String, TransportSeat> getAllSeats() {
        return seatMap;
    }

    //The synchronized keyword ensures two threads don't write to the file at the exact same millisecond
    public synchronized void logBookingToFile(String seatNumber, double price) {
        //"true" enables append mode so it doesn't overwrite existing data
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/bookings.txt", true))) {

            //Get the current date and time for the receipt
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = now.format(formatter);

            //Write the log entry
            writer.write("[" + timestamp + "] SUCCESS: Seat " + seatNumber + " booked for $" + price);
            writer.newLine(); // Move to the next line for the next booking

        } catch (IOException e) {
            System.out.println("CRITICAL ERROR: Could not write to bookings.txt - " + e.getMessage());
        }
    }
}