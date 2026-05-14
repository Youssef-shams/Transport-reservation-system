package models;

//abstract class defining all transport seats

public abstract class TransportSeat implements Reservable {
    private String seatNumber;
    private double basePrice;
    private boolean isBooked;

    //default constructor
    public TransportSeat(String seatNumber, double basePrice) {
        this.seatNumber = seatNumber;
        this.basePrice = basePrice;
        this.isBooked = false;
    }

    //setter and getter functions

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public boolean isBooked() {
        return isBooked;
    }

    //interface functions definitions

    //function for booking the seat
    @Override
    public boolean bookSeat() {
        if (!isBooked) {
            isBooked = true;
            return true;
        }
        return false;
    }

    //function to retrieve the seat's details
    @Override
    public String getSeatDetails() {
        String status = isBooked ? "Booked" : "Available";
        return "Seat: " + seatNumber + " | Status: " + status + " | Price: $" + calculatePrice();
    }

    //abstract function to calculate the seat's price according to it's type
    public abstract double calculatePrice();
}