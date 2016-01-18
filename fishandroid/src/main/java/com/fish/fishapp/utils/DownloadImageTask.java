package com.fish.fishapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.fish.fishapp.App;

import java.io.InputStream;


//Uso
//new DownloadImageTask((ImageView) findViewById(R.id.imageView1))
//.execute("http://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png");

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    Bitmap bitMap;
    private String url;

    public DownloadImageTask(ImageView bmImage, Bitmap bitmap) {
        this.bmImage = bmImage;
        this.bitMap = bitmap;
    }

    protected Bitmap doInBackground(String... urls) {
    	App.getInstance().log("Loading image: " + url);
    	url = urls[0];
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
        	App.getInstance().log("Error:" + e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
    	App.getInstance().log("Finished image:" + url);
        bmImage.setImageBitmap(result);
        bitMap = result;
    }
}
