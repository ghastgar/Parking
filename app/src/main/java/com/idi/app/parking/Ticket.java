package com.idi.app.parking;

import android.content.Context;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by pau on 01/01/16.
 */
public class Ticket {

    private static Double PRICE_PER_MINUTE = 0.02;

    private int id;
    private int parkingSpot;
    private Date dateIn;
    private String licensePlate;

    private Date dateOut;
    private double price;

    public Ticket(int spot, String licensePlate, Context context) {
        // if (spot <= 0 || spot > 15) return;
        this.parkingSpot = spot;
        this.licensePlate = licensePlate;
        Calendar c = Calendar.getInstance();
        this.dateIn = c.getTime();
        Log.e("Initial DateIn", dateIn.getTime()+"");
        ParkingTicketOpenHelper db = new ParkingTicketOpenHelper(context);
        this.id = db.addTicket(this.parkingSpot, this.licensePlate, this.dateIn.getTime());
    }

    public Ticket(int id, int spot, String licensePlate, long dateIn) {
        this.id = id;
        this.parkingSpot = spot;
        this.licensePlate = licensePlate;
        this.dateIn = new Date(dateIn);
    }

    public void checkout(Context context) {
        Calendar c = Calendar.getInstance();
        Log.d("Ticket checkout", "IN : " + dateIn + ", OUT : " + dateOut);
        this.dateOut = c.getTime();
        long timeInSeconds = (dateOut.getTime()/1000 - dateIn.getTime()/1000);
        this.price = (timeInSeconds/60)*PRICE_PER_MINUTE;
        Log.d("Ticket checkout", "Time: " + timeInSeconds/60 + " minutes, price: " + price + "€");

        ParkingTicketOpenHelper db = new ParkingTicketOpenHelper(context);
        db.closeOpenTicket(this.id, this.dateOut.getTime(), price+"");
        Log.d("Ticket checkout", "IN : " + dateIn +"("+dateIn.getTime() +")"+ ", OUT : " + dateOut);
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
