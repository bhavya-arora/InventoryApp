package com.bhavya.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bhavya.inventoryapp.data.inventoryContract.productEntry;

/**
 * Created by Bhavya Arora on 1/25/2018.
 */

public class productDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "inventory.db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_PRODUCT_TABLE = "CREATE TABLE " + productEntry.TABLE_NAME + " ("
            + productEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + productEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
            + productEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
            + productEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
            +productEntry.COLUMN_PRODUCT_SUPPLIER + " INTEGER NOT NULL);";

    public productDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PRODUCT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
    }
}
