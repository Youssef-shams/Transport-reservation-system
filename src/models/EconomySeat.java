package models;

public class EconomySeat extends TransportSeat {

    //constructor
    public EconomySeat(String seatNumber, double basePrice) {
        super(seatNumber, basePrice);
    }

    //economy seats only charge the base price
    @Override
    public double calculatePrice() {
        return getBasePrice();
    }
}