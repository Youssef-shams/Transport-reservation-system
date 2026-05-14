package models;

public class VIPSeat extends TransportSeat {
    private double luxuryTax;

    public VIPSeat(String seatNumber, double basePrice, double luxuryTax) {
        super(seatNumber, basePrice);
        this.luxuryTax = luxuryTax;
    }

    // VIP seats add a luxury tax to the base price
    @Override
    public double calculatePrice() {
        return getBasePrice() + luxuryTax;
    }
}