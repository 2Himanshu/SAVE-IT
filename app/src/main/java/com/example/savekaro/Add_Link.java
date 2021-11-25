package com.example.savekaro;

import com.example.savekaro.data.LinkContract;
import com.example.savekaro.data.LinkContract.LinkEntry;
import com.example.savekaro.data.LinksDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.app.ShareCompat;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.net.nsd.NsdManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Add_Link extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private EditText mPlatFormName;
    private EditText mLink;
    private LinksDbHelper mDbHelper;
    private Uri currentUri;
    private static final int ID= 2;
    private TextView platform_name_text;
    private TextView link_text;
    private boolean mLinkChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mLinkChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_link);

        mPlatFormName = findViewById(R.id.platform_name);
        mLink = findViewById(R.id.link);
        mDbHelper = new LinksDbHelper(this);

         platform_name_text = findViewById(R.id.platform_name);
         link_text = findViewById(R.id.link);

         platform_name_text.setOnTouchListener(mTouchListener);
         link_text.setOnTouchListener(mTouchListener);



        Intent intent = getIntent();
        currentUri = intent.getData();
        if(currentUri==null){
            setTitle("Add a Link");
            invalidateOptionsMenu();
        }
        else
        {
            setTitle("Edit Link");
            getLoaderManager().initLoader(ID,null,this);

        }
    }

    private void saveLink(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String Platform_Name = mPlatFormName.getText().toString().trim();
        String link_name = mLink.getText().toString().trim();

            if(currentUri==null && TextUtils.isEmpty(Platform_Name) && TextUtils.isEmpty(link_name)){
                return;
            }

        ContentValues values = new ContentValues();
        values.put(LinkEntry.PLATFORM_NAME,Platform_Name);
        values.put(LinkEntry.PLATFORM_LINK,link_name);
        
        if(currentUri==null){
            Uri insertUri = getContentResolver().insert(LinkEntry.CONTENT_URI,values);

        }
        else
        {
            int rowAffected = getContentResolver().update(currentUri,values,null,null);
            if(rowAffected==0){
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_link_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save:
                saveLink();
                finish();
                return true;

            case android.R.id.home:
                    if(!mLinkChanged){
                        NavUtils.navigateUpFromSameTask(Add_Link.this);
                        return true;
                    }
                DialogInterface.OnClickListener discardButtonListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(Add_Link.this);
                    }

                };
                showUnsavedListener(discardButtonListener);
                return true;

            case R.id.delete_add_link:
                showDeleteDialog();
                return true;

            case R.id.copy_text:
                copyTextToClipBoard();
                return true;

            case R.id.share:
                shareText();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    private void shareText() {

        TextView linkId = findViewById(R.id.link);

        String linkText = linkId.getText().toString();
        String mimeType = "text/plain";
        ShareCompat.IntentBuilder.from(this)
                .setChooserTitle("Select App to Share")
                .setType(mimeType)
                .setText(linkText)
                .startChooser();

    }


    private void copyTextToClipBoard() {
        TextView linkId = findViewById(R.id.link);
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("text",linkId.getText().toString());
        clipboardManager.setPrimaryClip(data);
        Toast.makeText(getApplicationContext(), "Copied!", Toast.LENGTH_SHORT).show();

    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do You Want to Delete");
        builder.setPositiveButton(R.string.delete_all_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteLink();
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

    private void deleteLink() {
        String platFormName = mPlatFormName.getText().toString();
        String platFormLink = mLink.getText().toString();
        ContentValues values = new ContentValues();
        values.put(LinkEntry.PLATFORM_NAME,platFormName);
        values.put(LinkEntry.PLATFORM_LINK,platFormLink);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(LinkEntry.RECYCLE_TABLE,null,values);
        getContentResolver().delete(currentUri,null,null);
        finish();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String projection[]={
                LinkEntry._ID,
                LinkEntry.PLATFORM_NAME,
                LinkEntry.PLATFORM_LINK
        };

        return new CursorLoader(
                getApplicationContext(),
                currentUri,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor == null && cursor.getCount()<1) return;

        if(cursor.moveToNext()){
            int nameIndex = cursor.getColumnIndex(LinkEntry.PLATFORM_NAME);
            int linkIndex = cursor.getColumnIndex(LinkEntry.PLATFORM_LINK);

            String platform_name = cursor.getString(nameIndex);
            String link = cursor.getString(linkIndex);

            platform_name_text.setText(platform_name);
            link_text.setText(link);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        platform_name_text.setText("");
        link_text.setText("");
    }

    private void showUnsavedListener(DialogInterface.OnClickListener discardButtonListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_link_message);
        builder.setPositiveButton(R.string.discard,discardButtonListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
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

    @Override
    public void onBackPressed() {
        if(!mLinkChanged){
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener dialogInterfaceListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };
        showUnsavedListener(dialogInterfaceListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
         super.onPrepareOptionsMenu(menu);
        if(currentUri==null)
        {
            MenuItem menuItem = menu.findItem(R.id.delete_add_link);
            menuItem.setVisible(false);

            MenuItem copytext = menu.findItem(R.id.copy_text);
            copytext.setVisible(false);

            MenuItem shareText = menu.findItem(R.id.share);
            shareText.setVisible(false);

        }
        return true;
    }
}