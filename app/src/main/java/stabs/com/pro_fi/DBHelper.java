package stabs.com.pro_fi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
    public static final String WIFI_NAME = "WIFI_NAME";
    public static final String PROFILE_RINGTONE = "PROFILE_RINGTONE";
    public static final String PROFILE_MEDIA = "PROFILE_MEDIA";
    public static final String PROFILE_NOTIFICATIONS = "PROFILE_NOTIFICATIONS";
    public static final String PROFILE_SYSTEM = "PROFILE_SYSTEM";

    // Create Statements
    private static final String PROFILE_TABLE_CREATE = "CREATE TABLE " + PROFILE_TABLE + "("
            + PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, "
            + PROFILE_NAME + " TEXT, "
            + WIFI_NAME + " TEXT, "
            + PROFILE_RINGTONE + " INTEGER, "
            + PROFILE_MEDIA + " INTEGER, "
            + PROFILE_NOTIFICATIONS + " INTEGER, "
            + PROFILE_SYSTEM + " INTEGER);";

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrades to the db should be done here.
    }


    /**
     * Helper method to insert a profile into the db
     * @param profile the profile to be stored in the db
     */
    public long insertProfile(Profile profile) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PROFILE_NAME, profile.getName());
        values.put(WIFI_NAME, profile.getWifi());
        values.put(PROFILE_RINGTONE, profile.getRingtone());
        values.put(PROFILE_MEDIA, profile.getMedia());
        values.put(PROFILE_NOTIFICATIONS, profile.getNotification());
        values.put(PROFILE_SYSTEM, profile.getSystem());
        return db.insert(PROFILE_TABLE, null, values);
    }

    /**
     * Helper method to update the fields of a profile
     * @param profile the profile to update
     * @return True if the updates were successfully saved
     *          False otherwise
     */
    public boolean updateProfile(Profile profile) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PROFILE_NAME, profile.getName());
        values.put(WIFI_NAME, profile.getWifi());
        values.put(PROFILE_RINGTONE, profile.getRingtone());
        values.put(PROFILE_MEDIA, profile.getMedia());
        values.put(PROFILE_NOTIFICATIONS, profile.getNotification());
        values.put(PROFILE_SYSTEM, profile.getSystem());
        String[] whereArgs =  new String[]{profile.getId() + ""};

        boolean result = false;
        try {
            db.update(PROFILE_TABLE, values, PROFILE_ID + " =?", whereArgs);
            result = true;
        } catch (Exception e) {
            Log.getStackTraceString(e);
        }
        return result;
    }
    public void deleteProfile(Profile profile)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PROFILE_TABLE, PROFILE_ID + " = ?",
                new String[] { String.valueOf(profile.getId()) });
        db.close();

    }

    public boolean isUnique(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query= "SELECT " + PROFILE_NAME +
                      " FROM " + PROFILE_TABLE +
                      " WHERE " + PROFILE_NAME+ " = '" + name + "'";
        Cursor cursor= db.rawQuery(query,null);
        return cursor.getCount()==0;

    }
    public boolean isUniqueWIFI(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query= "SELECT " + WIFI_NAME +
                " FROM " + PROFILE_TABLE +
                " WHERE " + WIFI_NAME+ " = '" + name + "'";
        Cursor cursor= db.rawQuery(query,null);
        return cursor.getCount()==0;

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
                new String[]{PROFILE_NAME, WIFI_NAME, PROFILE_RINGTONE, PROFILE_MEDIA, PROFILE_NOTIFICATIONS, PROFILE_SYSTEM},
                PROFILE_ID + "=?",
                new String[]{profileId + ""},
                null,
                null,
                null,
                null);

        if(cursor!=null && cursor.moveToNext()) {
            return new Profile(profileId,
                    cursor.getString(cursor.getColumnIndex(PROFILE_NAME)),
                    cursor.getString(cursor.getColumnIndex(WIFI_NAME)),
                    cursor.getInt(cursor.getColumnIndex(PROFILE_RINGTONE)),
                    cursor.getInt(cursor.getColumnIndex(PROFILE_MEDIA)),
                    cursor.getInt(cursor.getColumnIndex(PROFILE_NOTIFICATIONS)),
                    cursor.getInt(cursor.getColumnIndex(PROFILE_SYSTEM)));
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

        Cursor cursor = db.query(PROFILE_TABLE,
                new String[]{PROFILE_ID, PROFILE_NAME, WIFI_NAME, PROFILE_RINGTONE, PROFILE_MEDIA, PROFILE_NOTIFICATIONS, PROFILE_SYSTEM},
                WIFI_NAME + "=?",
                new String[]{wifiName},
                null,
                null,
                null,
                null);

        if(cursor!=null && cursor.moveToNext()) {
            return new Profile(
                    cursor.getInt(cursor.getColumnIndex(PROFILE_ID)),
                    cursor.getString(cursor.getColumnIndex(PROFILE_NAME)),
                    cursor.getString(cursor.getColumnIndex(WIFI_NAME)),
                    cursor.getInt(cursor.getColumnIndex(PROFILE_RINGTONE)),
                    cursor.getInt(cursor.getColumnIndex(PROFILE_MEDIA)),
                    cursor.getInt(cursor.getColumnIndex(PROFILE_NOTIFICATIONS)),
                    cursor.getInt(cursor.getColumnIndex(PROFILE_SYSTEM)));
        }
        return null;
    }

    /**
     * Helper methjod to get a list of all profiles in the database
     * @return An arraylist of all profiles, an empty list if there are no profiles.
     */
    public ArrayList<Profile> getAllProfiles() {
        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<Profile> profiles = new ArrayList<>();

        Cursor cursor = db.query(PROFILE_TABLE,
                new String[]{PROFILE_ID, PROFILE_NAME, WIFI_NAME, PROFILE_RINGTONE, PROFILE_MEDIA, PROFILE_NOTIFICATIONS, PROFILE_SYSTEM},
                null, null, null, null, null, null);

        while (cursor!=null && cursor.moveToNext()) {
            Profile profile = new Profile(
                    cursor.getInt(cursor.getColumnIndex(PROFILE_ID)),
                    cursor.getString(cursor.getColumnIndex(PROFILE_NAME)),
                    cursor.getString(cursor.getColumnIndex(WIFI_NAME)),
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
