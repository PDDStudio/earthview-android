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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.etiennelawlor.quickreturn.library.enums.QuickReturnViewType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnRecyclerViewOnScrollListener;
import com.mikepenz.aboutlibraries.LibTaskExecutor;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.pddstudio.earthview.EarthView;
import com.pddstudio.earthview.EarthViewCallback;
import com.pddstudio.earthview.EarthWallpaper;
import com.pddstudio.earthviewer.utils.Preferences;
import com.pddstudio.earthviewer.views.BaseDialog;

import java.util.ArrayList;
import java.util.Collection;

public class DemoActivity extends AppCompatActivity implements Preferences.PermissionCallback, EarthViewCallback,
        BaseDialog.CancelLoadingDialog.CancelDialogCallback, BaseDialog.NoWifiDialogCallback {

    //demo ui components
    Toolbar toolbar;
    Drawer drawer;
    CoordinatorLayout rootLayout;
    LinearLayout loadingLayout;
    LinearLayout noConnectionLayout;
    Button loadingButton;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter earthViewAdapter;
    QuickReturnRecyclerViewOnScrollListener scrollListener;
    QuickReturnRecyclerViewOnScrollListener connectionScrollListener;
    FrameLayout footerLayout;
    TextView footerLoadingText;
    Button footerButton;


    private static final boolean LOAD_ASYNC = true;
    private static int DRAWER_POSITION = 1;

    private static final int DRAWER_HOME = 1;
    private static final int DRAWER_FAV = 2;
    private static final int DRAWER_PREFS = 3;
    private static final int DRAWER_ABOUT = 4;
    private static final int DRAWER_GIT = 5;

    private EarthView earthView = EarthView.withGoogle();
    private int itemsTotal = -1;
    private int itemsCurrent = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!Preferences.exists()) {
            //init the preferences
            Preferences.init(this);
            //check for network access and request if not set
            if(!Preferences.getInstance().canAccessNetworkInfo()) {
                new BaseDialog(this).showPermissionExplanationDialog(BaseDialog.PERMISSION_EXPLANATION_NETWORK);
            }
        }

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setContentView(R.layout.activity_demo_pre);
            preparePreLollipop();
        } else {
            setContentView(R.layout.activity_demo);
            prepareLollipop();
        }

    }

    private void prepareLollipop() {
        Log.d("DemoActivity", "testing current active network connection... User is on Wifi? : " + Preferences.getInstance().isOnWiFi());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_bar_title);
        //toolbar.setSubtitle(R.string.app_bar_sub_title);
        setSupportActionBar(toolbar);

        rootLayout = (CoordinatorLayout) findViewById(R.id.coordinator_root);

        //build the drawer
        setUpNavigationDrawer();
        if(drawer != null) drawer.setSelection(DRAWER_HOME);

        loadingLayout = (LinearLayout) findViewById(R.id.loading_layout);
        loadingButton = (Button) findViewById(R.id.info_start_loading_bt);
        loadingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Preferences.getInstance().isOnWiFi()) {
                    loadingButton.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    earthView.getAllEarthWallpapers(DemoActivity.this, Preferences.getInstance().getShuffleWallpapers());
                } else {
                    new BaseDialog(DemoActivity.this).showNoWifiConnectionDialog(DemoActivity.this);
                }
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.loading_progress_bar);

        recyclerView = (RecyclerView) findViewById(R.id.earth_wall_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this, Preferences.getInstance().getGridColumnCount());
        recyclerView.setLayoutManager(layoutManager);

        footerLayout = (FrameLayout) findViewById(R.id.loading_footer_bar_frame);
        footerLayout.setVisibility(View.GONE);
        footerLoadingText = (TextView) findViewById(R.id.progress_text);
        footerLoadingText.setText(R.string.quick_return_loading_text);

        footerButton = (Button) findViewById(R.id.footer_cancel_btn);
        footerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseDialog.CancelLoadingDialog.showCancelDialog(DemoActivity.this);
            }
        });

        scrollListener = new QuickReturnRecyclerViewOnScrollListener.Builder(QuickReturnViewType.FOOTER)
                .footer(footerLayout)
                .minFooterTranslation(200)
                .isSnappable(true)
                .build();

        recyclerView.addOnScrollListener(scrollListener);

        noConnectionLayout = (LinearLayout) findViewById(R.id.no_connection_header);
        noConnectionLayout.setVisibility(View.GONE);

        connectionScrollListener = new QuickReturnRecyclerViewOnScrollListener.Builder(QuickReturnViewType.GOOGLE_PLUS)
                .header(noConnectionLayout)
                .minHeaderTranslation(200)
                .isSnappable(true)
                .build();

        if(LOAD_ASYNC) {
            loadingLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            earthViewAdapter = new EarthViewAdapter(null, this);
            recyclerView.setAdapter(earthViewAdapter);
        } else {
            loadingLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        if(Preferences.getInstance().getAutoLoadOnWifi()) {
            if(Preferences.getInstance().isOnWiFi()) {
                loadingButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                earthView.getAllEarthWallpapers(this, Preferences.getInstance().getShuffleWallpapers());
            } else {
                new BaseDialog(this).showNoWifiConnectionDialog(this);
            }
        }
    }

    private void preparePreLollipop() {
        Log.d("DemoActivity", "testing current active network connection... User is on Wifi? : " + Preferences.getInstance().isOnWiFi());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_bar_title);
        //toolbar.setSubtitle(R.string.app_bar_sub_title);
        setSupportActionBar(toolbar);

        rootLayout = (CoordinatorLayout) findViewById(R.id.coordinator_root);

        //build the drawer
        setUpNavigationDrawer();
        if(drawer != null) drawer.setSelection(DRAWER_HOME);

        loadingLayout = (LinearLayout) findViewById(R.id.loading_layout);
        loadingButton = (Button) findViewById(R.id.info_start_loading_bt);
        loadingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Preferences.getInstance().isOnWiFi()) {
                    loadingButton.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    earthView.getAllEarthWallpapers(DemoActivity.this, Preferences.getInstance().getShuffleWallpapers());
                } else {
                    new BaseDialog(DemoActivity.this).showNoWifiConnectionDialog(DemoActivity.this);
                }
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.loading_progress_bar);

        recyclerView = (RecyclerView) findViewById(R.id.earth_wall_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new GridLayoutManager(this, Preferences.getInstance().getGridColumnCount());
        recyclerView.setLayoutManager(layoutManager);

        if(LOAD_ASYNC) {
            loadingLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            earthViewAdapter = new EarthViewAdapter(null, this);
            recyclerView.setAdapter(earthViewAdapter);
        } else {
            loadingLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        if(Preferences.getInstance().getAutoLoadOnWifi()) {
            if(Preferences.getInstance().isOnWiFi()) {
                loadingButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                earthView.getAllEarthWallpapers(this, Preferences.getInstance().getShuffleWallpapers());
            } else {
                new BaseDialog(this).showNoWifiConnectionDialog(this);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Preferences.exists()) {
            Preferences.getInstance().registerPreferenceListener();
            if(earthViewAdapter != null && recyclerView != null && layoutManager != null) {
                if(((GridLayoutManager) layoutManager).getSpanCount() != Preferences.getInstance().getGridColumnCount()) {
                    ((GridLayoutManager) layoutManager).setSpanCount(Preferences.getInstance().getGridColumnCount());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(earthViewAdapter);
                }
            }
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
    public void onBackPressed() {
        if(drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            BaseDialog dialog = new BaseDialog(this);
            dialog.showExitDialog(new BaseDialog.ExitDialogListener() {
                @Override
                public void onExitConfirmed(boolean confirmExit) {
                    if (confirmExit) {
                        Preferences.getInstance().clearCache();
                        earthView.cancelAllRunningTasks();
                        DemoActivity.this.finish();
                        System.exit(0);
                    }
                }
            });
        }
    }

    private void wallFetchAsync(EarthWallpaper earthWallpaper) {
        if(loadingLayout.getVisibility() == View.VISIBLE) loadingLayout.setVisibility(View.GONE);
        if(!((EarthViewAdapter) recyclerView.getAdapter()).hasOnItemClickListener()) {
            ((EarthViewAdapter) recyclerView.getAdapter()).setOnItemClickListener(onItemClickListener);
        }
        ((EarthViewAdapter) recyclerView.getAdapter()).addItem(earthWallpaper);
        recyclerView.getAdapter().notifyItemInserted(recyclerView.getAdapter().getItemCount());
        updateFooterBar(recyclerView.getAdapter().getItemCount());
    }

    private void prepareFooterBar(int totalCount) {
        if(Preferences.getInstance().getLoadingBarEnabled()) {
            if(footerLayout != null) {
                if(totalCount == -1) footerLayout.setVisibility(View.GONE);
                else {
                    setTotalItems(totalCount);
                    footerLayout.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void setTotalItems(int count) {
        if(itemsTotal == -1) itemsTotal = count;
    }

    private void updateFooterBar(int count) {
        String baseText = getResources().getString(R.string.quick_return_loading_text);
        baseText = baseText + " (" + count + " of " + itemsTotal + ")";
        if(footerLoadingText != null) footerLoadingText.setText(baseText);
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

                wallActivity = new Intent(DemoActivity.this, WallpaperActivity.class);
                wallActivity.putExtra(WallpaperActivity.WALLPAPER_OBJECT, earthWallpaper);

                wallActivity.putExtra(WallpaperActivity.IMAGE_TRANSITION_NAME, clickedImageView.getTransitionName());
                wallActivity.putExtra(WallpaperActivity.TEXT_TRANSITION_NAME, clickedTextView.getTransitionName());

                Pair<View, String> wallpaperImage = Pair.create(clickedImageView, clickedImageView.getTransitionName());
                Pair<View, String> wallpaperText = Pair.create(clickedTextView, clickedTextView.getTransitionName());

                //ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(DemoActivity.this, wallpaperImage, wallpaperText);
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(DemoActivity.this, wallpaperImage);
                startActivity(wallActivity, compat.toBundle());

            } else {
                wallActivity = new Intent(DemoActivity.this, WallpaperActivityPre.class);
                wallActivity.putExtra(WallpaperActivity.WALLPAPER_OBJECT, earthWallpaper);
                startActivity(wallActivity);
            }

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(grantResults.length > 0) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) Preferences.getInstance().onReceivedPermissionCallback(requestCode, true);
            else Preferences.getInstance().onReceivedPermissionCallback(requestCode, false);
        }
    }

    @Override
    public void onPermissionExplanationRequired(boolean showExplanation, String requiredPermission) {
        if(showExplanation) DemoActivity.this.showPermissionExplanation(requiredPermission);
    }

    private void showPermissionExplanation(String permission) {
        BaseDialog dialog = new BaseDialog(this);
        if(permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            dialog.showPermissionExplanationDialog(BaseDialog.PERMISSION_EXPLANATION_STORAGE);
        } else if (permission.equals(Manifest.permission.INTERNET)) {
            dialog.showPermissionExplanationDialog(BaseDialog.PERMISSION_EXPLANATION_INTERNET);
        }
    }

    private void setUpNavigationDrawer() {
        ArrayList<IDrawerItem> drawerItems = new ArrayList<>();
        //creating and adding the items to the drawer
        SectionDrawerItem wallSection = new SectionDrawerItem().withName(R.string.navigation_section_wallpaper).withDivider(false);
        drawerItems.add(wallSection);
        PrimaryDrawerItem homeItem = new PrimaryDrawerItem().withIdentifier(DRAWER_HOME).withName(R.string.navigation_item_home).withDescription(R.string.navigation_item_home_desc).withIcon(CommunityMaterial.Icon.cmd_terrain).withSetSelected(true);
        drawerItems.add(homeItem);
        PrimaryDrawerItem favItem = new PrimaryDrawerItem().withIdentifier(DRAWER_FAV).withName(R.string.navigation_item_favourites).withDescription(R.string.navigation_item_favourites_desc).withIcon(CommunityMaterial.Icon.cmd_heart).withSelectable(false);
        drawerItems.add(favItem);
        SectionDrawerItem aboutSection = new SectionDrawerItem().withName(R.string.navigation_section_about);
        drawerItems.add(aboutSection);
        PrimaryDrawerItem gitItem = new PrimaryDrawerItem().withIdentifier(DRAWER_GIT).withName(R.string.navigation_item_git).withIcon(CommunityMaterial.Icon.cmd_github_circle).withSelectable(false);
        drawerItems.add(gitItem);
        PrimaryDrawerItem aboutItem = new PrimaryDrawerItem().withIdentifier(DRAWER_ABOUT).withName(R.string.navigation_item_about).withIcon(CommunityMaterial.Icon.cmd_information_outline).withSelectable(false);
        drawerItems.add(aboutItem);
        DividerDrawerItem dividerDrawerItem = new DividerDrawerItem();
        drawerItems.add(dividerDrawerItem);
        PrimaryDrawerItem settingsItem = new PrimaryDrawerItem().withIdentifier(DRAWER_PREFS).withName(R.string.navigation_item_settings).withIcon(CommunityMaterial.Icon.cmd_settings).withSelectable(false);
        drawerItems.add(settingsItem);

        drawer = new DrawerBuilder(this)
                .withToolbar(toolbar)
                .withDisplayBelowStatusBar(true)
                .withTranslucentStatusBar(true)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withDrawerItems(drawerItems)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (drawerItem.getIdentifier()) {
                            case DRAWER_HOME:
                                if (DRAWER_POSITION != DRAWER_HOME) {
                                    earthView.cancelAllRunningTasks();
                                    if (recyclerView != null && recyclerView.getAdapter() != null) {
                                        ((EarthViewAdapter) recyclerView.getAdapter()).cleanDataSet();
                                    }
                                    DRAWER_POSITION = DRAWER_HOME;
                                }
                                break;
                            case DRAWER_FAV:
                                if (DRAWER_POSITION != DRAWER_FAV) {
                                    //earthView.cancelAllRunningTasks();
                                    //if(recyclerView != null && recyclerView.getAdapter() != null) {
                                    //    ((EarthViewAdapter) recyclerView.getAdapter()).cleanDataSet();
                                    //}
                                    Intent favActivity;
                                    if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                                        favActivity = new Intent(DemoActivity.this, FavoritesActivityPre.class);
                                    } else {
                                        favActivity = new Intent(DemoActivity.this, FavoritesActivity.class);
                                    }
                                    startActivity(favActivity);
                                    DRAWER_POSITION = DRAWER_HOME;
                                }
                                break;
                            case DRAWER_ABOUT:
                                new LibsBuilder()
                                        .withSlideInAnimation(true)
                                        .withAutoDetect(true)
                                        .withActivityTitle(DemoActivity.this.getResources().getString(R.string.about_activity_title))
                                        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                                        .withFields(R.string.class.getFields())
                                        .withLibTaskExecutor(LibTaskExecutor.THREAD_POOL_EXECUTOR)
                                        .start(DemoActivity.this);
                                break;
                            case DRAWER_GIT:
                                final String GIT_URL = "https://github.com/PDDStudio/earthview-android";
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                browserIntent.setData(Uri.parse(GIT_URL));
                                startActivity(Intent.createChooser(browserIntent, getString(R.string.item_open_via)));
                                break;
                            case DRAWER_PREFS:
                                Intent preferences;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    preferences = new Intent(DemoActivity.this, SettingsActivity.class);
                                } else {
                                    preferences = new Intent(DemoActivity.this, SettingsActivityPre.class);
                                }
                                startActivity(preferences);
                                break;
                        }
                        if (drawer.isDrawerOpen()) drawer.closeDrawer();
                        return true;
                    }
                })
                .withHeader(R.layout.drawer_header)
                .build();
    }

    @Override
    public void onStartedLoading(int totalItemSize) {
        prepareFooterBar(totalItemSize);
    }

    @Override
    public void onItemLoaded(EarthWallpaper earthWallpaper) {
        wallFetchAsync(earthWallpaper);
    }

    @Override
    public void onFinishedLoading(Collection<EarthWallpaper> earthWallpapers) {
        if(recyclerView != null) recyclerView.removeOnScrollListener(scrollListener);
        if(footerLayout != null) footerLayout.setVisibility(View.GONE);
        if(rootLayout != null) Snackbar.make(rootLayout, R.string.snack_bar_loading_complete, Snackbar.LENGTH_LONG)
                .setAction(R.string.snack_bar_loading_cancel_confirmed_action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {}
                }).show();
    }

    @Override
    public void onCancelledLoading(Collection<EarthWallpaper> earthWallpapers) {

    }

    @Override
    public void onCancelConfirmed() {
        if(earthView != null) {
            earthView.cancelAllRunningTasks();
        }
        if(recyclerView != null) recyclerView.removeOnScrollListener(scrollListener);
        if(footerLayout != null) footerLayout.setVisibility(View.GONE);
        if(rootLayout != null) Snackbar.make(rootLayout, R.string.snack_bar_loading_cancel_confirmed, Snackbar.LENGTH_LONG)
                .setAction(R.string.snack_bar_loading_cancel_confirmed_action, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {}
                }).show();
    }

    @Override
    public void onLoadWithoutWifiConfirmed() {
        loadingButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        earthView.getAllEarthWallpapers(this, Preferences.getInstance().getShuffleWallpapers());
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) recyclerView.addOnScrollListener(connectionScrollListener);
    }
}
