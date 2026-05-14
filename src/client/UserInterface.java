package client;

import java.util.Scanner;

public class UserInterface {
    private Scanner scanner;

    public UserInterface() {
        this.scanner = new Scanner(System.in);
    }

    public void displayMenu() {
        System.out.println("\n=================================");
        System.out.println("  TRANSPORT RESERVATION SYSTEM   ");
        System.out.println("=================================");
        System.out.println("1. View Available Seats");
        System.out.println("2. Book a Seat");
        System.out.println("3. Exit Terminal");
        System.out.println("=================================");
        System.out.print("Please select an option (1-3): ");
    }

    public int getMenuChoice() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                int choice = Integer.parseInt(input);

                if (choice >= 1 && choice <= 3) {
                    return choice;
                }
                System.out.print("Invalid choice. Please enter a number between 1 and 3: ");
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Character detected. Please enter a number (1-3): ");
            }
        }
    }

    //Prompt for collecting the seat number to build the string command
    public String getSeatSelection() {
        while (true) {
            System.out.print("Enter the Seat Number you wish to reserve (e.g., 1A, 4B): ");
            String seat = scanner.nextLine().trim().toUpperCase();

            if (!seat.isEmpty()) {
                return seat;
            }
            System.out.println("Seat number cannot be blank.");
        }
    }
}