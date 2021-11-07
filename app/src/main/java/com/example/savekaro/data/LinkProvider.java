package com.example.savekaro.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

import com.example.savekaro.data.LinkContract.LinkEntry;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LinkProvider extends ContentProvider {
    private LinksDbHelper mDbHelper;
    private static UriMatcher mUriMacther = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int LINKS = 100;
    private static final int LINK_ID = 101;

    static {
        mUriMacther.addURI(LinkContract.CONTENT_AUTHORITY,LinkContract.PATH_SET,LINKS);
        mUriMacther.addURI(LinkContract.CONTENT_AUTHORITY,LinkContract.PATH_SET+"/#",LINK_ID);
    }
    @Override
    public boolean onCreate() {
        mDbHelper = new LinksDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        Cursor cursor;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = mUriMacther.match(uri);
        switch (match){
            case LINKS:
                cursor = db.query(LinkEntry.TABLE_NAME,strings,s,strings1,null,null,s1);
                break;

            case LINK_ID:
                s = "_id=?";
                strings1 = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(LinkEntry.TABLE_NAME,strings,s,strings1,null,null,s1);
                break;

            default:
                throw new IllegalArgumentException("cannot Query Unknown uri "+uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = mUriMacther.match(uri);
        switch (match){
            case LINKS:
                return LinkEntry.CONTENT_LIST_TYPE;
            case LINK_ID:
                return LinkEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI "+uri+" with match "+match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(LinkEntry.TABLE_NAME,null,contentValues);

        if(id==-1){
            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
            return null;
        }
        else
        {
            Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final int match = mUriMacther.match(uri);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowDeleted;
        switch (match){
            case LINKS:
                    rowDeleted =  db.delete(LinkEntry.TABLE_NAME,s,strings);
                    break;
            case LINK_ID:
                    s = LinkEntry._ID+"=?";
                    strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                    rowDeleted =  db.delete(LinkEntry.TABLE_NAME,s,strings);
                    break;

            default:
                throw new IllegalArgumentException("Cannot delete url "+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        if(rowDeleted==0){
            Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
        }
        return rowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        final int match = mUriMacther.match(uri);
        int rowUpdated;
        switch (match){
            case LINKS:
                rowUpdated =  updatePet(uri,contentValues,s,strings);
                break;

            case LINK_ID:
                s = LinkEntry._ID+"=?";
                strings = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowUpdated =  updatePet(uri,contentValues,s,strings);
                break;

            default:
                throw new IllegalArgumentException("cannot delete the uri ");
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return rowUpdated;
    }

    private int updatePet(Uri uri, ContentValues contentValues, String s, String[] strings) {
        if(contentValues.containsKey(LinkEntry.PLATFORM_NAME)){
            String platform_name = contentValues.getAsString(LinkEntry.PLATFORM_NAME);
            if(platform_name==null){
                throw new IllegalArgumentException("Requires a Plafform name");
            }
        }

        if(contentValues.containsKey(LinkEntry.PLATFORM_LINK)){
            String link_name = contentValues.getAsString(LinkEntry.PLATFORM_LINK);
            if(link_name==null){
                throw new IllegalArgumentException("Requires a Link");
            }
        }

        if(contentValues.size()==0)return 0;

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        return db.update(LinkEntry.TABLE_NAME,contentValues,s,strings);
    }


}
