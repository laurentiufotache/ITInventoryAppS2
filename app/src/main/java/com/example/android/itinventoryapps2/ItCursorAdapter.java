package com.example.android.itinventoryapps2;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.itinventoryapps2.data.Contract;

import static com.example.android.itinventoryapps2.data.Contract.ItEntry.COLUMN_NAME;
import static com.example.android.itinventoryapps2.data.Contract.ItEntry.COLUMN_PRICE;

/**
 * * {@link ItCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of it inventory data as its data source. This adapter knows
 * how to create list items for each row of it inventory data in the {@link Cursor}.
 * <p>
 * Created by lfotache on 01.06.2018.
 */

public class ItCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ItCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ItCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the it inventory data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current it device can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.it_name);
        TextView priceTextView = view.findViewById(R.id.it_price);
        TextView quantityTextView = view.findViewById(R.id.it_quantity);

        Button sellButton = view.findViewById(R.id.sell);

        // Find the columns of it device attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(COLUMN_NAME);
        int priceColumnIndex = cursor.getColumnIndex(COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(Contract.ItEntry.COLUMN_QUANTITY);

        // Read the it device attributes from the Cursor for the current it device
        final String itName = cursor.getString(nameColumnIndex);
        final String itPrice = cursor.getString(priceColumnIndex);
        String itQuantity = cursor.getString(quantityColumnIndex);

        // Update the TextViews with the attributes for the current it device
        nameTextView.setText(itName);
        priceTextView.setText(itPrice);
        quantityTextView.setText(itQuantity);

        final int idColumnIndex = cursor.getInt(cursor.getColumnIndex(Contract.ItEntry._ID));
        final int currentQuantityColumnIndex = cursor.getColumnIndex(Contract.ItEntry.COLUMN_QUANTITY);
        final int currentQuantity = Integer.valueOf(cursor.getString(currentQuantityColumnIndex));

        //Sell button which decrease quantity in storage
        sellButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (currentQuantity > 0) {
                    int newCurrentQuantity = currentQuantity - 1;
                    Uri quantityUri = ContentUris.withAppendedId(Contract.ItEntry.CONTENT_URI, idColumnIndex);

                    ContentValues values = new ContentValues();
                    values.put(Contract.ItEntry.COLUMN_QUANTITY, newCurrentQuantity);
                    context.getContentResolver().update(quantityUri, values, null, null);

                    Toast.makeText(context, "The sale was successfully! \nThe new quantity for "+ itName + " is: " + newCurrentQuantity, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "You can't sale, because " + itName + " is out of stock!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
