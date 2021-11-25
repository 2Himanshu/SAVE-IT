package com.example.savekaro;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.savekaro.adapter.RecycleAdapter;
import com.example.savekaro.data.LinkContract;
import com.example.savekaro.data.LinksDbHelper;

public class recycle_activity extends AppCompatActivity{

    private RecycleAdapter mAdapter;
    private LinksDbHelper mDbHelper;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle);
        mDbHelper = new LinksDbHelper(this);
        db = mDbHelper.getWritableDatabase();
        TextView name = findViewById(R.id.platform_name);
        TextView link = findViewById(R.id.platform_name);

// Get access to the underlying writeable database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
// Query for items from the database and get a cursor back
        Cursor cursor = db.rawQuery("SELECT  * FROM recycle", null);

        ListView lvItems = (ListView) findViewById(R.id.recyleListView);
// Setup cursor adapter using cursor from last step
        RecycleAdapter todoAdapter = new RecycleAdapter(this, cursor);
// Attach cursor adapter to the ListView
        lvItems.setAdapter(todoAdapter);
        View emptyView = findViewById(R.id.empty_view);
        lvItems.setEmptyView(emptyView);

        todoAdapter.changeCursor(cursor);

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long rowId) {



                AlertDialog.Builder adb = new AlertDialog.Builder(recycle_activity.this);
                adb.setTitle("Alert");
                adb.setMessage("Do You Want to recycle it ");

                adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int items = parent.getCount();
                        for(int j = items;j<=items;j++){
                            View views = lvItems.getChildAt(j);
                            String name = ((TextView) view.findViewById(R.id.platform_name)).getText().toString();
                            String link = ((TextView) view.findViewById(R.id.link)).getText().toString();
                            ContentValues values = new ContentValues();
                            values.put(LinkContract.LinkEntry.PLATFORM_NAME,name);
                            values.put(LinkContract.LinkEntry.PLATFORM_LINK,link);
                            getContentResolver().insert(LinkContract.LinkEntry.CONTENT_URI,values);
                        }
                        String id [] = {String.valueOf(rowId)};
                        int rowDeleted = db.delete(LinkContract.LinkEntry.RECYCLE_TABLE,"_id=?",id);
                        if(rowDeleted>0){
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                        refresh();
                    }
                });

                adb.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            if(dialogInterface!=null){
                                dialogInterface.dismiss();
                            }
                    }
                });
                adb.show();

            }

        });

    }

    private void refresh() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
// Query for items from the database and get a cursor back
        Cursor cursor = db.rawQuery("SELECT  * FROM recycle", null);

        ListView lvItems = (ListView) findViewById(R.id.recyleListView);
// Setup cursor adapter using cursor from last step
        RecycleAdapter todoAdapter = new RecycleAdapter(this, cursor);
// Attach cursor adapter to the ListView
        lvItems.setAdapter(todoAdapter);


        todoAdapter.changeCursor(cursor);
    }

}