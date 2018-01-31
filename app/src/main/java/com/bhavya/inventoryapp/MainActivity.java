package com.bhavya.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bhavya.inventoryapp.data.inventoryContract.productEntry;
import com.bhavya.inventoryapp.data.productAdapter;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private productAdapter mAdapter;
    private ListView productsList;
    private int PRODUCT_ADAPTER = 0;
    public static String TAG = "Bhavya";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        productsList = (ListView) findViewById(R.id.productsList);
        mAdapter = new productAdapter(this, null);
        productsList.setAdapter(mAdapter);

        productsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                intent.setData(Uri.parse(productEntry.CONTENT_URI + "/" + id));
                startActivity(intent);
            }
        });

        //initialize Loader
        getLoaderManager().initLoader(PRODUCT_ADAPTER, null, this);

    }

    public void saleButtonClicked(Context context, String position, String quantity, String name, String price, String Supplier){

        if(Integer.parseInt(quantity) <= 0){
            Toast.makeText(context, "No More Items Available", Toast.LENGTH_LONG).show();
            return;
        }
        else {
            Uri lUri = Uri.parse(productEntry.CONTENT_URI + "/" + position);
            int lQuatity = Integer.parseInt(quantity);
            --lQuatity;
            ContentValues values = new ContentValues();
            values.put(productEntry.COLUMN_PRODUCT_NAME, name);
            values.put(productEntry.COLUMN_PRODUCT_PRICE, Integer.parseInt(price));
            values.put(productEntry.COLUMN_PRODUCT_QUANTITY, lQuatity);
            values.put(productEntry.COLUMN_PRODUCT_SUPPLIER, Long.parseLong(Supplier));

            int updated = context.getContentResolver().update(lUri, values, null, null);
            if(updated == 0){
                Toast.makeText(context, "Unsuccessfull", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(context,lQuatity + " Left", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void insertDummyData(){
        ContentValues values = new ContentValues();
        values.put(productEntry.COLUMN_PRODUCT_NAME, "Head and Shoulder");
        values.put(productEntry.COLUMN_PRODUCT_PRICE, 12);
        values.put(productEntry.COLUMN_PRODUCT_QUANTITY, 100);
        values.put(productEntry.COLUMN_PRODUCT_SUPPLIER, Long.valueOf("9958122860"));

        getContentResolver().insert(productEntry.CONTENT_URI, values);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = new String[]{productEntry._ID,
                productEntry.COLUMN_PRODUCT_NAME,
                productEntry.COLUMN_PRODUCT_QUANTITY,
                productEntry.COLUMN_PRODUCT_SUPPLIER,
                productEntry.COLUMN_PRODUCT_PRICE};
        return new CursorLoader(this, productEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
