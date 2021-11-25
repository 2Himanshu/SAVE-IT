package com.example.savekaro.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.savekaro.R;
import com.example.savekaro.data.LinkContract.LinkEntry;
import com.example.savekaro.data.LinksDbHelper;

public class RecycleAdapter extends CursorAdapter
{
    private LinksDbHelper mDbHelper;
    public RecycleAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.list_item,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView platform_name = view.findViewById(R.id.platform_name);
        TextView platform_link = view.findViewById(R.id.link);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(LinkEntry.PLATFORM_NAME));
        String link = cursor.getString(cursor.getColumnIndexOrThrow(LinkEntry.PLATFORM_LINK));

        platform_link.setText(link);
        platform_name.setText(name);




    }
}
