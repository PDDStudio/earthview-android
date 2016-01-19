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

package com.pddstudio.earthviewer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.pddstudio.earthview.EarthWallpaper;
import com.pddstudio.earthviewer.utils.Favorites;
import com.pddstudio.earthviewer.utils.Preferences;

public class FavoritesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Favorites favorites;
    private RecyclerView recyclerView;
    private EarthViewAdapter earthViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private RelativeLayout emptyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        toolbar = (Toolbar) findViewById(R.id.favorites_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        favorites = Favorites.getFavorites(this);

        emptyLayout = (RelativeLayout) findViewById(R.id.favEmptyLayout);

        recyclerView = (RecyclerView) findViewById(R.id.favRecyclerView);
        layoutManager = new GridLayoutManager(this, Preferences.getInstance().getGridColumnCount());



        if(favorites.hasFavorites()) {
            earthViewAdapter = new EarthViewAdapter(favorites.getFavoritesList(), this);
            earthViewAdapter.setOnItemClickListener(onItemClickListener);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(earthViewAdapter);
        } else {
            showEmptyFavorites();
        }

        //tint the navigation bar if set
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(Preferences.getInstance().getTintNavigationBar()) {
                getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
            } else {
                getWindow().setNavigationBarColor(Color.BLACK);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(favorites.hasFavorites()) {
            earthViewAdapter = new EarthViewAdapter(favorites.getFavoritesList(), this);
            earthViewAdapter.setOnItemClickListener(onItemClickListener);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(earthViewAdapter);
        } else {
            showEmptyFavorites();
        }
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

    private void showEmptyFavorites() {
        recyclerView.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);
    }

    private final EarthViewAdapter.OnItemClickListener onItemClickListener = new EarthViewAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {

            EarthWallpaper earthWallpaper = ((EarthViewAdapter) earthViewAdapter).getItemAtPosition(position);
            Log.d("EarthViewAdapter/click", "Clicked on wallpaper: " + earthWallpaper.getWallpaperTitle() + " | " + earthWallpaper.getFormattedFileName(true, true));

            Intent wallActivity;

            View clickedImageView = view.findViewById(R.id.wall);
            View clickedTextView = view.findViewById(R.id.name);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                wallActivity = new Intent(FavoritesActivity.this, WallpaperActivity.class);
                wallActivity.putExtra(WallpaperActivity.WALLPAPER_OBJECT, earthWallpaper);

                wallActivity.putExtra(WallpaperActivity.IMAGE_TRANSITION_NAME, clickedImageView.getTransitionName());
                wallActivity.putExtra(WallpaperActivity.TEXT_TRANSITION_NAME, clickedTextView.getTransitionName());

                Pair<View, String> wallpaperImage = Pair.create(clickedImageView, clickedImageView.getTransitionName());
                Pair<View, String> wallpaperText = Pair.create(clickedTextView, clickedTextView.getTransitionName());

                //ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(DemoActivity.this, wallpaperImage, wallpaperText);
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(FavoritesActivity.this, wallpaperImage);
                startActivity(wallActivity, compat.toBundle());

            } else {
                wallActivity = new Intent(FavoritesActivity.this, WallpaperActivityPre.class);
                wallActivity.putExtra(WallpaperActivity.WALLPAPER_OBJECT, earthWallpaper);
                startActivity(wallActivity);
            }

        }
    };

}
