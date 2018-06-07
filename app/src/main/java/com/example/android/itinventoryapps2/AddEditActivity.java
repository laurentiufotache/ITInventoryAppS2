package com.example.android.itinventoryapps2;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.itinventoryapps2.data.Contract;

import java.util.Locale;

import static com.example.android.itinventoryapps2.data.Contract.ItEntry.COLUMN_NAME;
import static com.example.android.itinventoryapps2.data.Contract.ItEntry.COLUMN_PRICE;
import static com.example.android.itinventoryapps2.data.Contract.ItEntry.COLUMN_QUANTITY;
import static com.example.android.itinventoryapps2.data.Contract.ItEntry.COLUMN_SUPPLIER_NAME;
import static com.example.android.itinventoryapps2.data.Contract.ItEntry.COLUMN_SUPPLIER_PHONE;

public class AddEditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the it device data loader
     */
    private static final int EXISTING_IT_LOADER = 0;

    /**
     * Content URI for the existing it device (null if it's a new it device)
     */
    private Uri mCurrentItUri;

    /**
     * EditText field to enter the it product name
     */
    private EditText mName;

    /**
     * EditText field to enter the price product
     */
    private EditText mPrice;

    /**
     * EditText field to enter the quantity of the product
     */
    private EditText mQuantity;

    /**
     * EditText field to enter the supplier name
     */
    private EditText mSupplierName;

    /**
     * EditText field to enter the supplier phone
     */
    private EditText mSupplierPhone;

    /**
     * Boolean flag that keeps track of whether the it device has been edited (true) or not (false)
     */
    private boolean mItHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mItHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new it device or editing an existing one.
        Intent intent = getIntent();
        mCurrentItUri = intent.getData();

        // If the intent DOES NOT contain the it device content URI, then we know that we are
        // creating the new it device.
        if (mCurrentItUri == null) {
            // This is the new it device, so change the app bar to say "Add a new IT device"
            setTitle("Add a new IT device");

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete the it device that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing it device, so change app bar to say "Edit IT device"
            setTitle("Edit IT device");

            // Initialize a loader to read the data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_IT_LOADER, null, this);
        }

        mName = findViewById(R.id.it_name);
        mPrice = findViewById(R.id.it_price);
        mQuantity = findViewById(R.id.it_quantity);
        mSupplierName = findViewById(R.id.supplier_name);
        mSupplierPhone = findViewById(R.id.supplier_phone);
        Button mIncrease = findViewById(R.id.increase_button);
        Button mDecrease = findViewById(R.id.decrease_button);
        Button mOrder = findViewById(R.id.order_button);


        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mName.setOnTouchListener(mTouchListener);
        mPrice.setOnTouchListener(mTouchListener);
        mQuantity.setOnTouchListener(mTouchListener);
        mSupplierName.setOnTouchListener(mTouchListener);
        mSupplierPhone.setOnTouchListener(mTouchListener);
        mIncrease.setOnTouchListener(mTouchListener);
        mDecrease.setOnTouchListener(mTouchListener);

        mIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = mQuantity.getText().toString();
                if (TextUtils.isEmpty(quantity)) {
                    mQuantity.setText("1");
                } else {
                    int not_null_quantity = Integer.parseInt(mQuantity.getText().toString().trim());
                    not_null_quantity++;
                    mQuantity.setText(String.valueOf(not_null_quantity));
                }
            }
        });

        mDecrease.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String quantity = mQuantity.getText().toString();
                if (TextUtils.isEmpty(quantity)) {
                    mQuantity.setText("0");
                } else {
                    int new_quantity = Integer.parseInt(mQuantity.getText().toString().trim());
                    if (new_quantity > 0) {
                        new_quantity--;
                        mQuantity.setText(String.valueOf(new_quantity));
                    } else {
                        Toast.makeText(AddEditActivity.this, "The quantity cannot be negative!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mSupplierPhone.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Get user input from EditText views and save IT Product details into database.
     */
    private void saveItDevice() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mName.getText().toString().trim();

        String priceString = mPrice.getText().toString().trim();

        String quantityString = mQuantity.getText().toString().trim();

        String supplierNameString = mSupplierName.getText().toString().trim();

        String supplierPhoneString = mSupplierPhone.getText().toString().trim();

        // Check if this is supposed to be a new it device
        // and check if all the fields in the editor are blank
        if (mCurrentItUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(supplierPhoneString)) {
            // Since no fields were modified, we can return early without creating a new it device.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            Toast.makeText(this, R.string.nothing_was_changed, Toast.LENGTH_SHORT).show();
            // Exit activity
            finish();
            return;
        }

        if (TextUtils.isEmpty(nameString)) {
            mName.requestFocus();
            mName.setError(getString(R.string.empty_field_error));
            Toast.makeText(this, getString(R.string.enter_the_it_product_name), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(priceString)) {
            mPrice.requestFocus();
            mPrice.setError(getString(R.string.empty_field_error));
            Toast.makeText(this, getString(R.string.enter_the_price_for_the_it_product), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(quantityString)) {
            mQuantity.requestFocus();
            mQuantity.setError(getString(R.string.empty_field_error));
            Toast.makeText(this, getString(R.string.enter_the_quantity_for_the_it_product), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(supplierNameString)) {
            mSupplierName.requestFocus();
            mSupplierName.setError(getString(R.string.empty_field_error));
            Toast.makeText(this, getString(R.string.enter_the_supplier_name), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(supplierPhoneString)) {
            mSupplierPhone.requestFocus();
            mSupplierPhone.setError(getString(R.string.empty_field_error));
            Toast.makeText(this, getString(R.string.enter_the_supplier_phone_number), Toast.LENGTH_LONG).show();
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and it products attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, nameString);
        float priceFloat = Float.parseFloat(priceString);
        values.put(COLUMN_PRICE, priceFloat);
        values.put(COLUMN_QUANTITY, quantityString);
        values.put(COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(COLUMN_SUPPLIER_PHONE, supplierPhoneString);

        // Determine if this is a new or existing it device by checking if mCurrentItUri is null or not
        if (mCurrentItUri == null) {

            // This is a new it device, so insert a new it device into the provider,
            // returning the content URI for the new it device.
            Uri newUri = getContentResolver().insert(Contract.ItEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, R.string.save_error,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, R.string.added_msg,
                        Toast.LENGTH_SHORT).show();
                // Exit activity
                finish();
            }
        } else {
            // Otherwise this is an existing it device, so update the it device with content URI: mCurrentItUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentItUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentItUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, R.string.update_error,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, R.string.updated_msg,
                        Toast.LENGTH_SHORT).show();
            }
            // Exit activity
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/add_edit_menu.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.add_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new it device, hide the "Delete" menu item.
        if (mCurrentItUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save it device to database
                saveItDevice();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the it device hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mItHasChanged) {
                    NavUtils.navigateUpFromSameTask(AddEditActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(AddEditActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the it device hasn't changed, continue with handling back button press
        if (!mItHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all the it device attributes, define a projection that contains
        // all columns from the it table
        String[] projection = {
                Contract.ItEntry._ID,
                Contract.ItEntry.COLUMN_NAME,
                Contract.ItEntry.COLUMN_PRICE,
                Contract.ItEntry.COLUMN_QUANTITY,
                Contract.ItEntry.COLUMN_SUPPLIER_NAME,
                Contract.ItEntry.COLUMN_SUPPLIER_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentItUri,         // Query the content URI for the current it device
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of the it device attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(Contract.ItEntry.COLUMN_NAME);
            int priceColumnIndex = cursor.getColumnIndex(Contract.ItEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(Contract.ItEntry.COLUMN_QUANTITY);
            int supplier_nameColumnIndex = cursor.getColumnIndex(Contract.ItEntry.COLUMN_SUPPLIER_NAME);
            int supplier_phoneColumnIndex = cursor.getColumnIndex(Contract.ItEntry.COLUMN_SUPPLIER_PHONE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            Float price = cursor.getFloat(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier_name = cursor.getString(supplier_nameColumnIndex);
            String phone = cursor.getString(supplier_phoneColumnIndex);

            // Update the views on the screen with the values from the database
            mName.setText(name);
            mPrice.setText(String.format(Float.toString(price), Locale.getDefault()));
            mQuantity.setText(String.format(Integer.toString(quantity), Locale.getDefault()));
            mSupplierName.setText(supplier_name);
            mSupplierPhone.setText(phone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mName.setText(R.string.blank);
        mPrice.setText(R.string.blank);
        mQuantity.setText(R.string.blank);
        mSupplierName.setText(R.string.blank);
        mSupplierPhone.setText(R.string.blank);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_your_changes_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the it device.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the it device.
                deleteItDevice();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the it device.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Delete the it device from the database.
     */
    private void deleteItDevice() {
        // Only perform the delete if this is an existing it device.
        if (mCurrentItUri != null) {
            // Call the ContentResolver to delete the it device at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentItUri
            // content URI already identifies the it device that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentItUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, R.string.deleting_error_msg,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, R.string.device_deleted,
                        Toast.LENGTH_SHORT).show();
            }
            // Close the activity
            finish();
        }
    }
}
