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

package com.pddstudio.earthview.sync;

import android.util.Log;

import com.google.gson.Gson;
import com.pddstudio.earthview.EarthView;
import com.pddstudio.earthview.EarthWallpaper;
import com.pddstudio.earthview.utils.ApiUtils;
import com.pddstudio.earthview.utils.IdUtils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * This Class was created by Patrick J
 * on 16.12.15. For more Details and Licensing
 * have a look at the README.md
 */
public class SynchronizedTask {

    private final OkHttpClient okHttpClient = new OkHttpClient();
    private final Gson gson = new Gson();

    private final EarthWallpaper[] results;
    private final boolean multipleRequests;
    private final boolean randomRequests;
    private final int earthWallpaperCount;
    private final String[] earthWallIds;

    private SynchronizedTask(EarthView.SynchronizedBuilder synchronizedBuilder) {
        //for debugging only
        Log.d("SynchornizedTask", "Builder pattern values: ");
        Log.d("SynchornizedTask", "earthWallpaperCount: " + synchronizedBuilder.earthWallpaperCount);
        Log.d("SynchornizedTask", "multiple results: " + synchronizedBuilder.multipleResults);
        Log.d("SynchornizedTask", "random request: " + synchronizedBuilder.randomRequest);
        if(synchronizedBuilder.earthWallIds != null) {
            if(synchronizedBuilder.earthWallIds.length == 0) {
                Log.d("SynchornizedTask", "earthwallid size is 0");
            } else if(synchronizedBuilder.earthWallIds.length == 1) {
                Log.d("SynchornizedTask", "earthwallid: " + synchronizedBuilder.earthWallIds[0]);
            } else {
                Log.d("SynchornizedTask", "earthwallid(s) : ");
                for(int i = 0; i < synchronizedBuilder.earthWallIds.length; i++) {
                    Log.d("SynchronizedTask", "Wall ID: " + synchronizedBuilder.earthWallIds[i]);
                }
            }
        } else {
            Log.d("SynchzronizedTask", "No earth wall ids found.");
        }
        this.multipleRequests = synchronizedBuilder.multipleResults;
        this.earthWallpaperCount = synchronizedBuilder.earthWallpaperCount;
        this.results = new EarthWallpaper[synchronizedBuilder.earthWallpaperCount];
        this.randomRequests = synchronizedBuilder.randomRequest;
        if(synchronizedBuilder.earthWallIds != null) this.earthWallIds = synchronizedBuilder.earthWallIds;
        else this.earthWallIds = new String[0];

    }

    public static SynchronizedTask forBuilder(EarthView.SynchronizedBuilder synchronizedBuilder) {
        return new SynchronizedTask(synchronizedBuilder);
    }

    public final EarthWallpaper[] execute() {

        for(int i = 0; i < earthWallpaperCount; i++) {

            String requestUrl = ApiUtils.getApiUrl(randomRequests ? IdUtils.getRandomId() : earthWallIds[i]) ;
            try {
                Request request = new Request.Builder().url(requestUrl).build();
                Response response = okHttpClient.newCall(request).execute();
                if(!response.isSuccessful()) {
                    results[i] = null;
                    continue;
                }
                EarthWallpaper wallpaper = gson.fromJson(response.body().charStream(), EarthWallpaper.class);
                results[i] = wallpaper;
            } catch (IOException io) {/* Ignore the exception which is thrown by OkHttp */}

        }

        return results;
    }

}
