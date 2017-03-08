package com.project1v2mymoviescollection.Functions;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.project1v2mymoviescollection.Activities.PosterFullScreenActivity;
import com.project1v2mymoviescollection.Constants.And.SQL.DBConstants;
import com.project1v2mymoviescollection.Constants.And.SQL.MyMoviesSQLHelper;
import com.project1v2mymoviescollection.R;

/**
 * Created by SandraMac on 24/02/2017.
 *
 * This class creates a custom adapter for the movies list
 */

public class MyMovieAdapter extends CursorAdapter {

    private TextView title;
    private TextView genre;
    private TextView year;
    private ImageView poster;
    private String watched;
    private MyMoviesSQLHelper myMoviesSQLHelper;


    public MyMovieAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_movies_list,null);
        return view;
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        myMoviesSQLHelper = new MyMoviesSQLHelper(context);

        title = (TextView)view.findViewById(R.id.titleTV);
        year = (TextView) view.findViewById(R.id.yearTV);
        genre = (TextView) view.findViewById(R.id.genreTV);
        poster = (ImageView)view.findViewById(R.id.posterIV);
        final ImageButton watchedView;
        watchedView = (ImageButton) view.findViewById(R.id.imageButton2);

        watchedView.setTag(R.id.imageButton2,cursor.getInt(cursor.getColumnIndex(DBConstants.ID_COLUMN)));
        poster.setTag(R.id.posterIV,cursor.getString(cursor.getColumnIndex(DBConstants.POSTER_COLUMN)));

        String y = cursor.getString(cursor.getColumnIndex(DBConstants.RELEASE_DATE_COLUMN));
        String[] Year = y.split(" ");
        String image = cursor.getString(cursor.getColumnIndex(DBConstants.POSTER_COLUMN));

        if (cursor.getString(cursor.getColumnIndex(DBConstants.TITLE_COLUMN)).contains("-")){
            String[] name = cursor.getString(cursor.getColumnIndex(DBConstants.TITLE_COLUMN)).split("-");
            title.setText(name[0]);
        }
        else
            title.setText(cursor.getString(cursor.getColumnIndex(DBConstants.TITLE_COLUMN)));

        genre.setText(cursor.getString(cursor.getColumnIndex(DBConstants.GENRE_COLUMN)));
        year.setText(Year[Year.length-1]);

        if (image!=null){
            Bitmap bitmap = new Functions().decodeBase64(image);
            poster.setImageBitmap(bitmap);
        }


        watched=cursor.getString(cursor.getColumnIndex(DBConstants.WATCHED_COLUMN));
        if (watched.equals("0"))
            watchedView.setImageResource(R.drawable.not_watched_black);
        else
            watchedView.setImageResource(R.drawable.watched_black);

        watchedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                int id;
                if (watched.equals("0")){
                    watchedView.setImageResource(R.drawable.watched_black);
                    watched="1";
                    id= (int) v.getTag(R.id.imageButton2);
                    contentValues.put(DBConstants.WATCHED_COLUMN,"1");
                }
                else {
                    watchedView.setImageResource(R.drawable.not_watched_black);
                    watched="0";
                    id= (int) v.getTag(R.id.imageButton2);
                    contentValues.put(DBConstants.WATCHED_COLUMN,"0");
                    myMoviesSQLHelper.getWritableDatabase().update(DBConstants.TABLE_NAME, contentValues, "_id=?", new String[]{"" + id});
                }
                myMoviesSQLHelper.getWritableDatabase().update(DBConstants.TABLE_NAME, contentValues, "_id=?", new String[]{"" + id});

            }
        });

        poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PosterFullScreenActivity.class);
                String imageString = (String) v.getTag(R.id.posterIV);

                intent.putExtra(DBConstants.POSTER_COLUMN,imageString);
                context.startActivity(intent);

            }
        });
    }

}
