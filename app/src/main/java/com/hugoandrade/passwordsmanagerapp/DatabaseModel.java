package com.hugoandrade.passwordsmanagerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

public abstract class DatabaseModel {

    @SuppressWarnings("unused") private final static String TAG = DatabaseModel.class.getSimpleName();

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

    protected void retrieveAllPasswordEntries() {
        AsyncTask<Void, Void, List<PasswordEntry>> task = new AsyncTask<Void, Void, List<PasswordEntry>>() {

            @Override
            protected List<PasswordEntry> doInBackground(Void... params) {
                List<PasswordEntry> passwordEntryList = new ArrayList<>();

                Cursor cursor = database.query(PasswordEntry.Entry.TABLE_NAME, null,
                        null, null, null, null, null);

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

    protected void insertPasswordEntry(final String accountName, final String password) {
        AsyncTask<Void, Void, PasswordEntry> task = new AsyncTask<Void, Void, PasswordEntry>() {

            @Override
            protected PasswordEntry doInBackground(Void... params) {
                Cursor c = database.query(PasswordEntry.Entry.TABLE_NAME, null,
                        null, null, null, null, null);

                int nItems = c.getCount();
                c.close();

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(PasswordEntry.Entry.COL_ACCOUNT_NAME, accountName);
                values.put(PasswordEntry.Entry.COL_PASSWORD, password);
                values.put(PasswordEntry.Entry.COL_ORDER, nItems);

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

    protected void deleteAllPasswordEntries(final List<PasswordEntry> passwordEntryList) {
        AsyncTask<Void, Void, List<PasswordEntry>> task = new AsyncTask<Void, Void, List<PasswordEntry>>() {

            @Override
            protected List<PasswordEntry> doInBackground(Void... params) {
                List<PasswordEntry> deletePasswordEntryList = new ArrayList<>();

                for (PasswordEntry passwordEntry : passwordEntryList) {
                    int nRowsAffected = database.delete(
                            PasswordEntry.Entry.TABLE_NAME,
                            PasswordEntry.Entry.COL__ID + " = ?",
                            new String[]{String.valueOf(passwordEntry.id)});

                    if (nRowsAffected != 0)
                        deletePasswordEntryList.add(passwordEntry);
                }

                return deletePasswordEntryList;
            }

            @Override
            protected void onPostExecute(List<PasswordEntry> passwordEntryList) {
                super.onPostExecute(passwordEntryList);
                onDeleteAllPasswordEntries(passwordEntryList);
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    protected void updatePasswordEntryItem(final PasswordEntry passwordEntry) {
        AsyncTask<Void, Void, PasswordEntry> task = new AsyncTask<Void, Void, PasswordEntry>() {

            @Override
            protected PasswordEntry doInBackground(Void... params) {
                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(PasswordEntry.Entry.COL__ID, passwordEntry.id);
                values.put(PasswordEntry.Entry.COL_ACCOUNT_NAME, passwordEntry.accountName);
                values.put(PasswordEntry.Entry.COL_PASSWORD, passwordEntry.password);
                values.put(PasswordEntry.Entry.COL_ORDER, passwordEntry.order);

                int nRowsAffected = database.update(
                        PasswordEntry.Entry.TABLE_NAME, values,
                        PasswordEntry.Entry.COL__ID + " = ?",
                        new String[]{String.valueOf(passwordEntry.id)});

                if (nRowsAffected == 0)
                    return null;
                else
                    return passwordEntry;
            }

            @Override
            protected void onPostExecute(PasswordEntry passwordEntry) {
                super.onPostExecute(passwordEntry);
                onUpdatePasswordEntry(passwordEntry);
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    protected void deletePasswordEntry(final PasswordEntry passwordEntry) {
        AsyncTask<Void, Void, PasswordEntry> task = new AsyncTask<Void, Void, PasswordEntry>() {

            @Override
            protected PasswordEntry doInBackground(Void... params) {
                int nRowsAffected = database.delete(
                        PasswordEntry.Entry.TABLE_NAME,
                        PasswordEntry.Entry.COL__ID + " = ?",
                        new String[]{String.valueOf(passwordEntry.id)});

                if (nRowsAffected == 0)
                    return null;
                else
                    return passwordEntry;
            }

            @Override
            protected void onPostExecute(PasswordEntry passwordEntry) {
                super.onPostExecute(passwordEntry);
                onDeletePasswordEntry(passwordEntry);
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    protected void onGetAllPasswordEntries(List<PasswordEntry> passwordEntryList){
        //No-op
    }

    protected void onInsertPasswordEntry(PasswordEntry passwordEntry){
        //No-op
    }

    protected void onDeleteAllPasswordEntries(List<PasswordEntry> passwordEntryList) {
        //No-op
    }

    protected void onDeletePasswordEntry(PasswordEntry passwordEntry) {
        //No-op
    }

    protected void onUpdatePasswordEntry(PasswordEntry passwordEntry) {
        //No-op
    }


    /**
     * Helper class that actually creates and manages
     * the provider's underlying data repository.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "PasswordManagerApp";
        private static final int DATABASE_VERSION = 1;

        private static final String CREATE_DB_TABLE_PASSWORD_ENTRY =
                " CREATE TABLE " + PasswordEntry.Entry.TABLE_NAME + " (" +
                        " " + PasswordEntry.Entry.COL__ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        " " + PasswordEntry.Entry.COL_ACCOUNT_NAME + " TEXT UNIQUE NOT NULL, " +
                        " " + PasswordEntry.Entry.COL_PASSWORD + " TEXT NOT NULL, " +
                        " " + PasswordEntry.Entry.COL_ORDER + " INTEGER NOT NULL " +
                        " );";

        @SuppressWarnings("unused")
        private final static String TAG = DatabaseHelper.class.getSimpleName();

        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE_PASSWORD_ENTRY);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +  PasswordEntry.Entry.TABLE_NAME);
            onCreate(db);
        }
    }
}
