package com.example.android.itinventoryapps2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper for It Inventory App. Manages database creation and version management.
 * <p>
 * Created by lfotache on 25.05.2018.
 */

public class DbHelper extends SQLiteOpenHelper {

    // The name of the database file
    public static final String DATABASE_NAME = "products.db";

    // The version of the database. If we change the database schema, we must increment the database version
    public static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link DbHelper}.
     *
     * @param context of the app
     */
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // This is called when the database is created for the first time.
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a String that contains the SQL statement to create the it table:
        // CREATE TABLE it (_id INTEGER PRIMARY KEY AUTOINCREMENT, product_name TEXT, price INTEGER quantity INTEGER, supplier_name TEXT, supplier_phone_number TEXT );

        String SQL_CREATE_IT_TABLE = "CREATE TABLE " + Contract.ItEntry.TABLE_NAME + " (" +
                Contract.ItEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Contract.ItEntry.COLUMN_NAME + " TEXT NOT NULL," +
                Contract.ItEntry.COLUMN_PRICE + " REAL NOT NULL," +
                Contract.ItEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0," +
                Contract.ItEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL," +
                Contract.ItEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_IT_TABLE);
    }

    //This is called when the database needs to be upgraded.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}

