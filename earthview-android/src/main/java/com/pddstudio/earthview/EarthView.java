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

package com.pddstudio.earthview;

import android.os.AsyncTask;
import android.util.Log;

import com.pddstudio.earthview.utils.AsyncLoader;
import com.pddstudio.earthview.utils.IdUtils;
import com.pddstudio.earthview.utils.SingleLoader;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;


/**
 * This Class was created by Patrick J
 * on 10.12.15. For more Details and Licensing
 * have a look at the README.md
 */

/**
 * The main class to interact with the EarthView library.
 */
public final class EarthView {

    private AsyncLoader asyncLoader;
    private SingleLoader singleLoader;

    private EarthView() {}

    /**
     * Creates a new {@link EarthView} instance
     * @return a new {@link EarthView} instance
     */
    public static EarthView withGoogle() {
        //just for the Name :P (http://EarthView.withGoogle.com)
        return new EarthView();
    }

    /**
     * Fetches a random EarthView and returns it result to the given {@link SingleEarthViewCallback}
     * @param singleEarthViewCallback - The interface the result will be sent to
     */
    public void getRandomEarthWallpaper(SingleEarthViewCallback singleEarthViewCallback) {
        singleLoader =  new SingleLoader(singleEarthViewCallback).loadRandomEarthWallpaper();
        singleLoader.execute();
    }

    /**
     * Fetches the EarthView with the given identifier and returns the result to the {@link SingleEarthViewCallback}
     * @param identifier - The EarthView's identifier
     * @param singleEarthViewCallback - The interface the result will be sent to
     */
    public void getEarthWallpaper(String identifier, SingleEarthViewCallback singleEarthViewCallback) {
        singleLoader = new SingleLoader(singleEarthViewCallback).loadEarthViewById(identifier);
        singleLoader.execute();
    }

    /**
     * Fetches all EarthView and returns the result to the given {@link EarthViewCallback}
     * @param earthViewCallback - The interface the results will be sent to
     */
    public void getAllEarthWallpapers(EarthViewCallback earthViewCallback) {
        cancelEarthViewLoadingTask();
        asyncLoader = new AsyncLoader(earthViewCallback, IdUtils.getIdList());
        asyncLoader.execute();
    }

    /**
     * Fetches a list of random EarthViews with the given size and returns the result to the given {@link EarthViewCallback}
     * @param amount - The amount of EarthViews that should be fetched
     * @param earthViewCallback - The interface the results will be sent to
     */
    public void getEarthWallpapers(int amount, EarthViewCallback earthViewCallback) {
        //// TODO: 15.12.15 fix 
    }

    public void getEarthWallpapers(Set<String> wallpaperIds, EarthViewCallback earthViewCallback) {
        String walls[] = new String[wallpaperIds.size()];
        Iterator<String> stringIterator = wallpaperIds.iterator();
        for(int i = 0; i < wallpaperIds.size(); i++) {
            String id = stringIterator.next();
            walls[i] = id;
            Log.d("Iterator", "added id -> " + id + " on position " + i);
        }
        asyncLoader = new AsyncLoader(earthViewCallback, walls);
        asyncLoader.execute();
    }

    public void getEarthWallpapers(String[] wallpaperIds, EarthViewCallback earthViewCallback) {
        cancelEarthViewLoadingTask();
        asyncLoader = new AsyncLoader(earthViewCallback, wallpaperIds);
        asyncLoader.execute();
    }

    /**
     * Fetches a list of random EarthViews with the given size and returns the result to the given {@link EarthViewCallback}.
     * The difference to {#getEarthWallpapers} is that each item will be checked if it already exist or not before returning it.
     * @param ammount - The amount of EarthViews that should be fetched
     * @param earthViewCallback - The interface the results will be sent to
     */
    public void getUniqueEarthWallpapers(int ammount, EarthViewCallback earthViewCallback) {
        //TODO // FIXME: 15.12.15
    }

    /**
     * Forces all running tasks to be cancelled. If no task is running this call won't do anything.
     * When cancelling a task that is still running {@link EarthViewCallback#onCancelledLoading(Collection)} or
     * {@link SingleEarthViewCallback#onCancelledLoading()} will be invoked
     */
    public void cancelAllRunningTasks() {
        //cancel & rest the async tasks so they can be reused
        if(asyncLoader != null && asyncLoader.getStatus() == AsyncTask.Status.RUNNING) asyncLoader.cancel(true);
        asyncLoader = null;
        if(singleLoader != null && singleLoader.getStatus() == AsyncTask.Status.RUNNING) singleLoader.cancel(true);
        singleLoader = null;
    }

    /**
     * Cancels the {@link SingleLoader} task which was started via {@link #getEarthWallpaper(String, SingleEarthViewCallback)}
     * This method call will have no affect if no {@link SingleLoader} is running.
     * If a running task got cancelled {@link SingleEarthViewCallback#onCancelledLoading()} will be invoked.
     */
    public void cancleSingleEarthViewLoadingTask() {
        if(singleLoader != null && singleLoader.getStatus() == AsyncTask.Status.RUNNING) singleLoader.cancel(true);
        singleLoader = null;
    }

    /**
     * Cancels the running {@link AsyncLoader} task which was started via
     * {@link #getAllEarthWallpapers(EarthViewCallback)}
     * {@link #getEarthWallpapers(int, EarthViewCallback)}
     * {@link #getEarthWallpapers(Set, EarthViewCallback)}
     * {@link #getEarthWallpapers(String[], EarthViewCallback)}
     * This method call will have no affect if no {@link AsyncLoader} is running.
     * If a running task got cancelled {@link EarthViewCallback#onCancelledLoading(Collection)} will be invoked.
     */
    public void cancelEarthViewLoadingTask() {
        if(asyncLoader != null && asyncLoader.getStatus() == AsyncTask.Status.RUNNING) asyncLoader.cancel(true);
        asyncLoader = null;
    }

}
