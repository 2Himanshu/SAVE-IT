package com.example.savekaro;

import static com.example.savekaro.data.LinkContract.LinkEntry.RECYCLE_CONTENT_URI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.savekaro.adapter.LinkCursorAdapter;
import com.example.savekaro.data.LinkContract;
import com.example.savekaro.data.LinksDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.savekaro.data.LinkContract.LinkEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private LinksDbHelper mDbHelper;
    private LinkCursorAdapter mAdapter;
    private static final int ID  = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbHelper = new LinksDbHelper(this);
        mAdapter = new LinkCursorAdapter(getApplicationContext(),null);
        FloatingActionButton fb = findViewById(R.id.floating_point_btn);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Add_Link.class);
                startActivity(intent);
            }
        });

        ListView listView = findViewById(R.id.listView);

        listView.setAdapter(mAdapter);
        getLoaderManager().initLoader(ID,null,this);

        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int positon, long id) {
                Intent intent = new Intent(MainActivity.this,Add_Link.class);
                Uri currentUri = ContentUris.withAppendedId(LinkEntry.CONTENT_URI,id);
                intent.setData(currentUri);
                startActivity(intent);
            }
        });

    }

//    private void displayInfo(){
//        SQLiteDatabase db = mDbHelper.getReadableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(LinkEntry.PLATFORM_NAME,"LinkedIN");
//        values.put(LinkEntry.PLATFORM_LINK,"www.google.com");
//       long newRowID =  db.insert(LinkEntry.TABLE_NAME,null,values);
//        if(newRowID==-1){
//            Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
//        }
//        else
//        {
//            Toast.makeText(getApplicationContext(), "Inserted at "+newRowID, Toast.LENGTH_SHORT).show();
//        }
//    }
    @Override
    protected void onStart() {
        super.onStart();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_all_pets:
                showDeleteAllLinks();
                return true;

            case R.id.recyle:
                recycleItem();
        }
        return super.onOptionsItemSelected(item);
    }

    private void recycleItem() {
        Intent intent = new Intent(MainActivity.this,recycle_activity.class);
        startActivity(intent);
    }

    private void showDeleteAllLinks() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_dialog);
        builder.setPositiveButton(R.string.delete_all_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteAll();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface!=null){
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteAll() {
        getContentResolver().delete(LinkEntry.CONTENT_URI,null,null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String projection[] = {
          LinkEntry._ID,
          LinkEntry.PLATFORM_NAME,
          LinkEntry.PLATFORM_LINK
        };
        return new CursorLoader(getApplicationContext(),
                LinkEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}