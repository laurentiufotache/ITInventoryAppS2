package com.example.android.itinventoryapps2.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by lfotache on 25.05.2018.
 */

public final class Contract {
    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.itinventoryapps2";
    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.itinventoryapps2/itinventoryapps2/ is a valid path for
     * looking at it inventory data. content://com.example.android.itinventoryapps2/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_IT = "itinventoryapps2";

    /**
     * In order to prevent someone from accidentally instantiating the contract class,
     * we should create an empty constructor.
     */
    private Contract() {
    }

    /**
     * Inner class that defines constant values for the it products database table.
     * Each entry in the table represents a single it product.
     */
    public static final class ItEntry implements BaseColumns {

        /**
         * The content URI to access the it inventory data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_IT);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of it devices.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_IT;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single it device.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_IT;


        // Name of the database table for it products
        public static final String TABLE_NAME = "it";

        /**
         * Unique ID number for the it product (only for use in the database table).
         * Type: INTEGER
         */
        public final static String COLUMN_ID = "_id";

        /**
         * Name of the it product.
         * Type: TEXT
         */
        public static final String COLUMN_NAME = "name";

        /**
         * Price of the it product.
         * Type: REAL
         */
        public static final String COLUMN_PRICE = "price";

        /**
         * Quantity of the it product.
         * Type: INTEGER
         */
        public static final String COLUMN_QUANTITY = "quantity";

        /**
         * Name of the it product supplier.
         * Type: TEXT
         */
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";

        /**
         * Phone of the it product supplier.
         * Type: TEXT
         */
        public static final String COLUMN_SUPPLIER_PHONE = "supplier_phone";

    }
}
