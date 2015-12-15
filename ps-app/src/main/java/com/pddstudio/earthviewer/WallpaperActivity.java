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

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.pddstudio.earthview.EarthWallpaper;
import com.pddstudio.earthview.SingleEarthViewCallback;
import com.pddstudio.earthviewer.utils.DownloadHighResImage;
import com.pddstudio.earthviewer.utils.DownloadWallpaperTask;
import com.pddstudio.earthviewer.utils.Preferences;
import com.pddstudio.earthviewer.views.BaseDialog;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class WallpaperActivity extends AppCompatActivity implements Preferences.PermissionCallback,
        SingleEarthViewCallback, DownloadHighResImage.ImageCachingCallback, DownloadWallpaperTask.DownloadCallback {

    public static final String WALLPAPER_OBJECT = "com.pddstudio.wallpaperrecyclerdemo.earthWallpaperObject";
    public static final String IMAGE_TRANSITION_NAME = "com.pddstudio.wallpaperrecyclerdemo.imageTransitionName";
    public static final String TEXT_TRANSITION_NAME = "com.pddstudio.wallpaperrecyclerdemo.textTransitionName";

    //the selected wallpaper
    private EarthWallpaper earthWallpaper;

    private File downloadDir = new File(Environment.getExternalStorageDirectory(), "EarthViews");

    private MaterialDialog downloadDialog;

    //activity root layout
    private CoordinatorLayout rootLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    //the preview image
    private ImageView appBarImage;
    //the text views which hold the wallpaper information
    private TextView cityTextView;
    private TextView countryTextView;
    private TextView coordinatesTextView;
    private TextView googleMapsHintTextView;
    private TextView attributionTextView;
    private RelativeLayout googleMapsLayout;
    private RelativeLayout attributionLayout;
    private RelativeLayout shareLinkLayout;
    private TextView shareLinkTextView;
    //the FAB menu and it's fab's
    private FloatingActionMenu floatingActionMenu;
    private FloatingActionButton floatingActionShare;
    private FloatingActionButton floatingActionApply;
    private FloatingActionButton floatingActionArchive;
    private FloatingActionButton floatingActionDownload;

    //menu items
    private MenuItem menuItemHD;
    private boolean isHdReady = false;
    private boolean unlock = false;
    private File wallpaperFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);
        Intent data = getIntent();
        earthWallpaper = (EarthWallpaper) data.getExtras().getSerializable(WALLPAPER_OBJECT);
        setContentView(R.layout.activity_wallpaper);

        downloadDialog = new BaseDialog(this).getDownloadDialog();

        if(!downloadDir.exists()) {
            boolean created = downloadDir.mkdir();
            Log.d("WallpaperActivity", "Download directory didn't exist : creation -> " + created);
        }

        if(!Preferences.exists()) Preferences.init(this);

        downloadDir = new File(Preferences.getInstance().getWallpaperDownloadDirectory());

        rootLayout = (CoordinatorLayout) findViewById(R.id.wallpaper_coordinator_root);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.wallpaper_collapsing_toolbar);
        //collapsingToolbarLayout.setExpandedTitleTextAppearance(android.R.style.TextAppearance_DeviceDefault_Medium);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.FullscreenTextPreview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_details_view);
        toolbar.setTitle(earthWallpaper.getFormattedWallpaperTitle());
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //set and load the appbar image
        appBarImage = (ImageView) findViewById(R.id.wall_preview);
        Picasso.with(this).load(earthWallpaper.getWallThumbUrl()).into(appBarImage);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBarImage.setTransitionName(data.getExtras().getString(IMAGE_TRANSITION_NAME));
            //toolbar.setTransitionName(data.getExtras().getString(TEXT_TRANSITION_NAME));
        }

        //assigning the information to the views
        cityTextView = (TextView) findViewById(R.id.details_location_text);
        if(earthWallpaper.getWallpaperRegion() == null) {
            cityTextView.setText(R.string.details_unknown);
        } else {
            cityTextView.setText(earthWallpaper.getWallpaperRegion());
        }
        countryTextView = (TextView) findViewById(R.id.details_country_text);
        if(earthWallpaper.getWallpaperCountry() == null) {
            countryTextView.setText(R.string.details_unknown);
        } else {
            countryTextView.setText(earthWallpaper.getWallpaperCountry());
        }
        coordinatesTextView = (TextView) findViewById(R.id.details_coords_text);
        coordinatesTextView.setText(getFormattedCoordinates());

        //setting the google maps layout stuff
        googleMapsLayout = (RelativeLayout) findViewById(R.id.gmaps_nav_layout);
        googleMapsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent googleMaps = new Intent(Intent.ACTION_VIEW, Uri.parse(earthWallpaper.getGoogleMapsUrl()));
                startActivity(googleMaps);
            }
        });
        googleMapsHintTextView = (TextView) findViewById(R.id.details_gmaps_link_icon_text);
        googleMapsHintTextView.setText(earthWallpaper.getGoogleMapsTitle());

        final int[] c = {0};
        attributionLayout = (RelativeLayout) findViewById(R.id.details_attribution_layout);
        attributionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c[0]++;
                if(c[0] >= 20) unlock = true;
            }
        });

        attributionTextView = (TextView) findViewById(R.id.details_attribution_text);
        if(earthWallpaper.getWallpaperAttribution() == null) {
            attributionTextView.setText(R.string.details_unknown);
        } else {
            attributionTextView.setText(earthWallpaper.getWallpaperAttribution());
        }

        shareLinkLayout = (RelativeLayout) findViewById(R.id.share_link_layout);
        shareLinkLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEarthViewShow();
            }
        });

        shareLinkTextView = (TextView) findViewById(R.id.details_share_link_text);
        shareLinkTextView.setText(earthWallpaper.getShareUrl());

        //inflating the fab menu and it's entries
        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.details_fab_menu);
        floatingActionMenu.setClosedOnTouchOutside(true);

        floatingActionShare = (FloatingActionButton) findViewById(R.id.fab_item_share);
        floatingActionShare.setImageDrawable(new IconicsDrawable(this).icon(CommunityMaterial.Icon.cmd_share_variant).color(Color.WHITE).sizeDp(16));
        floatingActionShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEarthViewShare();
                if (floatingActionMenu.isOpened()) floatingActionMenu.close(true);
            }
        });

        floatingActionApply = (FloatingActionButton) findViewById(R.id.fab_item_apply);
        floatingActionApply.setImageDrawable(new IconicsDrawable(this).icon(CommunityMaterial.Icon.cmd_check).color(Color.WHITE).sizeDp(16));
        floatingActionApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Snackbar.make(rootLayout, "Applying is coming soon!", Snackbar.LENGTH_SHORT).show();
                onSetAsWallpaper();
                if (floatingActionMenu.isOpened()) floatingActionMenu.close(true);
            }
        });

        floatingActionArchive = (FloatingActionButton) findViewById(R.id.fab_item_archive);
        if(Preferences.getInstance().isFavorite(earthWallpaper)) {
            floatingActionArchive.setImageDrawable(new IconicsDrawable(this).icon(CommunityMaterial.Icon.cmd_heart_outline).color(Color.WHITE).sizeDp(16));
            floatingActionArchive.setLabelText(getResources().getString(R.string.fab_menu_item_remove_archive));
        } else {
            floatingActionArchive.setImageDrawable(new IconicsDrawable(this).icon(CommunityMaterial.Icon.cmd_heart).color(Color.WHITE).sizeDp(16));
            floatingActionArchive.setLabelText(getResources().getString(R.string.fab_menu_item_archive));
        }
        floatingActionArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(floatingActionMenu.isOpened()) floatingActionMenu.close(false);

                if (Preferences.getInstance().isFavorite(earthWallpaper)) {
                    Preferences.getInstance().removeFavorite(earthWallpaper);
                    floatingActionArchive.setImageDrawable(new IconicsDrawable(WallpaperActivity.this).icon(CommunityMaterial.Icon.cmd_heart).color(Color.WHITE).sizeDp(16));
                    floatingActionArchive.setLabelText(getResources().getString(R.string.fab_menu_item_archive));
                    Snackbar.make(rootLayout, R.string.fab_snack_bar_fav_removed, Snackbar.LENGTH_SHORT).setAction(R.string.fab_snack_bar_fav_action, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {}
                    }).show();
                } else {
                    Preferences.getInstance().addFavorite(earthWallpaper);
                    floatingActionArchive.setImageDrawable(new IconicsDrawable(WallpaperActivity.this).icon(CommunityMaterial.Icon.cmd_heart_outline).color(Color.WHITE).sizeDp(16));
                    floatingActionArchive.setLabelText(getResources().getString(R.string.fab_menu_item_remove_archive));
                    Snackbar.make(rootLayout, R.string.fab_snack_bar_fav_added, Snackbar.LENGTH_SHORT).setAction(R.string.fab_snack_bar_fav_action, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {}
                    }).show();
                }
            }
        });

        floatingActionDownload = (FloatingActionButton) findViewById(R.id.fab_item_download);
        floatingActionDownload.setImageDrawable(new IconicsDrawable(this).icon(CommunityMaterial.Icon.cmd_download).color(Color.WHITE).sizeDp(16));
        floatingActionDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (floatingActionMenu.isOpened()) floatingActionMenu.close(true);
                if (Preferences.getInstance().canWriteExternalStorage()) {
                    new DownloadWallpaperTask(WallpaperActivity.this, Preferences.getInstance().getWallpaperDownloadDir(), earthWallpaper).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, unlock);
                } else {
                    Preferences.getInstance().requestExternalStoragePermission(WallpaperActivity.this, WallpaperActivity.this, false);
                }
            }
        });

        //start loading the wallpaper in higher resolution if on wifi
        if(Preferences.getInstance().isOnWiFi()) {
            //EarthView.withGoogle().getEarthWallpaper(earthWallpaper.getWallpaperId(), this);
            new DownloadHighResImage(this, earthWallpaper, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

    }

    @Override
    public void onBackPressed() {
        if(floatingActionMenu != null && floatingActionMenu.isOpened()) {
            floatingActionMenu.close(true);
        } else {
            WallpaperActivity.this.supportFinishAfterTransition();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_load_full_wall:
                if(appBarImage != null) {
                    Picasso.with(WallpaperActivity.this).load(wallpaperFile).into(appBarImage);
                    menuItemHD.setVisible(false);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.wall_prev_menu, menu);
        menuItemHD = menu.findItem(R.id.menu_load_full_wall);
        menuItemHD.setIcon(new IconicsDrawable(this).sizeDp(24).icon(GoogleMaterial.Icon.gmd_hd).colorRes(R.color.md_white_1000));
        menuItemHD.setVisible(isHdReady);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(grantResults.length > 0) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Preferences.getInstance().onReceivedPermissionCallback(requestCode, true);
                if(Preferences.getInstance().getWallpaperDownloadDir() != null) {
                    new DownloadWallpaperTask(this, Preferences.getInstance().getWallpaperDownloadDir(), earthWallpaper).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, unlock);
                }
            }
            else {
                Preferences.getInstance().onReceivedPermissionCallback(requestCode, false);
                Snackbar.make(rootLayout, R.string.snack_bar_perm_denied, Snackbar.LENGTH_LONG).setAction(R.string.snack_bar_perm_denied_action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!Preferences.getInstance().canWriteExternalStorage())
                        Preferences.getInstance().requestExternalStoragePermission(WallpaperActivity.this, WallpaperActivity.this, true);
                    }
                }).show();
            }
        }
    }

    @Override
    public void onPermissionExplanationRequired(boolean showExplanation, String requiredPermission) {
        if(showExplanation) WallpaperActivity.this.showPermissionExplanation(requiredPermission);
    }

    private void showPermissionExplanation(String permission) {
        BaseDialog dialog = new BaseDialog(this).withDetails(this, this);
        if(permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            dialog.showPermissionExplanationDialog(BaseDialog.PERMISSION_EXPLANATION_STORAGE);
        }
    }

    private String getFormattedCoordinates() {
        String lat = earthWallpaper.getWallpaperLatitude();
        String lon = earthWallpaper.getWallpaperLongitude();
        if(lat == null) lat = getResources().getString(R.string.details_unknown);
        if(lon == null) lat = getResources().getString(R.string.details_unknown);
        return "Lat. " + lat + " | Long. " + lon;
    }

    private void onEarthViewShare() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.item_share_title) + earthWallpaper.getShareUrl());
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.item_share_title);
        startActivity(Intent.createChooser(shareIntent, getString(R.string.item_share_via)));
    }

    private void onEarthViewShow() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        browserIntent.setData(Uri.parse(earthWallpaper.getShareUrl()));
        startActivity(Intent.createChooser(browserIntent, getString(R.string.item_open_via)));
    }

    private void onSetAsWallpaper() {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        if(wallpaperFile == null || !wallpaperFile.exists()) {
            Snackbar.make(rootLayout, R.string.fab_snack_bar_image_not_cached, Snackbar.LENGTH_LONG).show();
        } else {
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                Bitmap wallpaper = BitmapFactory.decodeFile(wallpaperFile.getAbsolutePath());
                if(wallpaper != null) {
                    try {
                        wallpaperManager.setBitmap(wallpaper);
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }
            } else {
                Uri cropWallpaperUri = FileProvider.getUriForFile(this, "com.pddstudio.fileprovider", wallpaperFile);
                if(cropWallpaperUri != null) {
                    Intent cropIntent = wallpaperManager.getCropAndSetWallpaperIntent(cropWallpaperUri);
                    startActivity(cropIntent);
                }
            }
        }
    }

    @Override
    public void onStartedLoading() {

    }

    @Override
    public void onFinishedLoading(EarthWallpaper earthWallpaper) {

    }

    @Override
    public void onCancelledLoading() {

    }

    @Override
    public void onCachingFinished(boolean success, File location) {
        if(success) {
            wallpaperFile = location;
            Picasso.with(this).load(location).into(appBarImage);
        }
    }

    @Override
    public void onDownloadStarted() {
        downloadDialog.show();
    }

    @Override
    public void onDownloadFinished(boolean success, File file) {
        if(downloadDialog.isShowing()) downloadDialog.cancel();
        if(success) {
            downloadDialog = new BaseDialog(this).getDownloadFinishedDialog(true, file.getAbsolutePath());
        } else {
            downloadDialog = new BaseDialog(this).getDownloadFinishedDialog(false, null);
        }
        downloadDialog.show();
    }
}
