package com.example.savekaro.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.savekaro.data.LinkContract.LinkEntry;

public class LinksDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Account_Links.db";
    private static final int DATABASE_VERSION = 1;
    public LinksDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE = "CREATE TABLE "+LinkEntry.TABLE_NAME+ " ("
                + LinkEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                +LinkEntry.PLATFORM_NAME + " TEXT NOT NULL ,"
                +LinkEntry.PLATFORM_LINK+ " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
