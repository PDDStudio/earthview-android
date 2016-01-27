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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.lantouzi.wheelview.WheelView;
import com.pddstudio.earthviewer.R;

import java.util.ArrayList;
import java.util.List;

public class MuzeiSettingsActivity extends AppCompatActivity {

    private MuzeiPreferences muzeiPreferences;
    private TextView textView;
    private WheelView wheelView;
    private CheckBox checkBox;

    private static int minCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muzei_settings);

        muzeiPreferences = new MuzeiPreferences(this);
        openMuzeiSettingsDialog();

    }

    private void openMuzeiSettingsDialog() {

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.muzei_settings_dialog_title)
                .customView(R.layout.muzei_dialog_layout, false)
                .positiveText(R.string.muzei_settings_dialog_btn_positve)
                .positiveColorRes(R.color.colorAccent)
                .negativeText(R.string.muzei_settings_dialog_btn_negative)
                .negativeColorRes(R.color.colorAccent)
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        muzeiPreferences.setWheelValue(minCount);
                        muzeiPreferences.setUseHours(checkBox.isChecked());
                        final Intent pref = new Intent(MuzeiSettingsActivity.this, EarthViewerSource.class);
                        pref.putExtra("service", "restarted");
                        startService(pref);
                        Toast.makeText(MuzeiSettingsActivity.this, "Changed interval to " + minCount + (muzeiPreferences.getUseHours() ? " hours" : " minutes"), Toast.LENGTH_SHORT).show();
                        MuzeiSettingsActivity.this.finish();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        MuzeiSettingsActivity.this.finish();
                    }
                })
                .widgetColorRes(R.color.colorAccent)
                .build();

        wheelView = (WheelView) dialog.getCustomView().findViewById(R.id.muzei_wheel);
        wheelView.setItems(getMinutes(180));
        wheelView.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
            @Override
            public void onWheelItemSelected(int position) {
                Log.d("WheelView", "onWheelItemSelected() : position -> " + position);
                minCount = ++position;
            }
        });
        int currentIndex = muzeiPreferences.getWheelValue();
        wheelView.selectIndex(--currentIndex);

        textView = (TextView) dialog.getCustomView().findViewById(R.id.muzei_txt);

        checkBox = (CheckBox) dialog.getCustomView().findViewById(R.id.muzei_check_box);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    textView.setText(R.string.muzei_settings_dialog_text_hours);
                    wheelView.setAdditionCenterMark(getString(R.string.muzei_settings_dialog_postfix_hours, " "));
                    wheelView.selectIndex(wheelView.getSelectedPosition());
                } else {
                    textView.setText(R.string.muzei_settings_dialog_text_minutes);
                    wheelView.setAdditionCenterMark(getString(R.string.muzei_settings_dialog_postfix_min, " "));
                    wheelView.selectIndex(wheelView.getSelectedPosition());
                }
            }
        });
        checkBox.setChecked(muzeiPreferences.getUseHours());
        textView.setText(checkBox.isChecked() ? R.string.muzei_settings_dialog_text_hours : R.string.muzei_settings_dialog_text_minutes);

        dialog.show();
    }

    private List<String> getMinutes(int max) {
        List<String> list = new ArrayList<>();
        for(int i = 1; i < max; i++) {
            list.add(i+"");
        }
        return list;
    }

}
