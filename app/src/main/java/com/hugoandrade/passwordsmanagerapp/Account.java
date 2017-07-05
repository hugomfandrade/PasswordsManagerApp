package com.hugoandrade.passwordsmanagerapp;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class Account implements Parcelable {

    public static class Entry {
        public static final String TABLE_NAME = "Account";

        public static final String COL__ID = "_id";
        public static final String COL_USERNAME = "Username";
        public static final String COL_PASSWORD = "Password";
    }

    public String id;
    public String username;
    public String password;

    public Account(String id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    protected Account(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        id = in.readString();
        username = in.readString();
        password = in.readString();
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(username);
        dest.writeString(password);
    }

    public static Account parseFromCursor(Cursor cursor) {
        return new Account(
                cursor.getString(cursor.getColumnIndex(Account.Entry.COL__ID)),
                cursor.getString(cursor.getColumnIndex(Account.Entry.COL_USERNAME)),
                cursor.getString(cursor.getColumnIndex(Account.Entry.COL_PASSWORD)));
    }
}
