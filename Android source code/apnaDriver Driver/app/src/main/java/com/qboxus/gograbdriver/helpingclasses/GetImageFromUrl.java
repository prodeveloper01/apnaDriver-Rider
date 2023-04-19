package com.qboxus.gograbdriver.helpingclasses;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.qboxus.gograbdriver.appinterfaces.DrawableCallback;
import com.qboxus.gograbdriver.appinterfaces.FragmentCallback;

import java.io.IOException;
import java.io.InputStream;

public class GetImageFromUrl extends AsyncTask<String, Void, Bitmap> {

    DrawableCallback callback;
    public GetImageFromUrl(DrawableCallback callback){
        this.callback=callback;
    }
    @Override
    protected Bitmap doInBackground(String... url) {
        String stringUrl = url[0];
        Bitmap bitmap = null;
        InputStream inputStream;
        try {
            inputStream = new java.net.URL(stringUrl).openStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    @Override
    protected void onPostExecute(Bitmap bitmap){
        super.onPostExecute(bitmap);
        callback.Responce(bitmap);

    }
}