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

package com.pddstudio.earthviewer.utils.preferences;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.preference.Preference;
import android.util.AttributeSet;
import android.widget.Toast;

/**
 * This Class was created by Patrick J
 * on 08.12.15. For more Details and Licensing
 * have a look at the README.md
 */
public class InfoPreferenceItem extends Preference {

    int click = 0;
    Toast teaseToast;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InfoPreferenceItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public InfoPreferenceItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public InfoPreferenceItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InfoPreferenceItem(Context context) {
        super(context);
    }

    private void showEasterEgg() {
        if(teaseToast != null) teaseToast.cancel();
        teaseToast = Toast.makeText(getContext(), "No, you're still not a developer!", Toast.LENGTH_SHORT);
        teaseToast.show();
    }

    private void showTeaser(int ammoh) {
        if(teaseToast != null)  teaseToast.cancel();
        teaseToast = Toast.makeText(getContext(), "Only " + ammoh + " steps away!", Toast.LENGTH_SHORT);
        teaseToast.show();
    }

    @Override
    public void onClick() {
        if (click == 5) {
            showEasterEgg();
        } else if (click == 4) {
            showTeaser(1);
        } else if (click == 3) {
            showTeaser(2);
        } else if (click == 2) {
            showTeaser(3);
        }
        click++;
    }

}
