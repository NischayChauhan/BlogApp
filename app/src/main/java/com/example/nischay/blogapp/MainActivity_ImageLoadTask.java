package com.example.nischay.blogapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Nischay on 4/27/2020.
 */

public class MainActivity_ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

    private String url;
    private ImageView im_view;
    private TextView im_view_text;
    private ProgressBar im_view_loader;

    public MainActivity_ImageLoadTask(String url, ImageView im_view, TextView im_view_text ,ProgressBar im_view_loader) {
        this.im_view = im_view;
        this.im_view_text = im_view_text;
        this.im_view_loader = im_view_loader;
        this.url = url;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            final LruCache<String, Bitmap> lru = Cache_Manager.getInstance().getLru();
            Bitmap bitmapFromMemCache = lru.get(url);
            if(bitmapFromMemCache == null){
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                lru.put(url,myBitmap);
                return myBitmap;
            }else{
                return bitmapFromMemCache;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        im_view.setVisibility(View.VISIBLE);
        im_view.setImageBitmap(result);
        Log.e("From Loader","Done loading image");
        im_view_loader.setVisibility(View.GONE);
    }
}
