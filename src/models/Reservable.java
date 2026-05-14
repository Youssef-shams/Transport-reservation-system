package models;

//this interface ensures that any class that implements it defines these 2 functions, ensuring the required logic to reserve a seat

public interface Reservable {
    boolean bookSeat();
    String getSeatDetails();
}