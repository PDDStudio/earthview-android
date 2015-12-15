/*
 * Copyright 2015 - Patrick J - earthview-android
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pddstudio.earthview.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.pddstudio.earthview.EarthWallpaper;
import com.pddstudio.earthview.SingleEarthViewCallback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * This Class was created by Patrick J
 * on 10.12.15. For more Details and Licensing
 * have a look at the README.md
 */
public class SingleLoader extends AsyncTask<Void, Void, EarthWallpaper> {

    private final SingleEarthViewCallback singleEarthViewCallback;
    private String useWallId = null;

    public SingleLoader(SingleEarthViewCallback singleEarthViewCallback) {
        this.singleEarthViewCallback = singleEarthViewCallback;
    }

    public SingleLoader loadEarthViewById(String id) {
        this.useWallId = id;
        return this;
    }

    public SingleLoader loadRandomEarthWallpaper() {
        this.useWallId = null;
        return this;
    }

    @Override
    public void onPreExecute() {
        singleEarthViewCallback.onStartedLoading();
    }

    @Override
    protected EarthWallpaper doInBackground(Void... params) {
        Gson gson = new Gson();
        OkHttpClient okHttpClient = new OkHttpClient();
        String id;
        String requestUrl;
        if(useWallId == null) {
            id = IdUtils.getRandomId();
        } else {
            id = useWallId;
        }

        requestUrl = ApiUtils.getApiUrl(id);

        try {
            Request request = new Request.Builder().url(requestUrl).build();
            Response response = okHttpClient.newCall(request).execute();
            if(!response.isSuccessful()) return null;
            EarthWallpaper wallpaper = gson.fromJson(response.body().charStream(), EarthWallpaper.class);
            if(wallpaper != null && wallpaper.getWallpaperId().equals(id)) return wallpaper;
        } catch (IOException io) {
            Log.d("AsyncLoader", "IOError: " + io.getLocalizedMessage());
        }

        return null;
    }

    @Override
    public void onPostExecute(EarthWallpaper earthWallpaper) {
        singleEarthViewCallback.onFinishedLoading(earthWallpaper);
    }

}
