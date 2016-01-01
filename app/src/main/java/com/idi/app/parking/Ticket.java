package com.idi.app.parking;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by pau on 01/01/16.
 */
public class Ticket {

    private static Double PRICE_PER_MINUTE = 0.02;

    private int parkingSpot;
    private Date dateIn;
    private String licensePlate;

    private Date dateOut;
    private double price;

    public Ticket(int spot, String licensePlate) {
        // if (spot <= 0 || spot > 15) return;
        this.parkingSpot = spot;
        this.licensePlate = licensePlate;
        Calendar c = Calendar.getInstance();
        this.dateIn = c.getTime();
    }

    public void checkout() {
        Calendar c = Calendar.getInstance();
        this.dateOut = c.getTime();
        long timeInSeconds = (dateOut.getTime() - dateIn.getTime())/1000;
        this.price = (timeInSeconds/60)*PRICE_PER_MINUTE;
        Log.d("Ticket checkout", "Time: " + timeInSeconds/60 + " minutes, price: " + price + "€");
    }

    public double getPrice() {
        return price;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public int getParkingSpot() {
        return parkingSpot;
    }
}
