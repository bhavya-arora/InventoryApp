package com.bhavya.inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bhavya.inventoryapp.MainActivity;
import com.bhavya.inventoryapp.R;
import com.bhavya.inventoryapp.data.inventoryContract.productEntry;


/**
 * Created by Bhavya Arora on 1/26/2018.
 */

public class productAdapter extends CursorAdapter {
    private Context context;
    private Button saleButton;

    public productAdapter(Context context, Cursor cursor){

        super(context, cursor, 0);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
       return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView productName = (TextView) view.findViewById(R.id.productName);
        TextView productPrice = (TextView) view.findViewById(R.id.productPrice);
        TextView productQuantity = (TextView) view.findViewById(R.id.productQuantity);
        saleButton = (Button) view.findViewById(R.id.saleButton);

        int nameColumnIndex = cursor.getColumnIndex(productEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(productEntry.COLUMN_PRODUCT_PRICE);
        int quantiyColumnIndex = cursor.getColumnIndex(productEntry.COLUMN_PRODUCT_QUANTITY);
        int supplierColumnIndex = cursor.getColumnIndex(productEntry.COLUMN_PRODUCT_SUPPLIER);
        int idColumnIndex = cursor.getColumnIndex(productEntry._ID);

        int productId = cursor.getInt(idColumnIndex);
        int prdctQuantity = cursor.getInt(quantiyColumnIndex);
        String prdctName = cursor.getString(nameColumnIndex);
        int prdctPrice = cursor.getInt(priceColumnIndex);
        long prdctSupplier = cursor.getLong(supplierColumnIndex);

        saleButton.setTag(String.valueOf(productId + ","
                + prdctQuantity + ","
                + prdctName + ","
                + prdctPrice + ","
                + prdctSupplier));
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productTag = (String) view.getTag();
                String[] tags = productTag.split(",");

                String prdctId = tags[0];
                String prdctQuantity = tags[1];
                String prdctName = tags[2];
                String prdctPrice = tags[3];
                String prdctSupplier = tags[4];
                saleButtonCallback(prdctId, prdctQuantity, prdctName, prdctPrice, prdctSupplier);
            }
        });

        productName.setText(cursor.getString(nameColumnIndex));
        productPrice.setText(String.valueOf(cursor.getInt(priceColumnIndex))+ " Rs");
        productQuantity.setText(String.valueOf(prdctQuantity));
    }

    private void saleButtonCallback(String id, String Quantity, String name, String price, String supplier){
        new MainActivity().
                saleButtonClicked(context, id, Quantity, name, price, supplier);
    }

}
