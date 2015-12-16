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
import android.util.Log;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;

/**
 * This Class was created by Patrick J
 * on 16.12.15. For more Details and Licensing
 * have a look at the README.md
 */
public class EarthViewerSource extends RemoteMuzeiArtSource {

    private static final String SOURCE = "EarthViewerSource";
    private static final int ROTATE_TIME_MILLI_SECONDS = 3 * 60 * 60 * 1000;

    public EarthViewerSource() {
        super(SOURCE);
        //setUserCommands(BUILTIN_COMMAND_ID_NEXT_ARTWORK);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onTryUpdate(int reason) throws RetryException {
        Log.w("EarthViewerSrc", "onUpdate() called for reason : " + reason);

        publishArtwork(new Artwork.Builder()
                .imageUri(Uri.parse("https://www.gstatic.com/prettyearth/assets/full/6112.jpg"))
                .title("Muzei Support for EarthViewer")
                .byline("Coming Soon...")
                .viewIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://earthview.withgoogle.com/6112")))
                .build());

        scheduleUpdate(System.currentTimeMillis() + ROTATE_TIME_MILLI_SECONDS);

    }

}
