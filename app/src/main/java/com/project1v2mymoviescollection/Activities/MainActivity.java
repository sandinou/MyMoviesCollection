package com.project1v2mymoviescollection.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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
    String movie="";

    /**
     *  Creation of the activity
     * @param savedInstanceState
     */
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


        /**
         * Action defined when clicking on an item in the list
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor.moveToPosition(position);
                view.setBackgroundColor(Color.parseColor("#b30000"));
                Intent viewIntent = new Intent(MainActivity.this,ViewMovieActivity.class);
                viewIntent.putExtra("_id",cursor.getInt(cursor.getColumnIndex(DBConstants.ID_COLUMN)));
                viewIntent.putExtra("position",position);
                startActivity(viewIntent);
            }
        });
    }

    /**
     *  Associates the option menu with the layout
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    /**
     * Defines the menu's options to be performed
     * @param item
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            /**
             *  The different ways how to add a movie
             */
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
            /**
             *  For delete all the existing movies
             */
            case R.id.deleteAllMovies:
                if (cursor.moveToFirst()){
                    AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(this);
                    deleteBuilder.setTitle("DELETE ALL MOVIES?");
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
                            Toast.makeText(MainActivity.this, "You canceled to delete all movies", Toast.LENGTH_SHORT).show();
                        }
                    });
                    AlertDialog deleteAlertDialog = deleteBuilder.create();
                    deleteAlertDialog.show();
                    TextView textView = (TextView) deleteAlertDialog.findViewById(android.R.id.message);
                    textView.setTextSize(20);
                    textView.setGravity(Gravity.CENTER);
                }
                else
                    Toast.makeText(MainActivity.this, "You table is already empty !!", Toast.LENGTH_SHORT).show();
                break;
            /**
             *  For delete only the watched movies from the user
             */
            case R.id.deleteWatchedMovies:
                if (!cursor.moveToFirst())
                    Toast.makeText(this, "Your table is empty, so inevitably there are no seen movies !!!\n", Toast.LENGTH_SHORT).show();
                cursor = myMoviesSQLHelper.getReadableDatabase().rawQuery("SELECT * FROM "+DBConstants.TABLE_NAME+" WHERE "+DBConstants.WATCHED_COLUMN+" = 1 ",null);
                if (cursor.moveToFirst()){
                    AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(this);
                    deleteBuilder.setTitle("DELETE ALL WATCHED MOVIES?");
                    deleteBuilder.setIcon(R.drawable.alert_icon);
                    deleteBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            myMoviesSQLHelper.getWritableDatabase().delete(DBConstants.TABLE_NAME,DBConstants.WATCHED_COLUMN+" = 1",null);
                            cursor = myMoviesSQLHelper.getReadableDatabase().query(DBConstants.TABLE_NAME,null,null,null,null,null,null);
                            adapter.swapCursor(cursor);
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "You canceled to delete all watched movies", Toast.LENGTH_SHORT).show();
                        }
                    });
                    AlertDialog deleteAlertDialog = deleteBuilder.create();
                    deleteAlertDialog.show();
                    TextView textView = (TextView) deleteAlertDialog.findViewById(android.R.id.message);
                    textView.setTextSize(20);
                    textView.setGravity(Gravity.CENTER);
                }
                else
                    Toast.makeText(MainActivity.this, "You don't have any watched movies !!", Toast.LENGTH_SHORT).show();
                break;
            /**
             * See only the unwatched movies on the list
             */
            case R.id.see_nowatched_movies:
                cursor = myMoviesSQLHelper.getReadableDatabase().rawQuery("SELECT * FROM "+DBConstants.TABLE_NAME+" WHERE "+DBConstants.WATCHED_COLUMN+" = 0 ",null);
                adapter.swapCursor(cursor);
                break;

            /**
             * See only the already seen movies on the list
             */
            case R.id.see_watch:
                cursor = myMoviesSQLHelper.getReadableDatabase().rawQuery("SELECT * FROM "+DBConstants.TABLE_NAME+" WHERE "+DBConstants.WATCHED_COLUMN+" = 1 ",null);
                adapter.swapCursor(cursor);
                break;

            /**
             *  See all the movies on the list
             */
            case R.id.seeAll:
                cursor = myMoviesSQLHelper.getReadableDatabase().rawQuery("SELECT * FROM "+DBConstants.TABLE_NAME,null);
                adapter.swapCursor(cursor);
                break;

            /**
             *  To search a specific movie
             */
            case R.id.search_movie:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Enter your movie title"); //Set Alert dialog title here

                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                input.setGravity(Gravity.CENTER);
                alert.setView(input);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String title = input.getEditableText().toString();
                        movie=title.toUpperCase();;
                        cursor = myMoviesSQLHelper.getReadableDatabase().rawQuery("SELECT * FROM "+DBConstants.TABLE_NAME+" WHERE "+DBConstants.TITLE_COLUMN+" = \""+movie+"\"",null);
                        if (cursor.getCount()<=0){
                            Toast.makeText(MainActivity.this, "This movie is not in your collection", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            cursor.moveToFirst();
                            Intent editIntent = new Intent(MainActivity.this, ViewMovieActivity.class);
                            editIntent.putExtra("_id",cursor.getInt(cursor.getColumnIndex(DBConstants.ID_COLUMN)));
                            editIntent.putExtra("position",cursor.getPosition());
                            startActivity(editIntent);
                        }
                    }
                });
                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();

                break;
            /**
             * To exist from the app
             */
            case R.id.exit:
                finish();
                break;
            /**
             * To sort movies by name
             */
            case R.id.sortbyname:
                cursor = myMoviesSQLHelper.getReadableDatabase().rawQuery("SELECT * FROM "+DBConstants.TABLE_NAME+" ORDER BY "+DBConstants.TITLE_COLUMN+" ASC",null);
                adapter.swapCursor(cursor);
                break;
            /**
             *  To sort movies by date
             */
            case R.id.sort_from_newest_to_oldest:
                cursor = myMoviesSQLHelper.getReadableDatabase().rawQuery("SELECT * FROM "+DBConstants.TABLE_NAME+" ORDER BY ("+DBConstants.YEAR_COLUMN+") DESC",null);
                adapter.swapCursor(cursor);
                break;
            case R.id.sort_from_oldest_to_newest:
                cursor = myMoviesSQLHelper.getReadableDatabase().rawQuery("SELECT * FROM "+DBConstants.TABLE_NAME+" ORDER BY "+DBConstants.YEAR_COLUMN+" ASC",null);
                adapter.swapCursor(cursor);
                break;
        }
        return true;
    }

    /**
     *  Associates the context menu with the layout
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        currentPosition = ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
        getMenuInflater().inflate(R.menu.movie_menu,menu);
    }

    /**
     *  Defines the menu's options to be performed
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /**
             * Edits an existing movie to modify it
             */
            case R.id.editItem:
                cursor.moveToPosition(currentPosition);
                Intent viewIntent = new Intent(MainActivity.this,ViewMovieActivity.class);
                viewIntent.putExtra("_id",cursor.getInt(cursor.getColumnIndex(DBConstants.ID_COLUMN)));
                viewIntent.putExtra("position",currentPosition);
                startActivity(viewIntent);
                break;
            /**
             * Delete the movie
             */
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
            /**
             *  Share the movie informations
             */
            case R.id.shareItem:
                cursor.moveToPosition(currentPosition);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,new Functions().MovieInformations(cursor));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
        }
        return super.onContextItemSelected(item);
    }

}
