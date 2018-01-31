package com.bhavya.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bhavya.inventoryapp.data.inventoryContract.productEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private TextView prdctName;
    private TextView prdctPrice;
    private TextView prdctQuantity;
    private TextView prdctSupplier;

    private Uri mListUri;

    private boolean mProductHasChanged = false;

    private static int PRODUCT_LOADER = 0;

    View.OnTouchListener mListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //check for intent
        mListUri = getIntent().
                getData();
        if(mListUri == null){
            setTitle("Add a Product");
        }
        else{
            Log.i(MainActivity.TAG, "onCreate: "+ mListUri);
            setTitle("Edit Product");
            getLoaderManager().initLoader(PRODUCT_LOADER, null, this);

        }

        prdctName = (TextView) findViewById(R.id.prdctName);
        prdctPrice = (TextView) findViewById(R.id.prdctPrice);
        prdctQuantity = (TextView) findViewById(R.id.prdctQuantity);
        prdctSupplier = (TextView) findViewById(R.id.prdctSupplier);

        prdctName.setOnTouchListener(mListener);
        prdctPrice.setOnTouchListener(mListener);
        prdctSupplier.setOnTouchListener(mListener);
        prdctQuantity.setOnTouchListener(mListener);

    }

    public void saveProduct(){
        String lName = prdctName.getText().toString();
        String lPrice = prdctPrice.getText().toString();
        String lQuantity = prdctQuantity.getText().toString();
        String lSupplier = prdctSupplier.getText().toString();
        if(TextUtils.isEmpty(lName)){
            prdctName.setError("Cannot be Empty");
        }else{
            if(TextUtils.isEmpty(lPrice)){
                prdctPrice.setError("Cannot be Empty");
            }else{
                if(TextUtils.isEmpty(lQuantity)){
                    prdctQuantity.setError("Cannot be Empty");
                }else{
                    if(TextUtils.isEmpty(lSupplier)){
                        prdctSupplier.setError("Cannot be Empty");
                    }else{
                        ContentValues values = new ContentValues();
                        values.put(productEntry.COLUMN_PRODUCT_NAME, lName);
                        values.put(productEntry.COLUMN_PRODUCT_PRICE, Integer.valueOf(lPrice));
                        values.put(productEntry.COLUMN_PRODUCT_QUANTITY, Integer.valueOf(lQuantity));
                        values.put(productEntry.COLUMN_PRODUCT_SUPPLIER, Long.valueOf(lSupplier));

                        if(mListUri == null){
                            //insering vlaue through ContentResolver
                            Uri uri = getContentResolver().insert(productEntry.CONTENT_URI, values);
                            if(uri == null){
                                Toast.makeText(this, "Insertion Failed", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(this, "New Product Added Successfull", Toast.LENGTH_LONG).show();
                            }
                            finish();
                        }else{
                            int rowsAffected = getContentResolver().update(mListUri, values, null, null);
                            if(rowsAffected == 0){
                                Toast.makeText(this, "Updation Unsuccessfull", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(this, "Updation Successfull", Toast.LENGTH_LONG).show();
                            }
                            finish();
                        }
                    }
                }
            }
        }

    }

    public void deleteProduct(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you wanna Delete?");
        builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int rowAffected = getContentResolver().delete(mListUri, null, null);
                if(rowAffected == 0){
                    Toast.makeText(EditorActivity.this, "Deletion unsuccessfull", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(EditorActivity.this, "Deletion Successfull", Toast.LENGTH_LONG);
                    finish();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClick){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard Changes?");
        builder.setPositiveButton("Keep Editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Discard", discardButtonClick);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if(!mProductHasChanged){
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };
        showUnsavedChangesDialog(discardButtonClick);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editoractivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_save:
                saveProduct();
                return true;
            case R.id.action_delete:
                deleteProduct();
                return true;
            case android.R.id.home:
                if(!mProductHasChanged){
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonCLick = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                showUnsavedChangesDialog(discardButtonCLick);
                return true;
            case R.id.action_order:
                String supplier = prdctSupplier.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+supplier));
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(mListUri == null){
            MenuItem item = menu.findItem(R.id.action_delete);
            item.setVisible(false);
            MenuItem item1 = menu.findItem(R.id.action_order);
            item1.setVisible(false);
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.i(MainActivity.TAG, "onCreateLoader: " + mListUri);
        String[] projection = {productEntry._ID, productEntry.COLUMN_PRODUCT_NAME,
                productEntry.COLUMN_PRODUCT_PRICE,
                productEntry.COLUMN_PRODUCT_SUPPLIER,
                productEntry.COLUMN_PRODUCT_QUANTITY};
        return new CursorLoader(this, mListUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor.moveToFirst()){
            int nameColumnIndex = cursor.getColumnIndex(productEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(productEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(productEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(productEntry.COLUMN_PRODUCT_SUPPLIER);

            String name = cursor.getString(nameColumnIndex);
            String price = String.valueOf(cursor.getInt(priceColumnIndex));
            String quantity = String.valueOf(cursor.getInt(quantityColumnIndex));
            String suppplier = String.valueOf(cursor.getLong(supplierColumnIndex));

            prdctName.setText(name);
            prdctPrice.setText(price);
            prdctQuantity.setText(quantity);
            prdctSupplier.setText(suppplier);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        prdctName.setText("");
        prdctPrice.setText("");
        prdctQuantity.setText("");
        prdctSupplier.setText("");
    }
}
