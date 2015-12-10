package com.pddstudio.earthview.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.pddstudio.earthview.EarthViewCallback;
import com.pddstudio.earthview.EarthWallpaper;
import com.pddstudio.earthview.utils.ApiUtils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * This Class was created by Patrick J
 * on 10.12.15. For more Details and Licensing
 * have a look at the README.md
 */
public class AsyncLoader extends AsyncTask<Void, EarthWallpaper, Void> {

    private final String[] wallIds;
    private final EarthViewCallback earthViewCallback;
    private final List<EarthWallpaper> earthWallpapers;

    public AsyncLoader(EarthViewCallback earthViewCallback, String... earthViewIds) {
        this.wallIds = earthViewIds;
        this.earthViewCallback = earthViewCallback;
        this.earthWallpapers = new LinkedList<>();
    }

    @Override
    public void onPreExecute() {
        earthViewCallback.onStartedLoading();
    }

    @Override
    protected Void doInBackground(Void... params) {

        Gson gson = new Gson();
        OkHttpClient okHttpClient = new OkHttpClient();

        earthWallpapers.clear();

        for(String singleId : wallIds) {
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
        this.earthWallpapers.add(earthWallpapers[0]);
        earthViewCallback.onItemLoaded(earthWallpapers[0]);
    }

    @Override
    public void onPostExecute(Void v) {
        earthViewCallback.onFinishedLoading(earthWallpapers);
    }

}
