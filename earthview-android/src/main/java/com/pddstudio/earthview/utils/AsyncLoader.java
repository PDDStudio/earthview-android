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
import com.pddstudio.earthview.EarthView;
import com.pddstudio.earthview.EarthViewCallback;
import com.pddstudio.earthview.EarthWallpaper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
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
    private final Collection<EarthWallpaper> earthWallpapers;
    private final OkHttpClient okHttpClient = new OkHttpClient();

    public AsyncLoader(EarthViewCallback earthViewCallback, String... earthViewIds) {
        this.wallIds = earthViewIds;
        this.earthViewCallback = earthViewCallback;
        this.earthWallpapers = Collections.synchronizedList(new LinkedList<EarthWallpaper>());
    }

    @Override
    public void onPreExecute() {
        //invoke the callback that we're starting now loading the requests
        earthViewCallback.onStartedLoading(wallIds.length);
    }

    @Override
    protected Void doInBackground(Void... params) {

        Gson gson = new Gson();

        earthWallpapers.clear();

        synchronized (wallIds) {
            for(String singleId : wallIds) {
                String requestUrl = ApiUtils.getApiUrl(singleId);
                try {
                    Request request = new Request.Builder().url(requestUrl).build();
                    Response response = okHttpClient.newCall(request).execute();
                    if(!response.isSuccessful()) continue;
                    EarthWallpaper wallpaper = gson.fromJson(response.body().charStream(), EarthWallpaper.class);
                    if(wallpaper != null) publishProgress(wallpaper);
                } catch (IOException io) {/* Ignore the exception which is thrown by OkHttp */}
                if(this.isCancelled()) {
                    okHttpClient.cancel(null);
                    break;
                }
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(EarthWallpaper... earthWallpapers) {
        //adding the item to the collection and invoke the callback that a new item has been added
        this.earthWallpapers.add(earthWallpapers[0]);
        earthViewCallback.onItemLoaded(earthWallpapers[0]);
    }

    @Override
    protected void onCancelled() {
        //invoke the callback when the task has been cancelled
        earthViewCallback.onCancelledLoading(earthWallpapers);
    }

    @Override
    public void onPostExecute(Void v) {
        //invoke the callback when loading finished
        earthViewCallback.onFinishedLoading(earthWallpapers);
    }

}
