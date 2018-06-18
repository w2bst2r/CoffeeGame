package com.fenestra.kahvalti.database;

import com.badlogic.gdx.sql.Database;
import com.badlogic.gdx.sql.DatabaseCursor;
import com.badlogic.gdx.sql.DatabaseFactory;
import com.badlogic.gdx.sql.SQLiteGdxException;

import java.util.ArrayList;

/**
 * Created by karim on 5/12/17.
 */

public class CafeDB {
    private Database dbHandler;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME= "Cafe.sqlite";
    private static final String TABLE_SCORE = "SCORE";
    private static final String TABLE_CAFE = "CAFE";
    //score table
    private static final String SCORE_ID = "id";
    private static final String SCORE_SCORE = "score";
    //cafe table
    private static final String CAFE_ID = "id";
    private static final String CAFE_NAME = "name";
    private static final String CAFE_LENGTH = "length";

    //create table query
    private static final String createTableScore = "CREATE TABLE IF NOT EXISTS "+ TABLE_SCORE + "(" +
            SCORE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            SCORE_SCORE + " REAL " + "); " ;
    private static final String createTableCafe = "CREATE TABLE IF NOT EXISTS "+ TABLE_CAFE + "(" +
            CAFE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CAFE_NAME + " TEXT, " + CAFE_LENGTH + " INTEGER " + ");";

    //coffee list
    static String[] cafeList = {
            "cortado", "cortadito", "doppio", "lungo", "affogato",
            "americano", "cappuccino", "ristretto", "macchiato", "espresso", "double espresso",
            "cafe latte", "piccolo latte","mocha", "vienna", "turkish coffee"
    };


    public CafeDB() {
        dbHandler = DatabaseFactory.getNewDatabase(DATABASE_NAME, DATABASE_VERSION, createTableScore, null);
        dbHandler.setupDatabase();
        try{
            dbHandler.openOrCreateDatabase();
            dbHandler.execSQL(createTableCafe);
            closeDatabase();
        } catch (SQLiteGdxException e){
            e.printStackTrace();
        }

        DatabaseCursor cursor = null;
        int count = 0;

        try {
            dbHandler.openOrCreateDatabase();
            cursor = dbHandler.rawQuery("SELECT count(*) FROM " + TABLE_CAFE);
            if(cursor.next())
                count = cursor.getInt(0);
            closeDatabase();
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }

        if(count == 0) {
            for (String s : cafeList) {
                insertData(s, s.length());
            }
        }
    }

    public void insertData(String cafeName, int nameLength) {
        try {
            dbHandler.openOrCreateDatabase();
            dbHandler.execSQL(
                "INSERT INTO " + TABLE_CAFE +
                "(" + CAFE_NAME + ", " + CAFE_LENGTH +
                ") VALUES(\"" + cafeName + "\", " + nameLength + ");"
            );
            closeDatabase();
        } catch(SQLiteGdxException e) {
            e.printStackTrace();
        }
    }

    public void insertData(Score score) {
        try {
            dbHandler.openOrCreateDatabase();
            dbHandler.execSQL (
                            "INSERT INTO " + TABLE_SCORE +
                              "(" + SCORE_SCORE + ") VALUES(" + score.getScore() + ");");
            closeDatabase();
        } catch(SQLiteGdxException e) {
            e.printStackTrace();
        }
    }

    public int highestScore(){
        DatabaseCursor cursor = null;
        int highestScore = 0;
        try {
            dbHandler.openOrCreateDatabase();
           cursor = dbHandler.rawQuery(
                   "SELECT max(score) FROM SCORE");
            if (cursor.next()) {
                highestScore = cursor.getInt(0);
            }

            closeDatabase();
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }
        return highestScore;
    }

    public ArrayList<String> getCafeList() {
        DatabaseCursor cursor;
        ArrayList<String> cafes = new ArrayList<String>();
        try {
            dbHandler.openOrCreateDatabase();
            cursor = dbHandler.rawQuery(
                    "SELECT name FROM CAFE ORDER BY length ASC;"
            );
            while(cursor.next()) {
                if (cursor.getString(0).contains("i"))
                    cafes.add(cursor.getString(0).replace("i", "I").toUpperCase());
                else
                    cafes.add(cursor.getString(0).toUpperCase());
            }
            closeDatabase();
            return cafes;
        } catch (SQLiteGdxException e) {
            e.printStackTrace();
        }
        return cafes;
    }

    public void closeDatabase() {
        try{
            dbHandler.closeDatabase();
        }   catch (SQLiteGdxException e){
            e.printStackTrace();
        }
        //dbHandler = null;
    }
}
