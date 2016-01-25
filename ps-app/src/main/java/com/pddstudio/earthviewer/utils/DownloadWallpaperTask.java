/*
 * Copyright 2015 - Patrick J - ps-app
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

package com.pddstudio.earthviewer.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.pddstudio.earthview.EarthWallpaper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This Class was created by Patrick J
 * on 11.12.15. For more Details and Licensing
 * have a look at the README.md
 */
public class DownloadWallpaperTask extends AsyncTask<Boolean, Void, File> {

    public interface DownloadCallback {
        void onDownloadStarted();
        void onDownloadFinished(boolean success, File file);
    }

    private final File saveDirLocation;
    private final DownloadCallback downloadCallback;
    private final EarthWallpaper earthWallpaper;

    public DownloadWallpaperTask(DownloadCallback downloadCallback, File saveDirLocation, EarthWallpaper earthWallpaper) {
        this.downloadCallback = downloadCallback;
        this.saveDirLocation = saveDirLocation;
        this.earthWallpaper = earthWallpaper;
    }

    @Override
    public void onPreExecute() {
        downloadCallback.onDownloadStarted();
    }

    @Override
    protected File doInBackground(Boolean... params) {
        final File savedImage = new File(saveDirLocation, earthWallpaper.getFormattedFileName(true, true));
        try {

            OkHttpClient okHttpClient = new OkHttpClient();
            final Request request = new Request.Builder().url(params[0] ? earthWallpaper.getWallPhotoUrl() : earthWallpaper.getWallDownloadUrl()).build();
            Response response = okHttpClient.newCall(request).execute();
            if(!response.isSuccessful()) {
                Log.d("DownloadError", "Unable to download image! " + response);
                return null;
            }
            InputStream inputStream = response.body().byteStream();
            FileOutputStream fileOutputStream = new FileOutputStream(savedImage);
            IOUtils.copy(inputStream, fileOutputStream);
            inputStream.close();
            fileOutputStream.close();

            Log.d("ImageDownload", "Finished caching image!");
            return savedImage;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onPostExecute(File downloadedFile) {
        if(downloadedFile != null) downloadCallback.onDownloadFinished(downloadedFile.exists(), downloadedFile);
        else downloadCallback.onDownloadFinished(false, null);
    }

}
