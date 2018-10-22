package org.hugoandrade.passwordsmanagerapp.objects;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class PasswordEntry implements Parcelable {

    public String id;
    public String entryName;
    public String accountName;
    public String password;
    public int order;

    public static class Entry {

        public static final String TABLE_NAME = "PasswordEntry";

        public static class Cols {
            public final static String _ID = "_id";
            public final static String ENTRY_NAME = "EntryName";
            public final static String ACCOUNT_NAME = "AccountName";
            public final static String PASSWORD = "Password";
            public final static String ORDER = "OrderNumber";
        }
    }

    public PasswordEntry(String id, String entryName, String accountName, String password, int order) {
        this.id = id;
        this.entryName = entryName;
        this.accountName = accountName;
        this.password = password;
        this.order = order;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PasswordEntry) {
            PasswordEntry passwordEntry = (PasswordEntry) obj;
            return passwordEntry.id.equals(id);
        }
        return super.equals(obj);
    }

    protected PasswordEntry(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        id = in.readString();
        entryName = in.readString();
        accountName = in.readString();
        password = in.readString();
        order = in.readInt();
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
        dest.writeString(entryName);
        dest.writeString(accountName);
        dest.writeString(password);
        dest.writeInt(order);
    }

    public static PasswordEntry parseFromCursor(Cursor cursor) {
        return new PasswordEntry(
                cursor.getString(cursor.getColumnIndex(Entry.Cols._ID)),
                cursor.getString(cursor.getColumnIndex(Entry.Cols.ENTRY_NAME)),
                cursor.getString(cursor.getColumnIndex(Entry.Cols.ACCOUNT_NAME)),
                cursor.getString(cursor.getColumnIndex(Entry.Cols.PASSWORD)),
                cursor.getInt(cursor.getColumnIndex(Entry.Cols.ORDER)));
    }

}
