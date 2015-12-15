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

import com.pddstudio.earthviewer.SettingsActivity;
import com.pddstudio.earthviewer.utils.Preferences;

/**
 * This Class was created by Patrick J
 * on 08.12.15. For more Details and Licensing
 * have a look at the README.md
 */
public class SaveDirectoryPreference extends Preference implements SettingsActivity.SettingsItem {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SaveDirectoryPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        checkSaveLocation();
    }

    public SaveDirectoryPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        checkSaveLocation();
    }

    public SaveDirectoryPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        checkSaveLocation();
    }

    public SaveDirectoryPreference(Context context) {
        super(context);
        checkSaveLocation();
    }

    private void checkSaveLocation() {
        String location;
        if(Preferences.exists() && Preferences.getInstance().canWriteExternalStorage()) {
            location = Preferences.getInstance().getWallpaperDownloadDirectory();
        } else {
            location = "???";
        }
        setSummary(getSummary().toString().replace("%wall%", location));
    }

    private void showDirectoryChooser() {
        ((SettingsActivity) getContext()).showDirectoryChooserDialog(this);
    }

    @Override
    public void onClick() {
        showDirectoryChooser();
    }

    @Override
    public void onStorageChanged(String newPath) {
        setSummary(newPath);
    }
}
