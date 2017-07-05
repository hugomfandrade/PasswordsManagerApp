package com.hugoandrade.passwordsmanagerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public abstract class DatabaseModel {

    private final static String TAG = DatabaseModel.class.getSimpleName();

    // Database fields
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    protected void onInitialize(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    protected void open() {
        database = dbHelper.getWritableDatabase();
    }

    protected void close() {
        dbHelper.close();
    }

    protected void retrieveAllPasswordEntries(final String userID) {
        AsyncTask<Void, Void, List<PasswordEntry>> task = new AsyncTask<Void, Void, List<PasswordEntry>>() {

            @Override
            protected List<PasswordEntry> doInBackground(Void... params) {
                List<PasswordEntry> passwordEntryList = new ArrayList<>();

                Cursor cursor = database.query(PasswordEntry.Entry.TABLE_NAME, null,
                        PasswordEntry.Entry.COL_USER_ID + " = ?" , new String[] {userID},
                        null, null, null);

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    passwordEntryList.add(PasswordEntry.parseFromCursor(cursor));
                    cursor.moveToNext();
                }
                // make sure to close the cursor
                cursor.close();

                return passwordEntryList;
            }

            @Override
            protected void onPostExecute(List<PasswordEntry> passwordEntryList) {
                super.onPostExecute(passwordEntryList);
                onGetAllPasswordEntries(passwordEntryList);
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    protected void retrieveAccount(final String username, final String password) {
        AsyncTask<Void, Void, Account> task = new AsyncTask<Void, Void, Account>() {

            @Override
            protected Account doInBackground(Void... params) {
                Cursor cursor = database.query(Account.Entry.TABLE_NAME, null,
                        Account.Entry.COL_USERNAME + " = ?"  + " AND " + Account.Entry.COL_PASSWORD + " = ?",
                        new String[] {username, password},
                        null, null, null);

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Account account = Account.parseFromCursor(cursor);
                    cursor.close();
                    return account;
                }
                // make sure to close the cursor

                cursor.close();
                return null;
            }

            @Override
            protected void onPostExecute(Account account) {
                super.onPostExecute(account);
                onGetAccount(account);
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    protected void insertPasswordEntry(final String userID, final String accountName, final String password) {
        AsyncTask<Void, Void, PasswordEntry> task = new AsyncTask<Void, Void, PasswordEntry>() {

            @Override
            protected PasswordEntry doInBackground(Void... params) {

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(PasswordEntry.Entry.COL_USER_ID, userID);
                values.put(PasswordEntry.Entry.COL_ACCOUNT_NAME, accountName);
                values.put(PasswordEntry.Entry.COL_PASSWORD, password);

                // Insert the new row, returning the primary key value of the new row
                long newRowId = database.insert(PasswordEntry.Entry.TABLE_NAME, null, values);

                Cursor cursor = database.query(PasswordEntry.Entry.TABLE_NAME, null,
                        PasswordEntry.Entry.COL__ID + " = ?", new String[] {String.valueOf(newRowId)},
                        null, null, null);

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    PasswordEntry passwordEntry = PasswordEntry.parseFromCursor(cursor);
                    cursor.close();
                    return passwordEntry;
                }
                // make sure to close the cursor

                cursor.close();
                return null;
            }

            @Override
            protected void onPostExecute(PasswordEntry passwordEntry) {
                super.onPostExecute(passwordEntry);
                onInsertPasswordEntry(passwordEntry);
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    protected void insertAccount(final String username, final String password) {
        AsyncTask<Void, Void, Account> task = new AsyncTask<Void, Void, Account>() {

            @Override
            protected Account doInBackground(Void... params) {

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(Account.Entry.COL_USERNAME, username);
                values.put(Account.Entry.COL_PASSWORD, password);

                // Insert the new row, returning the primary key value of the new row
                long newRowId = database.insert(Account.Entry.TABLE_NAME, null, values);

                Log.e(TAG, "--> " + String.valueOf(newRowId));
                Cursor cursor = database.query(Account.Entry.TABLE_NAME, null,
                        Account.Entry.COL__ID + " = ?", new String[] {String.valueOf(newRowId)},
                        null, null, null);

                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Account account = Account.parseFromCursor(cursor);
                    cursor.close();
                    return account;
                }
                // make sure to close the cursor

                cursor.close();
                return null;
            }

            @Override
            protected void onPostExecute(Account account) {
                super.onPostExecute(account);
                onInsertAccount(account);
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    protected void onGetAllPasswordEntries(List<PasswordEntry> passwordEntryList){
        //No-op
    }

    protected void onInsertAccount(Account account){
        //No-op
    }

    protected void onGetAccount(Account account){
        //No-op
    }

    protected void onInsertPasswordEntry(PasswordEntry passwordEntry){
        //No-op
    }


    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "PasswordManagerApp";
        private static final int DATABASE_VERSION = 1;

        private static final String CREATE_DB_TABLE_ACCOUNT =
                " CREATE TABLE " + Account.Entry.TABLE_NAME + " (" +
                        " " + Account.Entry.COL__ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        " " + Account.Entry.COL_USERNAME + " TEXT UNIQUE NOT NULL, " +
                        " " + Account.Entry.COL_PASSWORD + " TEXT NOT NULL " +
                        " );";

        private static final String CREATE_DB_TABLE_PASSWORD_ENTRY =
                " CREATE TABLE " + PasswordEntry.Entry.TABLE_NAME + " (" +
                        " " + PasswordEntry.Entry.COL__ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        " " + PasswordEntry.Entry.COL_USER_ID + " TEXT NOT NULL, " +
                        " " + PasswordEntry.Entry.COL_ACCOUNT_NAME + " TEXT UNIQUE NOT NULL, " +
                        " " + PasswordEntry.Entry.COL_PASSWORD + " TEXT NOT NULL " +
                        " );";

        @SuppressWarnings("unused")
        private final static String TAG = DatabaseHelper.class.getSimpleName();

        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE_ACCOUNT);
            db.execSQL(CREATE_DB_TABLE_PASSWORD_ENTRY);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +  Account.Entry.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " +  PasswordEntry.Entry.TABLE_NAME);
            onCreate(db);
        }
    }
}
