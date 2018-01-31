package com.bhavya.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bhavya.inventoryapp.MainActivity;
import com.bhavya.inventoryapp.data.inventoryContract.productEntry;

/**
 * Created by Bhavya Arora on 1/25/2018.
 */

public class productProvider extends ContentProvider {

    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private productDbHelper mProductDbHelper;

    static{
        sUriMatcher.addURI(inventoryContract.CONTENT_AUTHORITY, inventoryContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(inventoryContract.CONTENT_AUTHORITY, inventoryContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        mProductDbHelper = new productDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mProductDbHelper.getReadableDatabase();
        Log.i(MainActivity.TAG, "query: " +uri);
        int match = sUriMatcher.match(uri);
        Log.i(MainActivity.TAG, "query: "+ match);
        Cursor cursor;

        switch (match){
            case PRODUCTS:
                Log.i(MainActivity.TAG, "query: products");
              cursor = db.query(productEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
              break;
            case PRODUCT_ID:
                Log.i(MainActivity.TAG, "query: Products_id");
                selection = productEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(productEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException();
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Log.i(MainActivity.TAG, "insert: under insert");
        int match = sUriMatcher.match(uri);
        Log.i(MainActivity.TAG, "insert: " + match);
        Uri rUri = null;

        switch (match){
            case PRODUCTS:
              rUri = insertProduct(uri, contentValues);
              break;
            default:
                new IllegalArgumentException();
        }
        return rUri;
    }

    public Uri insertProduct(Uri uri, ContentValues values){
        SQLiteDatabase db = mProductDbHelper.getWritableDatabase();

        Long returnedId = db.insert(productEntry.TABLE_NAME, null, values);
        if(returnedId == -1){
            Log.i("Bhavya", "insertProduct: Insertion Failed");
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, returnedId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase db = mProductDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsAffected = 0;
        switch (match){
            case PRODUCTS:
            rowsAffected =  db.delete(productEntry.TABLE_NAME, s, strings);
             break;
            case PRODUCT_ID:
                s = productEntry._ID + "=?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsAffected = db.delete(productEntry.TABLE_NAME, s, strings);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase db = mProductDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int rowsAffected;
        switch (match){
            case PRODUCTS:
                rowsAffected = db.update(productEntry.TABLE_NAME, contentValues, s, strings);
                getContext().getContentResolver().notifyChange(uri, null);
                return rowsAffected;
            case PRODUCT_ID:
                s = productEntry._ID + "=?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsAffected = db.update(productEntry.TABLE_NAME, contentValues, s, strings);
                getContext().getContentResolver().notifyChange(uri, null);
                return rowsAffected;
            default:
                throw new IllegalArgumentException("Cann't resolve uri "+ uri);
        }
    }
}
