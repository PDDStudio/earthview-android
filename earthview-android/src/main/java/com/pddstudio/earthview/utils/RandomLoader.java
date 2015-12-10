package com.pddstudio.earthview.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.pddstudio.earthview.EarthViewCallback;
import com.pddstudio.earthview.EarthWallpaper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

/**
 * This Class was created by Patrick J
 * on 10.12.15. For more Details and Licensing
 * have a look at the README.md
 */
public class RandomLoader extends AsyncTask<Void, EarthWallpaper, Void> {

    private final int wallSize;
    private final EarthViewCallback earthViewCallback;
    private boolean duplicate = true;
    private final Collection<EarthWallpaper> earthWallpaperCollection = new LinkedList<>();

    public RandomLoader(int ammount, EarthViewCallback earthViewCallback) {
        this.wallSize = ammount;
        this.earthViewCallback = earthViewCallback;
    }

    public RandomLoader uniqueOnly() {
        this.duplicate = false;
        return this;
    }

    @Override
    public void onPreExecute() {
        earthViewCallback.onStartedLoading();
    }

    @Override
    protected Void doInBackground(Void... params) {

        String[] ids;
        if(duplicate) ids = IdUtils.getRandomIds(wallSize);
        else ids = IdUtils.getUniqueRandomIds(wallSize);

        Gson gson = new Gson();
        OkHttpClient okHttpClient = new OkHttpClient();

        earthWallpaperCollection.clear();

        for(String singleId : ids) {
            String requestUrl = ApiUtils.getApiUrl(singleId);
            try {
                Request request = new Request.Builder().url(requestUrl).build();
                Response response = okHttpClient.newCall(request).execute();
                if(!response.isSuccessful()) continue;
                EarthWallpaper wallpaper = gson.fromJson(response.body().charStream(), EarthWallpaper.class);
                if(wallpaper != null) publishProgress(wallpaper);
            } catch (IOException io) {
                Log.d("AsyncLoader", "IOError: " + io.getLocalizedMessage());
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(EarthWallpaper... earthWallpapers) {
        this.earthWallpaperCollection.add(earthWallpapers[0]);
        earthViewCallback.onItemLoaded(earthWallpapers[0]);
    }

    @Override
    public void onPostExecute(Void v) {
        earthViewCallback.onFinishedLoading(earthWallpaperCollection);
    }


}
