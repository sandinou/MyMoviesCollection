package com.project1v2mymoviescollection.Constants.And.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.project1v2mymoviescollection.Activities.MainActivity;
import com.project1v2mymoviescollection.Activities.ManuallyAddEditMovieActivity;
import com.project1v2mymoviescollection.Functions.Functions;
import com.project1v2mymoviescollection.R;

/**
 * Created by SandraMac on 15/02/2017.
 */

public class MyMoviesSQLHelper  extends SQLiteOpenHelper{

    private Context context;

    public MyMoviesSQLHelper(Context context) {
        super(context, "mymovies.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ DBConstants.TABLE_NAME+
                " ("+DBConstants.ID_COLUMN+" INTEGER PRIMARY KEY AUTOINCREMENT, "+ DBConstants.TITLE_COLUMN+" TEXT, "
                + DBConstants.RELEASE_DATE_COLUMN+" TEXT, "+DBConstants.YEAR_COLUMN+" INTEGER,"+ DBConstants.RUNTIME_COLUMN+" TEXT, "
                + DBConstants.DIRECTOR_COLUMN+" TEXT, "+ DBConstants.STORY_COLUMN+" TEXT, "
                + DBConstants.URL_COLUMN+" TEXT, "+ DBConstants.WRITER_COLUMN+" TEXT, "
                + DBConstants.ACTORS_COLUMN+" TEXT, "+ DBConstants.GENRE_COLUMN+" TEXT, "
                + DBConstants.POSTER_COLUMN+" TEXT, "+DBConstants.IMDB_ID_MOVIE_COLUMN+" TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + DBConstants.TABLE_NAME);
        onCreate(db);
    }


    /**
     * Function to save all movie's information into database
     * @param title
     * @param releaseDate
     * @param runtime
     * @param director
     * @param writer
     * @param genre
     * @param actors
     * @param storyLine
     * @param url
     * @param imageString
     */
    public void save(Context context,int id, String title, String releaseDate, int year, String runtime,
                            String director, String writer, String genre, String actors, String storyLine, String url, String imageString, String imdbID){


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.TITLE_COLUMN, title);
        contentValues.put(DBConstants.RELEASE_DATE_COLUMN, releaseDate);
        contentValues.put(DBConstants.YEAR_COLUMN,year);
        contentValues.put(DBConstants.RUNTIME_COLUMN, runtime);
        contentValues.put(DBConstants.DIRECTOR_COLUMN, director);
        contentValues.put(DBConstants.WRITER_COLUMN, writer);
        contentValues.put(DBConstants.GENRE_COLUMN, genre);
        contentValues.put(DBConstants.ACTORS_COLUMN, actors);
        contentValues.put(DBConstants.STORY_COLUMN, storyLine);
        contentValues.put(DBConstants.URL_COLUMN, url);
        contentValues.put(DBConstants.POSTER_COLUMN, imageString);
        contentValues.put(DBConstants.IMDB_ID_MOVIE_COLUMN,imdbID);

        if (id == -1) {
            db.insert(DBConstants.TABLE_NAME, null, contentValues);
        } else
            db.update(DBConstants.TABLE_NAME, contentValues, "_id=?", new String[]{"" + id});

        Intent finish = new Intent(context, MainActivity.class);
        finish.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(finish);
    }


    /**
     *  Function to get movie's informations from the database for ViewMovieActivity
     * @param title
     * @param date
     * @param runtime
     * @param director
     * @param writer
     * @param genre
     * @param actors
     * @param story
     * @param imageString
     * @param poster
     * @param cursor
     */
    public static void view(TextView title, TextView date, TextView runtime, TextView director, TextView writer, TextView genre, TextView actors, TextView story, String imageString, ImageView poster, Cursor cursor) {
        //cursor.moveToFirst();
        title.setPaintFlags(title.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        title.setText(cursor.getString(cursor.getColumnIndex(DBConstants.TITLE_COLUMN)));
        date.setText(cursor.getString(cursor.getColumnIndex(DBConstants.RELEASE_DATE_COLUMN)));
        runtime.setText(Functions.minutesInHours(cursor.getString(cursor.getColumnIndex(DBConstants.RUNTIME_COLUMN))));
        director.setText(cursor.getString(cursor.getColumnIndex(DBConstants.DIRECTOR_COLUMN)));
        writer.setText(cursor.getString(cursor.getColumnIndex(DBConstants.WRITER_COLUMN)));
        genre.setText(cursor.getString(cursor.getColumnIndex(DBConstants.GENRE_COLUMN)));
        actors.setText(cursor.getString(cursor.getColumnIndex(DBConstants.ACTORS_COLUMN)));
        story.setText(cursor.getString(cursor.getColumnIndex(DBConstants.STORY_COLUMN)));
        imageString = cursor.getString(cursor.getColumnIndex(DBConstants.POSTER_COLUMN));
        if (imageString != null) {
            Bitmap myBitmapAgain = Functions.decodeBase64(imageString);
            poster.setImageBitmap(myBitmapAgain);
        } else {
            poster.setImageResource(R.drawable.movies_icon);
        }
    }

    /**
     * Function to get movie's informations from the database for ManuallyAddEditMovieActivity and InternetAddActivity
     * @param cursor
     * @param title
     * @param date
     * @param runtime
     * @param director
     * @param writer
     * @param genre
     * @param actors
     * @param story
     * @param url
     * @param imageString
     * @param image
     * @param action
     * @param animation
     * @param adventure
     * @param comedy
     * @param drama
     * @param horror
     * @param western
     * @param thriller
     * @param romance
     * @param sf
     * @param crime
     * @param history
     * @param war
     * @param fantasy
     * @param bio
     */
    public static void editMovie(Cursor cursor, EditText title, TextView date, EditText runtime, EditText director, EditText writer, String genre, EditText actors, EditText story, EditText url,
                                 String imageString, ImageView image, CheckBox action, CheckBox animation, CheckBox adventure, CheckBox comedy, CheckBox drama, CheckBox horror,
                                 CheckBox western, CheckBox thriller, CheckBox romance, CheckBox sf, CheckBox crime, CheckBox history, CheckBox war, CheckBox fantasy, CheckBox bio){

        title.setText(cursor.getString(cursor.getColumnIndex(DBConstants.TITLE_COLUMN)));
        date.setText(cursor.getString(cursor.getColumnIndex(DBConstants.RELEASE_DATE_COLUMN)));
        runtime.setText(cursor.getString(cursor.getColumnIndex(DBConstants.RUNTIME_COLUMN)));
        director.setText(cursor.getString(cursor.getColumnIndex(DBConstants.DIRECTOR_COLUMN)));
        writer.setText(cursor.getString(cursor.getColumnIndex(DBConstants.WRITER_COLUMN)));
        genre=cursor.getString(cursor.getColumnIndex(DBConstants.GENRE_COLUMN));
        Functions.checkedGenre(genre,action,animation,adventure,comedy,drama,horror,western,thriller,romance,sf,crime,history,war,fantasy,bio);
        actors.setText(cursor.getString(cursor.getColumnIndex(DBConstants.ACTORS_COLUMN)));
        story.setText(cursor.getString(cursor.getColumnIndex(DBConstants.STORY_COLUMN)));
        url.setText(cursor.getString(cursor.getColumnIndex(DBConstants.URL_COLUMN)));
        imageString = cursor.getString(cursor.getColumnIndex(DBConstants.POSTER_COLUMN));
        if (imageString!=null) {
            Bitmap myBitmapAgain = Functions.decodeBase64(imageString);
            image.setImageBitmap(myBitmapAgain);
        }
        else {
            image.setImageResource(R.drawable.movies_icon);
        }
    }

}
