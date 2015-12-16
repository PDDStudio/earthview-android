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

package com.pddstudio.earthviewer.muzei;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;
import com.google.android.apps.muzei.api.UserCommand;
import com.pddstudio.earthview.EarthView;
import com.pddstudio.earthview.EarthWallpaper;

/**
 * This Class was created by Patrick J
 * on 16.12.15. For more Details and Licensing
 * have a look at the README.md
 */
public class EarthViewerSource extends RemoteMuzeiArtSource {

    private static final String SOURCE = "EarthViewerSource";

    private MuzeiPreferences muzeiPreferences;

    public EarthViewerSource() {
        super(SOURCE);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.muzeiPreferences = new MuzeiPreferences(EarthViewerSource.this);
        UserCommand command = new UserCommand(BUILTIN_COMMAND_ID_NEXT_ARTWORK);
        setUserCommands(command);

    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String commandString = intent.getExtras().getString("service");
                if(commandString != null) {
                    try {
                        onTryUpdate(UPDATE_REASON_USER_NEXT);
                    } catch (RetryException retry) { Log.w("EarthViewer", retry.getMessage()); }
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onTryUpdate(int reason) throws RetryException {
        //receive the current ID to be sure we don't show the same image twice (even if it's almost 0 chance :P)
        String currentId = (getCurrentArtwork() != null) ? getCurrentArtwork().getToken() : null;

        EarthWallpaper earthWallpaper = EarthView.withGoogle().getSynchronizedBuilder().getRandomWallpaper().executeWithResult();
        if(earthWallpaper == null) throw new RetryException();
        if(currentId != null && TextUtils.equals(currentId, earthWallpaper.getWallpaperId())) throw new RetryException();

        Artwork artwork = new Artwork.Builder()
                .imageUri(Uri.parse(earthWallpaper.getWallPhotoUrl()))
                .title(earthWallpaper.getFormattedWallpaperTitle())
                .byline(earthWallpaper.getShareUrl())
                .token(earthWallpaper.getWallpaperId())
                .viewIntent(new Intent(Intent.ACTION_VIEW, Uri.parse(earthWallpaper.getShareUrl())))
                .build();

        publishArtwork(artwork);
        scheduleUpdate(System.currentTimeMillis() + muzeiPreferences.getRotateTimeMilis());
        //TODO: update via activity
    }

}
