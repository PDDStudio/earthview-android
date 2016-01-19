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

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.pddstudio.earthview.EarthWallpaper;
import com.pddstudio.earthviewer.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import io.paperdb.Paper;

/**
 * This Class was created by Patrick J
 * on 07.12.15. For more Details and Licensing
 * have a look at the README.md
 */
public class Preferences implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String PREFERENCES = "com.pddstudio.earthview.demo.prefs";
    private static final String DEFAULT_WALLPAPER_DIR = "EarthViews";

    public static final int EXTERNAL_STORAGE_PERMISSION = 12;
    public static final int INTERNET_PERMISSION = 13;
    public static final int ACCESS_NETWORK_PERMISSION = 14;

    private static Preferences preferences = null;
    //size in MB for the max cache used before we're going to clean it ;)
    private static final int CACHE_SIZE_LIMIT = 50;
    private static final int FILE_DIR_SIZE_LIMIT = 50;

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("PreferenceListener", "key changed -> " + key);
        // Log.d("PreferenceListener", "new value -> " + sharedPreferences.getString(key, null));
        if(key.equals(RECYCLER_VIEW_GRID_COUNT)) {

        } else if(key.equals(WALLPAPER_DOWNLOAD_DIRECTORY)) {

        } else if(key.equals(SAVE_MOBILE_DATA)) {

        } else if(key.equals(WALLPAPER_SOURCE_TYPE)) {

        } else if(key.equals(CACHING_LIFECYCLE)) {

        } else if(key.equals(CACHING_MODE)) {

        } else if(key.equals(TINT_NAVIGATION_BAR)) {
            if(tmpSettings != null && tmpSettings.getWindow() != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if(getTintNavigationBar()) {
                    tmpSettings.getWindow().setNavigationBarColor(context.getResources().getColor(R.color.colorPrimaryDark));
                } else {
                    tmpSettings.getWindow().setNavigationBarColor(Color.BLACK);
                }
            }
        }
    }

    public interface PermissionCallback {
        void onPermissionExplanationRequired(boolean showExplanation, String requiredPermission);
    }

    //string identifiers for the preference values
    private static final String RECYCLER_VIEW_GRID_COUNT = "rc_grid_count";
    private static final String WALLPAPER_DOWNLOAD_DIRECTORY = "wall_output_dir";
    private static final String SAVE_MOBILE_DATA = "save_data_mode";
    private static final String SHOW_LOADING_BAR = "show_loading_bar";
    private static final String WALLPAPER_SOURCE_TYPE = "wall_source";
    private static final String CACHING_LIFECYCLE = "caching_lifecycle";
    private static final String CACHING_MODE = "caching_mode";
    private static final String FAVORITES = "wall_favs";
    private static final String FAVORITES_IDS = "wall_favs_ids";
    private static final String AUTO_LOAD_WIFI = "auto_load_wifi";
    private static final String SHUFFLE_WALLPAPERS = "load_walls_random";
    private static final String APPLICATION_TYPEFACE = "app_typeface_id";
    private static final String TINT_NAVIGATION_BAR = "tint_nav_bar";

    private final AppCompatActivity activity;
    private final Context context;
    private final SharedPreferences sharedPreferences;
    private final PermissionCallback permissionCallback;
    private ConnectivityManager connectivityManager;
    private SharedPreferences.Editor prefEditor;

    private boolean canAccessStorage = false;
    private boolean canAccessInternet = false;
    private boolean canAccessNetwork = false;

    private Activity tmpSettings;

    private Preferences(Context context) {
        //this.sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
        this.activity = (AppCompatActivity) context;
        this.permissionCallback = (PermissionCallback) context;
        //check the permissions on startup
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("Preferences", "Device is running on SDK +23 ! Checking permissions...");
            int storagePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(storagePermission == PackageManager.PERMISSION_GRANTED) canAccessStorage = true;
            int internetPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET);
            if(internetPermission == PackageManager.PERMISSION_GRANTED) canAccessInternet = true;
            int networkPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE);
            if(networkPermission == PackageManager.PERMISSION_GRANTED) canAccessNetwork = true;
        } else {
            canAccessStorage = true;
            canAccessInternet = true;
            canAccessNetwork = true;
        }
        //register Paper data storage
        Paper.init(context);
        //get the connectivity manager
        if(canAccessNetwork) connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    public static void init(Context context) {
        if(preferences != null) Log.w("Preferences", "Preferences instance still exist! Overriding it...");
        preferences = new Preferences(context);
    }

    public static boolean exists() {
        return preferences != null;
    }

    public static Preferences getInstance() {
        return preferences;
    }

    public void registerPreferenceListener() {
        Log.d("PreferenceListener", "registering SharedPreferenceListener!");
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        garbageCollection();
    }

    public void unregisterPreferenceListener() {
        Log.d("PreferenceListener", "unregistering SharedPreferenceListener!");
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        garbageCollection();
    }

    public void clearCache() {
        try {
            FileUtils.cleanDirectory(context.getCacheDir());
            Log.d("GarbageCollector", "successfully cleaned the application's cache!");
        } catch (IOException e) {
            Log.d("GarbageError", "unable to clean cache: " + e.getLocalizedMessage());
        }
    }

    /*
    FUNCTIONS FOR STORAGE MANAGEMENT, USED TO AVOID TOO MUCH CACHING
     */

    private void garbageCollection() {
        if(getCacheDirectorySize() >= CACHE_SIZE_LIMIT) {
            try {
                FileUtils.cleanDirectory(context.getCacheDir());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("GarbageCollection", "automatically cleaned cache. Size was more than" + CACHE_SIZE_LIMIT + " Mb.");
        } else if(getFileDirectorySize() >= FILE_DIR_SIZE_LIMIT) {
            try {
                FileUtils.cleanDirectory(context.getFilesDir());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("GarbageCollection", "automatically cleaned internal files directory. Size was more than" + FILE_DIR_SIZE_LIMIT + " Mb.");
        }
    }

    private long getCacheDirectorySize() {
        File cacheDir = context.getFilesDir();
        long size = FileUtils.sizeOfDirectory(cacheDir);
        long sizeKb = size / 1024;
        return sizeKb / 1024;
    }

    private long getFileDirectorySize() {
        File filesDir = context.getFilesDir();
        long size = FileUtils.sizeOfDirectory(filesDir);
        long sizeKb = size / 1024;
        return sizeKb / 1024;
    }

    public String getSettingsBuildNumber() {
        String buildVersionName;
        int buildVersion;
        try {
            buildVersionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            buildVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // this should never happen
            buildVersionName = "Unknown";
            buildVersion = -1;
        }
        return buildVersionName + " [Build Version: " + buildVersion + "]";
    }

    /*
    ANDROID M PERMISSION VALIDATION
     */

    public boolean canWriteExternalStorage() {
        int permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permission == PackageManager.PERMISSION_GRANTED) canAccessStorage = true;
        else canAccessStorage = false;
        return canAccessStorage;
    }

    public void requestExternalStoragePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionCallback.onPermissionExplanationRequired(true, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, EXTERNAL_STORAGE_PERMISSION);
        }
    }

    public void requestExternalStoragePermission(AppCompatActivity appCompatActivity, PermissionCallback callback, boolean requestFromDialog) {
        if(ActivityCompat.shouldShowRequestPermissionRationale(appCompatActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) && !requestFromDialog) {
            callback.onPermissionExplanationRequired(true, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            ActivityCompat.requestPermissions(appCompatActivity, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, EXTERNAL_STORAGE_PERMISSION);
        }
    }

    public boolean canAccessInternet() {
        int permission = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET);
        if(permission == PackageManager.PERMISSION_GRANTED) canAccessInternet = true;
        else canAccessInternet = false;
        return canAccessInternet;
    }

    public void requestInternetPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.INTERNET)) {
            permissionCallback.onPermissionExplanationRequired(true, Manifest.permission.INTERNET);
        } else {
            ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.INTERNET }, INTERNET_PERMISSION);
        }
    }

    public boolean canAccessNetworkInfo() {
        int permission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE);
        if(permission == PackageManager.PERMISSION_GRANTED) canAccessNetwork = true;
        else canAccessNetwork = false;
        return canAccessNetwork;
    }

    public void requestNetworkAccessPermission() {
        ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.ACCESS_NETWORK_STATE }, ACCESS_NETWORK_PERMISSION);
    }

    public void onReceivedPermissionCallback(int permission, boolean granted) {
        if(permission == INTERNET_PERMISSION) canAccessInternet = granted;
        else if(permission == EXTERNAL_STORAGE_PERMISSION) canAccessStorage = granted;
        else if(permission == ACCESS_NETWORK_PERMISSION) canAccessNetwork = granted;
    }

    public String getWallpaperDownloadDirectory() {
        String defaultWall;
        if(canAccessStorage) {
            defaultWall = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + DEFAULT_WALLPAPER_DIR;
        } else {
            defaultWall = "";
        }
        return sharedPreferences.getString(WALLPAPER_DOWNLOAD_DIRECTORY, defaultWall);
    }

    public File getWallpaperDownloadDir() {
        String storage = getWallpaperDownloadDirectory();
        File downloadDir = new File(storage);
        if(storage.equals("") || storage.isEmpty()) {
            return null;
        } else if(downloadDir.exists() && downloadDir.isDirectory()) {
            return downloadDir;
        } else {
            return null;
        }
    }

    public void setWallpaperDownloadDirectory(String wallpaperDownloadDirectory) {
        prefEditor = sharedPreferences.edit();
        prefEditor.putString(WALLPAPER_DOWNLOAD_DIRECTORY, wallpaperDownloadDirectory);
        prefEditor.apply();
    }

    public boolean isOnWiFi() {
        if(connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected()) {
                if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI ||
                        networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET)
                    return true;
            }
        }
        return false;
    }

    public int getGridColumnCount() {
        //TODO: rewrite preference to avoid this nasty converting hack
        CharSequence charSequence = sharedPreferences.getString(RECYCLER_VIEW_GRID_COUNT, "2");
        String count = Character.toString(charSequence.charAt(0));
        return Integer.parseInt(count);
    }

    public boolean getLoadingBarEnabled() {
        return sharedPreferences.getBoolean(SHOW_LOADING_BAR, true);
    }

    public boolean getAutoLoadOnWifi() {
        return sharedPreferences.getBoolean(AUTO_LOAD_WIFI, true);
    }

    public boolean getShuffleWallpapers() {
        boolean shuffle = sharedPreferences.getBoolean(SHUFFLE_WALLPAPERS, false);
        Log.d("Preferences", "getShuffleWallpapers() : " + shuffle);
        return shuffle;
    }

    public void addFavorite(EarthWallpaper earthWallpaper) {
        Favorites.getFavorites(context).addFavorite(earthWallpaper);
    }

    public boolean isFavorite(EarthWallpaper earthWallpaper) {
        return Favorites.getFavorites(context).isFavorite(earthWallpaper);
    }

    public void removeFavorite(EarthWallpaper earthWallpaper) {
        Favorites.getFavorites(context).removeFavorite(earthWallpaper);
    }

    public Typeface getTypeface() {
        String typeface = sharedPreferences.getString(APPLICATION_TYPEFACE, "1");
        Typeface typefaceModel;
        switch (typeface) {
            default:
            case "1":
                typefaceModel = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
                break;
            case "2":
                typefaceModel = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoMono-Regular.ttf");
                break;
            case "3":
                typefaceModel = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoSlab-Regular.ttf");
                break;
            case "4":
                typefaceModel = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Regular.ttf");
                break;
        }
        return typefaceModel;
    }

    public boolean getTintNavigationBar() {
        return sharedPreferences.getBoolean(TINT_NAVIGATION_BAR, true);
    }

    public void setTempPrefActivity(Activity activity) {
        this.tmpSettings = activity;
    }

}
