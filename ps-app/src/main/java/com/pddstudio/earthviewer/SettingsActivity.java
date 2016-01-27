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

package com.pddstudio.earthviewer;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import com.pddstudio.earthviewer.utils.Preferences;

import java.io.File;

public class SettingsActivity extends AppCompatActivity implements FolderChooserDialog.FolderCallback{

    private SettingsItem settingsItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        Toolbar toolbar = (Toolbar) findViewById(R.id.pref_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getFragmentManager().findFragmentById(R.id.pref_content) == null)
            getFragmentManager().beginTransaction().replace(R.id.pref_content, new SettingsFragment()).commit();

        //tint the navigation bar if set
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(Preferences.getInstance().getTintNavigationBar()) {
                getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
            } else {
                getWindow().setNavigationBarColor(Color.BLACK);
            }
        }

        Preferences.getInstance().setTempPrefActivity(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Preferences.exists()) {
            Preferences.getInstance().registerPreferenceListener();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(Preferences.exists()) {
            Preferences.getInstance().unregisterPreferenceListener();
        }
    }

    @Override
    public void onFolderSelection(File file) {
        if(file.exists() && file.isDirectory() && file.canWrite()) {
            Log.d("Settings", "Download path changed to " + file.getAbsolutePath());
            Preferences.getInstance().setWallpaperDownloadDirectory(file.getAbsolutePath());
            if(settingsItem != null) settingsItem.onStorageChanged(file.getAbsolutePath());
        }
    }

    public void showDirectoryChooserDialog(SettingsItem settingsItemCallback) {
        this.settingsItem = settingsItemCallback;
        new FolderChooserDialog.Builder(this)
                .chooseButton(R.string.preferences_item_wall_folder_select)
                .cancelButton(R.string.preferences_item_wall_folder_cancel)
                .show();
    }

    //using the PreferenceFragment for the Settings
    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            Preference buildPref = findPreference("build_info_pref");
            buildPref.setSummary(Preferences.getInstance().getSettingsBuildNumber());
        }

    }

    public interface SettingsItem {
        void onStorageChanged(String newPath);
    }

}