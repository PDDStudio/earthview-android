package com.pddstudio.earthview.utils;

/**
 * This Class was created by Patrick J
 * on 10.12.15. For more Details and Licensing
 * have a look at the README.md
 */
public final class ApiUtils {

    private static final String BASE_API_URL = "https://earthview.withgoogle.com/_api/";
    private static final String JSON_EXT = ".json";

    public static final String getApiUrl(String forId) {
        return BASE_API_URL + forId + JSON_EXT;
    }

}
