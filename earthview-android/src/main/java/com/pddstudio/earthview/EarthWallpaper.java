package com.pddstudio.earthview;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * This Class was created by Patrick J
 * on 10.12.15. For more Details and Licensing
 * have a look at the README.md
 */
public class EarthWallpaper implements Serializable {

    //the base url
    private static final String EARTHVIEW_BASE_URL = "https://earthview.withgoogle.com";
    private static final String WALLPAPER_EXTENSION = ".jpg";

    //hidden information for background operations
    @SerializedName("id") private String wallpaperId;
    @SerializedName("slug") private String wallpaperSlug;
    @SerializedName("url") private String wallpaperUrl;
    @SerializedName("api") private String wallpaperApiUrl;

    //general wallpaper info (including gmaps, etc)
    @SerializedName("title") private String wallpaperTitle;
    @SerializedName("lat") private String wallpaperLatitude;
    @SerializedName("lng") private String wallpaperLongitude;
    @SerializedName("photoUrl") private String wallPhotoUrl;
    @SerializedName("thumbUrl") private String wallThumbUrl;
    @SerializedName("downloadUrl") private String wallDownloadUrl;
    @SerializedName("region") private String wallpaperRegion;
    @SerializedName("country") private String wallpaperCountry;
    @SerializedName("attribution") private String wallpaperAttribution;
    @SerializedName("mapsLink") private String googleMapsUrl;
    @SerializedName("mapsTitle") private String googleMapsTitle;

    //api info for next / prev wall
    @SerializedName("nextUrl") private String nextUrl;
    @SerializedName("nextApi") private String nextApi;
    @SerializedName("prevUrl") private String prevUrl;
    @SerializedName("prevApi") private String prevApi;

    public EarthWallpaper() {}

    /**
     * @return - The EarthView's identifier (similar as the officials)
     */
    public String getWallpaperId() {
        return wallpaperId;
    }

    /**
     * The EarthView's Slug
     * @return - The EarthView's Slug
     */
    public String getWallpaperSlug() {
        return wallpaperSlug;
    }

    /**
     * The EarthView's Url (earthview.withgoogle.com/xxx)
     * @return - The EarthView's Url (earthview.withgoogle.com/xxx)
     */
    public String getWallpaperUrl() {
        return EARTHVIEW_BASE_URL + wallpaperUrl;
    }

    /**
     * The EarthView's API-Url
     * @return - The EarthView's API-Url
     */
    public String getWallpaperApiUrl() {
        return EARTHVIEW_BASE_URL + wallpaperApiUrl;
    }

    /**
     * The EarthView's Title
     * @return - The EarthView's Title
     */
    public String getWallpaperTitle() {
        return wallpaperTitle;
    }

    /**
     * The EarthView's shot Latitude
     * @return - The EarthView's shot Latitude
     */
    public String getWallpaperLatitude() {
        return wallpaperLatitude;
    }

    /**
     * The EarthView's shot Longitude
     * @return - The EarthView's shot Longitude
     */
    public String getWallpaperLongitude() {
        return wallpaperLongitude;
    }

    /**
     * The EarthView's photo URL (without 'EarthView' footer)
     * @return - The EarthView's photo URL (without 'EarthView' footer)
     */
    public String getWallPhotoUrl() {
        return wallPhotoUrl;
    }

    /**
     * The EarthView's Thumbnail URL (to reduce data when loading multiple images for preview at once)
     * @return - The EarthView's Thumbnail URL (to reduce data when loading multiple images for preview at once)
     */
    public String getWallThumbUrl() {
        return wallThumbUrl;
    }

    /**
     * The EarthView's download URL (as the one on the Homepage - with 'EarthView' footer)
     * @return - The EarthView's download URL (as the one on the Homepage - with 'EarthView' footer)
     */
    public String getWallDownloadUrl() {
        return EARTHVIEW_BASE_URL + wallDownloadUrl;
    }

    /**
     * The EarthView's shot region
     * @return - The EarthView's shot region
     */
    public String getWallpaperRegion() {
        return wallpaperRegion;
    }

    /**
     * The EarthView's shot country
     * @return - The EarthView's shot country
     */
    public String getWallpaperCountry() {
        return wallpaperCountry;
    }

    /**
     * The EarthView's attribution (copyright)
     * @return - The EarthView's attribution (copyright)
     */
    public String getWallpaperAttribution() {
        return wallpaperAttribution;
    }

    /**
     * The EarthView's URL to view the shot on Google Maps
     * @return - The EarthView's URL to view the shot on Google Maps
     */
    public String getGoogleMapsUrl() {
        return googleMapsUrl;
    }

    /**
     * The EarthView's Title - Can be used when linking to the Google Maps Link for example
     * @return - The EarthView's Title - Can be used when linking to the Google Maps Link for example

     */
    public String getGoogleMapsTitle() {
        return googleMapsTitle;
    }

    /**
     * The next EarthView's URL
     * @return - The next EarthView's URL
     */
    public String getNextUrl() {
        return EARTHVIEW_BASE_URL + nextUrl;
    }

    /**
     * The next EarthView's API URL
     * @return - The next EarthView's API URL
     */
    public String getNextApi() {
        return EARTHVIEW_BASE_URL + nextApi;
    }

    /**
     * The previous EarthView's URL
     * @return - The previous EarthView's URL
     */
    public String getPrevUrl() {
        return EARTHVIEW_BASE_URL + prevUrl;
    }

    /**
     * The previous EarthView's API URL
     * @return - The previous EarthView's API URL
     */
    public String getPrevApi() {
        return EARTHVIEW_BASE_URL + prevApi;
    }

    public String getFormattedWallpaperTitle() {
        if(wallpaperTitle.contains("– Earth View from Google")) {
            int ignore = wallpaperTitle.indexOf("– Earth View from Google");
            return wallpaperTitle.substring(0, ignore);
        }
        return wallpaperTitle;
    }

    public String getFormattedFileName(boolean withExtension) {
        int cutIndex = wallpaperSlug.lastIndexOf("-");
        String formattedName = wallpaperSlug.substring(0, cutIndex);
        Log.d("EarthWallpaper", "formattedFileName: " + formattedName);
        if(withExtension) return formattedName + WALLPAPER_EXTENSION;
        else return formattedName;
    }

}