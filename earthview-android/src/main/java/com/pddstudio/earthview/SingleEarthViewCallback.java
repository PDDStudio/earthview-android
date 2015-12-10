package com.pddstudio.earthview;

/**
 * This Class was created by Patrick J
 * on 10.12.15. For more Details and Licensing
 * have a look at the README.md
 */
public interface SingleEarthViewCallback {
    void onStartedLoading();
    void onFinishedLoading(EarthWallpaper earthWallpaper);
}
