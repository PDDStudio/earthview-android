package com.pddstudio.earthview;

import com.pddstudio.earthview.utils.AsyncLoader;
import com.pddstudio.earthview.utils.IdUtils;
import com.pddstudio.earthview.utils.RandomLoader;
import com.pddstudio.earthview.utils.SingleLoader;


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
    private RandomLoader randomLoader;
    private SingleLoader singleLoader;

    private EarthView() {}

    /**
     * Creates a new {@link EarthView} instance
     * @return a new {@link EarthView} instance
     */
    public static EarthView withGoogle() {
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
        asyncLoader = new AsyncLoader(earthViewCallback, IdUtils.getIdList());
        asyncLoader.execute();
    }

    /**
     * Fetches a list of random EarthViews with the given size and returns the result to the given {@link EarthViewCallback}
     * @param ammount - The amount of EarthViews that should be fetched
     * @param earthViewCallback - The interface the results will be sent to
     */
    public void getEarthWallpapers(int ammount, EarthViewCallback earthViewCallback) {
        randomLoader = new RandomLoader(ammount, earthViewCallback);
        randomLoader.execute();
    }

    /**
     * Fetches a list of random EarthViews with the given size and returns the result to the given {@link EarthViewCallback}.
     * The difference to {#getEarthWallpapers} is that each item will be checked if it already exist or not before returning it.
     * @param ammount - The amount of EarthViews that should be fetched
     * @param earthViewCallback - The interface the results will be sent to
     */
    public void getUniqueEarthWallpapers(int ammount, EarthViewCallback earthViewCallback) {
        randomLoader = new RandomLoader(ammount, earthViewCallback).uniqueOnly();
        randomLoader.execute();
    }

}
