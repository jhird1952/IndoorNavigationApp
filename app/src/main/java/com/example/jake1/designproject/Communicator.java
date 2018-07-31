package com.example.jake1.designproject;

public class Communicator {

    private boolean takeStairs;
    private boolean takeElevator;
    private String fromLocation;
    private String toLocation;

    Communicator(){

    }

    public void setTakeStairs(boolean stairs) {

        takeStairs = stairs;

    }

    public void setTakeElevator(boolean elevator) {

        takeElevator = elevator;

    }

    public void setFromLocation(String from) {

        fromLocation = from;

    }

    public void setToLocation(String to) {

        toLocation = to;

    }

    public float[] queryServer(){

        return null;

    }

}
