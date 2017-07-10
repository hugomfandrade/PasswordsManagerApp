package com.hugoandrade.passwordsmanagerapp;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class PasswordEntry implements Parcelable {

    public static class Entry {
        public static final String TABLE_NAME = "PasswordEntry";

        public final static String COL__ID = "_id";
        public final static String COL_ACCOUNT_NAME = "AccountName";
        public final static String COL_PASSWORD = "Password";
    }

    public String id;
    public String accountName;
    public String password;

    public PasswordEntry(String id, String accountName, String password) {
        this.id = id;
        this.accountName = accountName;
        this.password = password;
    }

    protected PasswordEntry(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        id = in.readString();
        accountName = in.readString();
        password = in.readString();
    }


    public static final Creator<PasswordEntry> CREATOR = new Creator<PasswordEntry>() {
        @Override
        public PasswordEntry createFromParcel(Parcel in) {
            return new PasswordEntry(in);
        }

        @Override
        public PasswordEntry[] newArray(int size) {
            return new PasswordEntry[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(accountName);
        dest.writeString(password);
    }

    public static PasswordEntry parseFromCursor(Cursor cursor) {
        return new PasswordEntry(
                cursor.getString(cursor.getColumnIndex(Entry.COL__ID)),
                cursor.getString(cursor.getColumnIndex(Entry.COL_ACCOUNT_NAME)),
                cursor.getString(cursor.getColumnIndex(Entry.COL_PASSWORD)));
    }

}
