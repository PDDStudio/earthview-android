/*
 * Copyright 2016 - Patrick J - ps-app
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

import android.content.Context;

import com.pddstudio.earthview.EarthWallpaper;

import java.util.LinkedList;
import java.util.List;

import io.paperdb.Paper;

/**
 * This Class was created by Patrick J
 * on 18.01.16. For more Details and Licensing
 * have a look at the README.md
 */
public class Favorites {

    private static Favorites favorites;
    private static final String FAVORITES_LIST = "favListItems";

    final Context context;
    List<EarthWallpaper> favoritesList;
    private Favorites(Context context) {
        this.context = context;
        this.favoritesList = Paper.book().read(FAVORITES_LIST, new LinkedList<EarthWallpaper>());
    }

    public static Favorites getFavorites(Context context) {
        if(favorites == null) favorites = new Favorites(context);
        return favorites;
    }

    private void reloadFavorites() {
        this.favoritesList = Paper.book().read(FAVORITES_LIST, new LinkedList<EarthWallpaper>());
    }

    private void saveFavorites() {
        Paper.book().write(FAVORITES_LIST, favoritesList);
    }

    public boolean hasFavorites() {
        return !favoritesList.isEmpty();
    }

    public boolean isFavorite(EarthWallpaper earthWallpaper) {
        for(EarthWallpaper wallpaper : favoritesList) {
            if(earthWallpaper.getWallpaperId().equals(wallpaper.getWallpaperId())) return true;
        }
        return false;
    }

    public void addFavorite(EarthWallpaper earthWallpaper) {
        favoritesList.add(earthWallpaper);
        saveFavorites();
        reloadFavorites();
    }

    public void removeFavorite(EarthWallpaper earthWallpaper) {
        for(int i = 0; i < favoritesList.size(); i++) {
            if(favoritesList.get(i).getWallpaperId().equals(earthWallpaper.getWallpaperId())) {
                favoritesList.remove(i);
            }
        }
        saveFavorites();
        reloadFavorites();
    }

    public List<EarthWallpaper> getFavoritesList() {
        return favoritesList;
    }



}
