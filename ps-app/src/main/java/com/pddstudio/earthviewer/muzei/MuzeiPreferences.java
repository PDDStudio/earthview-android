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

package com.pddstudio.earthviewer.muzei;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * This Class was created by Patrick J
 * on 16.12.15. For more Details and Licensing
 * have a look at the README.md
 */
public class MuzeiPreferences {

    //name of the preference file in the system
    private static final String MUZEI_PREFS = "com.pddstudio.earthviewer.muzei";

    //preference identifiers
    private static final String USE_HOURS = "use_hrs";
    private static final String WHEEL_VAL = "int_val";

    private final SharedPreferences sharedPreferences;

    public MuzeiPreferences(Context context) {
        this.sharedPreferences = context.getSharedPreferences(MUZEI_PREFS, Context.MODE_PRIVATE);
    }

    public int getRotateTimeMilis() {
        return calculateFromPreferences();
    }

    public boolean getUseHours() {
        return sharedPreferences.getBoolean(USE_HOURS, false);
    }

    public void setUseHours(boolean useHours) {
        this.sharedPreferences.edit().putBoolean(USE_HOURS, useHours).apply();
    }

    public void setWheelValue(int value) {
        this.sharedPreferences.edit().putInt(WHEEL_VAL, value).apply();
    }

    public int getWheelValue() {
        return sharedPreferences.getInt(WHEEL_VAL, 1);
    }

    private int calculateFromPreferences() {
        int value = getWheelValue();
        if(getUseHours()) {
            value = value * 60 * 60 * 1000;
        } else {
            value = value * 60 * 1000;
        }
        return value;
    }


}
