package com.pddstudio.earthview;

import java.util.Collection;

/**
 * This Class was created by Patrick J
 * on 10.12.15. For more Details and Licensing
 * have a look at the README.md
 */
public interface EarthViewCallback {
    void onStartedLoading();
    void onItemLoaded(EarthWallpaper earthWallpaper);
    void onFinishedLoading(Collection<EarthWallpaper> earthWallpapers);
}
