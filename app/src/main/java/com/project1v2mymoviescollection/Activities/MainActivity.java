package com.project1v2mymoviescollection.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.project1v2mymoviescollection.Constants.And.SQL.DBConstants;
import com.project1v2mymoviescollection.Constants.And.SQL.MyMoviesSQLHelper;
import com.project1v2mymoviescollection.Functions.CheckConnection;
import com.project1v2mymoviescollection.Functions.Functions;
import com.project1v2mymoviescollection.Functions.MyMovieAdapter;
import com.project1v2mymoviescollection.R;


public class MainActivity extends AppCompatActivity {

    private MyMoviesSQLHelper myMoviesSQLHelper;
    private Cursor cursor;
    private MyMovieAdapter adapter;
    private ListView listView;
    public SQLiteDatabase database;
    public  int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.moviesLV);

        myMoviesSQLHelper = new MyMoviesSQLHelper(this);
        cursor = myMoviesSQLHelper.getReadableDatabase().query(DBConstants.TABLE_NAME,null,null,null,null,null,null);
        database = myMoviesSQLHelper.getWritableDatabase();
        adapter = new MyMovieAdapter(this,cursor);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                Intent viewIntent = new Intent(MainActivity.this,ViewMovieActivity.class);
                viewIntent.putExtra("_id",cursor.getInt(cursor.getColumnIndex(DBConstants.ID_COLUMN)));
                viewIntent.putExtra("position",position);
                startActivity(viewIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.addMovie:
                AlertDialog.Builder addBuilder = new AlertDialog.Builder(this);
                addBuilder.setTitle("How do you want to create your movie info sheet? ");
                addBuilder.setPositiveButton("Automatically by internet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CheckConnection checkConnection = new CheckConnection(MainActivity.this);
                        if (checkConnection.isNetworkAvailable()){
                            Intent addMovieI = new Intent(MainActivity.this,InternetSearchActivity.class);
                            startActivity(addMovieI);
                        }
                        else
                            Toast.makeText(MainActivity.this, "Please connect to internet !", Toast.LENGTH_SHORT).show();




                       // Intent addMovieI = new Intent(MainActivity.this,InternetSearchActivity.class);
                        //startActivity(addMovieI);
                    }
                }).setNegativeButton("Manually", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent addMovieM = new Intent(MainActivity.this,ManuallyAddEditMovieActivity.class);
                        startActivity(addMovieM);
                    }
                }).setCancelable(true);
                AlertDialog addAlertDialog = addBuilder.create();
                addAlertDialog.show();
                break;
            case R.id.deleteAllMovies:
                if (cursor.moveToFirst()){
                    AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(this);
                    deleteBuilder.setTitle("    DELETE  ");
                    deleteBuilder.setMessage("Are you really sure you want to delete all the movies sheets ?");
                    deleteBuilder.setIcon(R.drawable.alert_icon);
                    deleteBuilder.setIcon(R.drawable.alert_icon);
                    deleteBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            myMoviesSQLHelper.getWritableDatabase().delete(DBConstants.TABLE_NAME,null,null);
                            cursor = myMoviesSQLHelper.getReadableDatabase().query(DBConstants.TABLE_NAME,null,null,null,null,null,null);
                            adapter.swapCursor(cursor);
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "You canceled to delete all the movies sheets", Toast.LENGTH_SHORT).show();
                        }
                    });
                    AlertDialog deleteAlertDialog = deleteBuilder.create();
                    deleteAlertDialog.show();
                    TextView textView = (TextView) deleteAlertDialog.findViewById(android.R.id.message);
                    textView.setTextSize(20);
                }
                if (!cursor.moveToFirst())
                    Toast.makeText(MainActivity.this, "You table is already empty !!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.exit:
                finish();
                break;
            case R.id.sortbyname:
                cursor = myMoviesSQLHelper.getReadableDatabase().rawQuery("SELECT * FROM "+DBConstants.TABLE_NAME+" ORDER BY "+DBConstants.TITLE_COLUMN+" ASC",null);
                adapter.swapCursor(cursor);



                break;
            case R.id.sort_from_newest_to_oldest:
                cursor = myMoviesSQLHelper.getReadableDatabase().rawQuery("SELECT * FROM "+DBConstants.TABLE_NAME+" ORDER BY ("+DBConstants.YEAR_COLUMN+") DESC",null);
                adapter.swapCursor(cursor);
                //adapter = new MyMovieAdapter(this,cursor);
                break;
            case R.id.sort_from_oldest_to_newest:
                cursor = myMoviesSQLHelper.getReadableDatabase().rawQuery("SELECT * FROM "+DBConstants.TABLE_NAME+" ORDER BY "+DBConstants.YEAR_COLUMN+" ASC",null);
                adapter.swapCursor(cursor);
                //adapter = new MyMovieAdapter(this,cursor);
                break;
        }
        //adapter = new MyMovieAdapter(this,cursor);
        return true;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        currentPosition = ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
        getMenuInflater().inflate(R.menu.movie_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editItem:
                cursor.moveToPosition(currentPosition);
                Intent viewIntent = new Intent(MainActivity.this,ViewMovieActivity.class);
                viewIntent.putExtra("_id",cursor.getInt(cursor.getColumnIndex(DBConstants.ID_COLUMN)));
                viewIntent.putExtra("position",currentPosition);
                startActivity(viewIntent);
                break;
            case R.id.deleteItem:
                cursor.moveToPosition(currentPosition);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("    DELETE  ");
                builder.setMessage("Are you really sure you want to delete the sheet: " + cursor.getString(cursor.getColumnIndex(DBConstants.TITLE_COLUMN)) + " ?");
                builder.setIcon(R.drawable.alert_icon);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myMoviesSQLHelper.getWritableDatabase().delete(DBConstants.TABLE_NAME, "_id=?", new String[]{"" + cursor.getInt(cursor.getColumnIndex(DBConstants.ID_COLUMN))});
                        cursor = myMoviesSQLHelper.getReadableDatabase().query(DBConstants.TABLE_NAME, null, null, null, null, null, null);
                        adapter.swapCursor(cursor);                    }
                })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "You canceled to delete the sheet", Toast.LENGTH_SHORT).show();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                textView.setTextSize(18);
                break;
            case R.id.shareItem:
                cursor.moveToPosition(currentPosition);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, Functions.MovieInformations(cursor));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;

        }
        return super.onContextItemSelected(item);
    }
}
