package com.project1v2mymoviescollection.Functions;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.project1v2mymoviescollection.Activities.InternetSearchActivity;
import com.project1v2mymoviescollection.R;
import com.squareup.picasso.Picasso;

import java.io.InputStream;

/**
 * Created by SandraMac on 17/02/2017.
 *
 * This class creates a custom adapter for the InternetSearchActivity results
 */

public class InternetMovieAdapter extends ArrayAdapter<MyMovie> {

    private Bitmap poster;
    private TextView title;
    private TextView year;
    private ImageView image;


    public InternetMovieAdapter(Context context, int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.internet_my_list_item,null);

        title = (TextView)convertView.findViewById(R.id.title);
        year = (TextView) convertView.findViewById(R.id.year);
        image = (ImageView)convertView.findViewById(R.id.imageView2);

        MyMovie movie = getItem(position);
        title.setText(movie.getTitle());
        year.setText(movie.getYear());

        setImage(movie);

        return convertView;
    }


    public void setImage(MyMovie movie){
        if (movie.getPoster().equals("N/A")) {
            image.setImageResource(R.drawable.movies_icon);
        }
        else
            Picasso.with(getContext()).load(movie.getPoster()).fit().centerInside().into(image);
            //new DownloadImage(image).execute(movie.getPoster());
    }


    /**
     * Class to download image from URL with Async Task
     */

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        ProgressDialog mProgressDialog;

        public DownloadImage(ImageView iv) {
            this.imageView = iv;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        public Bitmap doInBackground(String... URL) {
            String imageURL = URL[0];
            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        public void onPostExecute(Bitmap result) {

            if (result!=null) {
                // Set the bitmap into ImageView
                poster = result;
                imageView.setImageBitmap(poster);
            }
        }
    }
}
