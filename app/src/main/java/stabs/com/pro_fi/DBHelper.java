package stabs.com.pro_fi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Helper class query the android SQLite database.
 */

public class DBHelper extends SQLiteOpenHelper {

    // Singleton instance of the DBHelper
    private static DBHelper mInstance = null;

    // General DB information
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "Profile.db";

    // Profile Table
    public static final String PROFILE_TABLE = "PROFILE_TABLE";
    public static final String PROFILE_ID = "PROFILE_ID";
    public static final String PROFILE_NAME = "PROFILE_NAME";
    public static final String PROFILE_RINGTONE = "PROFILE_RINGTONE";
    public static final String PROFILE_MEDIA = "PROFILE_MEDIA";
    public static final String PROFILE_NOTIFICATIONS = "PROFILE_NOTIFICATIONS";
    public static final String PROFILE_SYSTEM = "PROFILE_SYSTEM";

    // Wifi Table
    public static final String WIFI_TABLE = "WIFI_TABLE";
    public static final String WIFI_NAME = "WIFI_NAME";

    // Create Statements
    private static final String PROFILE_TABLE_CREATE = "CREATE TABLE " + PROFILE_TABLE + "("
            + PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, "
            + PROFILE_NAME + " TEXT UNIQUE, "
            + PROFILE_RINGTONE + " INTEGER, "
            + PROFILE_MEDIA + " INTEGER, "
            + PROFILE_NOTIFICATIONS + " INTEGER, "
            + PROFILE_SYSTEM + " INTEGER);";

    private static final String WIFI_TABLE_CREATE = "CREATE TABLE " + WIFI_TABLE + "("
            + PROFILE_ID + " INTEGER, "
            + WIFI_NAME + " TEXT PRIMARY KEY, "
            + "FOREIGN KEY(" + PROFILE_ID + ") REFERENCES "
            + PROFILE_TABLE + "("+ PROFILE_ID +"));";

    public static DBHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBHelper(context);
        }
        return mInstance;
    }

    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PROFILE_TABLE_CREATE);
        db.execSQL(WIFI_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrades to the db should be done here.
    }


    /**
     * Helper method to insert a profile into the db
     * @param profile the profile to be stored in the db
     * @return the id of the profile in the Profile table
     */
    public long insertProfile(Profile profile, ArrayList<String> wifiList) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PROFILE_NAME, profile.getName());
        values.put(PROFILE_RINGTONE, profile.getRingtone());
        values.put(PROFILE_MEDIA, profile.getMedia());
        values.put(PROFILE_NOTIFICATIONS, profile.getNotification());
        values.put(PROFILE_SYSTEM, profile.getSystem());
        Long result = db.insert(PROFILE_TABLE, null, values);

        for (String wifiName : wifiList) {
            ContentValues wifiValues = new ContentValues();
            wifiValues.put(PROFILE_ID, result.intValue());
            wifiValues.put(WIFI_NAME, wifiName);
            db.insert(WIFI_TABLE, null, wifiValues);
        }

        return result;
    }

    /**
     * Helper method to update the fields of a profile
     * @param profile the profile to update
     * @return True if the number of rows affected is greater than 0.
     */
    public boolean updateProfile(Profile profile, ArrayList<String> wifiList) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PROFILE_NAME, profile.getName());
        values.put(PROFILE_RINGTONE, profile.getRingtone());
        values.put(PROFILE_MEDIA, profile.getMedia());
        values.put(PROFILE_NOTIFICATIONS, profile.getNotification());
        values.put(PROFILE_SYSTEM, profile.getSystem());
        String[] whereArgs =  new String[]{profile.getId() + ""};
        int rowsAffected = db.update(PROFILE_TABLE, values, PROFILE_ID + " =?", whereArgs);

        db.delete(WIFI_TABLE, PROFILE_ID + "=?", new String[]{String.valueOf(profile.getId())});

        for (String wifiName : wifiList) {
            ContentValues wifiValues = new ContentValues();
            wifiValues.put(PROFILE_ID, profile.getId());
            wifiValues.put(WIFI_NAME, wifiName);
            db.insert(WIFI_TABLE, null, wifiValues);
        }
        // for debugging purposes
        return rowsAffected >0;
    }

    public void deleteProfile(Profile profile) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PROFILE_TABLE, PROFILE_ID + "=?", new String[] {String.valueOf(profile.getId())});
        db.delete(WIFI_TABLE, PROFILE_ID + "=?", new String[]{String.valueOf(profile.getId())});
        db.close();

    }

    /**
     * Checks if a new value for a given column in the database is unique
     * @param column: the column to look up, e.g. PROFILE_NAME
     * @param newValue the unique value to search against
     * @param profileId the id the of the profile
     * @return True if newValue is not present in column (is unique), False otherwise
     */
    public boolean isUnique(String column, String newValue, int profileId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String tableName = PROFILE_TABLE;
        if (column.equals(WIFI_NAME)) {
            tableName = WIFI_TABLE;
        }

        String query = "SELECT " + PROFILE_ID + "," + column +
                " FROM " + tableName +
                " WHERE " + column + " = '" + newValue + "'" +
                " AND " + PROFILE_ID + " != '" + profileId + "'";

        Cursor cursor = db.rawQuery(query,null);
        boolean result = cursor.getCount()==0;
        cursor.close();
        return  result;

    }

    /**
     * Helper method to retrieve a profile from the db, based on its id
     * @param profileId the id of the profile to get from the db
     * @return the profile corresponding to the profileId.
     *          returns null if no profile is found with the id profileId.
     */
    public Profile getProfile(int profileId) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(PROFILE_TABLE,
                new String[]{PROFILE_NAME, PROFILE_RINGTONE, PROFILE_MEDIA, PROFILE_NOTIFICATIONS, PROFILE_SYSTEM},
                PROFILE_ID + "=?",
                new String[]{profileId + ""},
                null, null, null, null);


        if(cursor!=null && cursor.moveToNext()) {
            Profile profile =  new Profile(profileId,
                    cursor.getString(cursor.getColumnIndex(PROFILE_NAME)),
                    null,
                    cursor.getInt(cursor.getColumnIndex(PROFILE_RINGTONE)),
                    cursor.getInt(cursor.getColumnIndex(PROFILE_MEDIA)),
                    cursor.getInt(cursor.getColumnIndex(PROFILE_NOTIFICATIONS)),
                    cursor.getInt(cursor.getColumnIndex(PROFILE_SYSTEM)));
            cursor.close();
            return profile;
        }
        return null;
    }

    /**
     * Helper method to retrieve a profile from the db, based on its wifiName
     * @param wifiName the name of the profile's WiFi
     * @return the profile corresponding to the wifiName.
     *          returns null if no profile is found with the wifiName.
     */
    public Profile getProfile(String wifiName) {
        SQLiteDatabase db = this.getWritableDatabase();

        String rawQuery = "SELECT " + PROFILE_ID + ", " + PROFILE_NAME + ", "  + WIFI_NAME + ", "
                + PROFILE_RINGTONE + ", " +  PROFILE_MEDIA + ", " + PROFILE_NOTIFICATIONS + ", "
                + PROFILE_SYSTEM  + " FROM " + PROFILE_TABLE + " OUTER LEFT JOIN " + WIFI_TABLE
                + " USING (" + PROFILE_ID + ") WHERE " + WIFI_NAME + "='" + wifiName + "'";

        Cursor cursor = db.rawQuery(rawQuery, null);

        if (cursor!=null && cursor.moveToNext()) {

            Profile profile = new Profile(
                    cursor.getInt(cursor.getColumnIndex(PROFILE_ID)),
                    cursor.getString(cursor.getColumnIndex(PROFILE_NAME)),
                    cursor.getString(cursor.getColumnIndex(WIFI_NAME)),
                    cursor.getInt(cursor.getColumnIndex(PROFILE_RINGTONE)),
                    cursor.getInt(cursor.getColumnIndex(PROFILE_MEDIA)),
                    cursor.getInt(cursor.getColumnIndex(PROFILE_NOTIFICATIONS)),
                    cursor.getInt(cursor.getColumnIndex(PROFILE_SYSTEM)));
            cursor.close();
            return profile;
        }
        return null;
    }

    /**
     * Retrieves the names of the wifi networks associated with this profile
     * @param profileId the id of the profile
     * @return an Arraylist of all wifis associated with this wifi
     */
    public ArrayList<String> getProfileWifiList(int profileId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> result = new ArrayList<>();

        String rawQuery = "SELECT " + WIFI_NAME + " FROM " + WIFI_TABLE + " WHERE " + PROFILE_ID
                + "=" + profileId;

        Cursor cursor = db.rawQuery(rawQuery, null);

        while(cursor!=null && cursor.moveToNext()) {
            result.add(cursor.getString(cursor.getColumnIndex(WIFI_NAME)));
        }

        if (cursor!=null) {
            cursor.close();
        }

        return result;
    }

    /**
     * Helper methjod to get a list of all profiles in the database
     * @return An arraylist of all profiles, an empty list if there are no profiles.
     */
    public ArrayList<Profile> getAllProfiles() {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<Profile> profiles = new ArrayList<>();

        Cursor cursor = db.query(PROFILE_TABLE,
                new String[]{PROFILE_ID, PROFILE_NAME, PROFILE_RINGTONE, PROFILE_MEDIA, PROFILE_NOTIFICATIONS, PROFILE_SYSTEM},
                null, null, null, null, null, null);

        while (cursor!=null && cursor.moveToNext()) {
            Profile profile =  new Profile(
                    cursor.getInt(cursor.getColumnIndex(PROFILE_ID)),
                    cursor.getString(cursor.getColumnIndex(PROFILE_NAME)),
                    null,
                    cursor.getInt(cursor.getColumnIndex(PROFILE_RINGTONE)),
                    cursor.getInt(cursor.getColumnIndex(PROFILE_MEDIA)),
                    cursor.getInt(cursor.getColumnIndex(PROFILE_NOTIFICATIONS)),
                    cursor.getInt(cursor.getColumnIndex(PROFILE_SYSTEM)));
            profiles.add(profile);
        }

        if (cursor!=null) {
            cursor.close();
        }
        return profiles;
    }
}
