package com.project1v2mymoviescollection.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project1v2mymoviescollection.Constants.And.SQL.DBConstants;
import com.project1v2mymoviescollection.Constants.And.SQL.MyMoviesSQLHelper;
import com.project1v2mymoviescollection.Functions.Functions;
import com.project1v2mymoviescollection.R;

public class ViewMovieActivity extends AppCompatActivity {

    private TextView date,runtime,director,writer,actors,genre,story;
    private ImageView affiche;
    private int id, watched_state;
    private String imageString;
    public  int currentPosition = 0;
    private MyMoviesSQLHelper myMoviesSQLHelper;
    public Cursor cursor;
    private ImageButton watchedIB;
    private boolean watch=true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_movie);

       // title = (TextView)findViewById(R.id.titleTV);
        date = (TextView)findViewById(R.id.dateTV);
        runtime = (TextView)findViewById(R.id.runtimeTV);
        director = (TextView)findViewById(R.id.directorTV);
        writer = (TextView)findViewById(R.id.writerTV);
        actors = (TextView)findViewById(R.id.actorsTV);
        genre = (TextView)findViewById(R.id.genreTV);
        story = (TextView)findViewById(R.id.storyTV);
       // poster = (ImageView)findViewById(R.id.posterIV);
        affiche  =(ImageView) findViewById(R.id.afficheIV);
        watchedIB = (ImageButton) findViewById(R.id.watched_state);

        id = getIntent().getIntExtra("_id",-1);
        currentPosition = getIntent().getIntExtra("position",1);

        myMoviesSQLHelper=new MyMoviesSQLHelper(this);
        cursor = myMoviesSQLHelper.getReadableDatabase().query(DBConstants.TABLE_NAME,null,DBConstants.ID_COLUMN+"=?",new String[]{" "+id},null,null,null);
        cursor.moveToFirst();

        myMoviesSQLHelper.view(date,runtime,director,writer,genre,actors,story,imageString,cursor, affiche,watchedIB);

        setTitle(cursor.getString(cursor.getColumnIndex(DBConstants.TITLE_COLUMN)));

        watchedIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (watch==false){
                    watchedIB.setImageResource(R.drawable.not_watched_red);
                    watch=true;
                    watched_state=0;
                }
                else{
                    watchedIB.setImageResource(R.drawable.watched_red);
                    watch=false;
                    watched_state=1;
                }
                myMoviesSQLHelper.save(ViewMovieActivity.this,id,cursor.getString(cursor.getColumnIndex(DBConstants.TITLE_COLUMN)),cursor.getString(cursor.getColumnIndex(DBConstants.RELEASE_DATE_COLUMN)),cursor.getInt(cursor.getColumnIndex(DBConstants.YEAR_COLUMN)),cursor.getString(cursor.getColumnIndex(DBConstants.RUNTIME_COLUMN)),cursor.getString(cursor.getColumnIndex(DBConstants.DIRECTOR_COLUMN)),cursor.getString(cursor.getColumnIndex(DBConstants.WRITER_COLUMN)),cursor.getString(cursor.getColumnIndex(DBConstants.GENRE_COLUMN)),cursor.getString(cursor.getColumnIndex(DBConstants.ACTORS_COLUMN)),cursor.getString(cursor.getColumnIndex(DBConstants.STORY_COLUMN)),cursor.getString(cursor.getColumnIndex(DBConstants.URL_COLUMN)),cursor.getString(cursor.getColumnIndex(DBConstants.POSTER_COLUMN)),cursor.getString(cursor.getColumnIndex(DBConstants.IMDB_ID_MOVIE_COLUMN)),watched_state);

            }
        });

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        currentPosition = ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
        getMenuInflater().inflate(R.menu.edit_delete_menu,menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editItem:
                cursor.moveToFirst();
                Intent editIntent = new Intent(ViewMovieActivity.this, ManuallyAddEditMovieActivity.class);
                editIntent.putExtra("_id",cursor.getInt(cursor.getColumnIndex(DBConstants.ID_COLUMN)));
                editIntent.putExtra("position",cursor.getPosition());
                startActivity(editIntent);
            break;

            case R.id.deleteItem:
                cursor.moveToPosition(currentPosition);
                cursor.moveToFirst();
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewMovieActivity.this);
                builder.setTitle("    DELETE  ");
                builder.setMessage("Are you really sure you want to delete the sheet: " + cursor.getString(cursor.getColumnIndex(DBConstants.TITLE_COLUMN)) + " ?");
                builder.setIcon(R.drawable.alert_icon);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myMoviesSQLHelper.getWritableDatabase().delete(DBConstants.TABLE_NAME, "_id=?", new String[]{"" + cursor.getInt(cursor.getColumnIndex(DBConstants.ID_COLUMN))});
                        cursor = myMoviesSQLHelper.getReadableDatabase().query(DBConstants.TABLE_NAME, null, null, null, null, null, null);

                        Intent intent = new Intent(ViewMovieActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(ViewMovieActivity.this, "You canceled to delete the sheet", Toast.LENGTH_SHORT).show();
                            }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                textView.setTextSize(18);
            break;

            case R.id.shareItem:
                cursor.moveToPosition(currentPosition);
                Functions.share(this,cursor);
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_delete_menu,menu);
        return true;
    }
}
