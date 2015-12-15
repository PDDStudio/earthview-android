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

package com.pddstudio.earthviewer.utils.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;

import com.pddstudio.earthviewer.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * This Class was created by Patrick J
 * on 08.12.15. For more Details and Licensing
 * have a look at the README.md
 */
public class ClearCachePreference extends Preference {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ClearCachePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        checkSize();
    }

    public ClearCachePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        checkSize();
    }

    public ClearCachePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        checkSize();
    }

    public ClearCachePreference(Context context) {
        super(context);
        checkSize();
    }

    private void checkSize() {
        long sizeMb = getCacheDirectorySize();
        setSummary(getSummary().toString().replace("%s", sizeMb + " Mb"));
    }

    private void clearCache() {
        File cacheDir = getContext().getCacheDir();
        try {
            FileUtils.cleanDirectory(cacheDir);
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            String sizeString = getContext().getResources().getString(R.string.preferences_item_clear_cache_summary);
            sizeString = sizeString.replace("%s", getCacheDirectorySize() + " Mb");
            setSummary(sizeString);
        }
    }

    private long getCacheDirectorySize() {
        File cacheDir = getContext().getCacheDir();
        long size = FileUtils.sizeOfDirectory(cacheDir);
        long sizeKb = size / 1024;
        return sizeKb / 1024;
    }

    @Override
    public void onClick() {
        Log.d("ClearCachePreference", "clearing cache!");
        clearCache();
    }

}
