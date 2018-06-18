package com.fenestra.kahvalti.database;

/**
 * Created by karim on 5/11/17.
 */

public class Cafe {

    private int id;
    private String name;
    private int length;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }




    public Cafe(String cafeName, int cafeLength) {
        this.name = cafeName;
        this.length = cafeLength;
    }

    public String getCafeName() {
        return name;
    }

    public void setCafeName(String cafeName) {
        this.name = cafeName;
    }

    public int getCafeLength() {
        return length;
    }

    public void setCafeLength(int cafeLength) {
        this.length = cafeLength;
    }
}
