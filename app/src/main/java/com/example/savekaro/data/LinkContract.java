package com.example.savekaro.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

public class LinkContract{

    public static final String CONTENT_AUTHORITY = "com.example.savekaro";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_SET = "links";
    public static final String RECYCLE_PATH = "recycle";

    public LinkContract(){
    }

    public static final class LinkEntry implements BaseColumns{
        public static final String TABLE_NAME = "links";
        public static final String RECYCLE_TABLE = "recycle";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_SET);
        public static final Uri RECYCLE_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,RECYCLE_PATH);


        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_URI+"/"+PATH_SET;
        public static final String  CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_URI+"/"+PATH_SET;

        public static final String _ID = BaseColumns._ID;
        public static final String PLATFORM_NAME = "platform";
        public static final String PLATFORM_LINK = "platform_link";
    }
}
