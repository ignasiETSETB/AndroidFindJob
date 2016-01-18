package com.fish.fishapp;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.fish.fishapp.utils.AndroidVersion;

import java.io.InputStream;

public class ImageCache {
	//memoria
	private LruCache<String, Bitmap> mMemoryCache;
	
	public ImageCache(){
		//constructor
		// Get max available VM memory, exceeding this amount will throw an
	    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
	    // int in its constructor.
	    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

	    // Use 1/8th of the available memory for this memory cache.
	    final int cacheSize = maxMemory / 8;

	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @SuppressLint("NewApi")
			@Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            // The cache size will be measured in kilobytes rather than
	            // number of items.
	            if(AndroidVersion.isHoneycombMr2OrHigher()) {
	            	return bitmap.getByteCount() / 1024;
	            } else {
	                return bitmap.getRowBytes() * bitmap.getHeight() /1024;
	            }
	        }
	    };
	}
	
	private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (key!=null && bitmap !=null){
		    if (getBitmapFromMemCache(key) == null) {
		        mMemoryCache.put(key, bitmap);
		    }
		}
	}

	private Bitmap getBitmapFromMemCache(String key) {
		if (key == null){
			return null;
		} 
	    return mMemoryCache.get(key);
	}
	
	public void loadBitmap(String imageURL, ImageView imageView, Bitmap image) {
	    final String imageKey = imageURL;
	    if (imageURL==null) return;
	    final Bitmap bitmap = getBitmapFromMemCache(imageKey);
	    if (bitmap != null) {
	    	imageView.setImageBitmap(bitmap);
	    	image = bitmap;
	    } else {
	    	imageView.setImageResource(R.drawable.image_placeholder);
	    	DownloadImageTask task = new DownloadImageTask(imageView,image);
	    	task.execute(imageURL);
	    }
	}
	
	
	public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    ImageView bmImage;
	    Bitmap image;
	    private String url;

	    public DownloadImageTask(ImageView bmImage, Bitmap pimage) {
	        this.bmImage = bmImage;
	        this.image = pimage;
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
	    	addBitmapToMemoryCache(url, result);
	    	image=result;
	        bmImage.setImageBitmap(result);
	    }
	}
}
