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

package com.pddstudio.earthviewer.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.pddstudio.earthviewer.R;
import com.pddstudio.earthviewer.utils.Preferences;

/**
 * This Class was created by Patrick J
 * on 07.12.15. For more Details and Licensing
 * have a look at the README.md
 */
public class BaseDialog {

    public static final int PERMISSION_EXPLANATION_STORAGE = 1;
    public static final int PERMISSION_EXPLANATION_INTERNET = 2;
    public static final int PERMISSION_EXPLANATION_NETWORK = 3;

    private final Context context;
    private final MaterialDialog dialog;

    private AppCompatActivity appCompatActivity = null;
    private Preferences.PermissionCallback permissionCallback = null;

    public interface ExitDialogListener {
        void onExitConfirmed(boolean confirmExit);
    }

    public interface NoWifiDialogCallback {
        void onLoadWithoutWifiConfirmed();
    }

    public BaseDialog(Context context) {
        this.context = context;
        this.dialog = new MaterialDialog.Builder(context)
                .titleColorRes(R.color.colorPrimary)
                .typeface(Preferences.getInstance().getTypeface(), Preferences.getInstance().getTypeface())
                .build();
    }

    public BaseDialog withDetails(AppCompatActivity appCompatActivity, Preferences.PermissionCallback permissionCallback) {
        this.appCompatActivity = appCompatActivity;
        this.permissionCallback = permissionCallback;
        return this;
    }

    public void showPermissionExplanationDialog(final int permission) {
        if(permission == PERMISSION_EXPLANATION_STORAGE) {
            MaterialDialog.Builder dialogBuilder = dialog.getBuilder();
            dialogBuilder.icon(new IconicsDrawable(context).icon(CommunityMaterial.Icon.cmd_alert).sizeDp(42).colorRes(R.color.colorPrimary))
                    .title(R.string.dialog_perm_storage_title)
                    .content(R.string.dialog_perm_storage_content)
                    .positiveText(R.string.dialog_perm_btn_request_again)
                    .neutralText(R.string.dialog_perm_btn_request_cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                            materialDialog.hide();
                            if (appCompatActivity == null || permissionCallback == null) {
                                Preferences.getInstance().requestExternalStoragePermission();
                            } else {
                                Preferences.getInstance().requestExternalStoragePermission(appCompatActivity, permissionCallback, true);
                            }
                        }
                    })
                    .show();
        } else if(permission == PERMISSION_EXPLANATION_INTERNET) {
            MaterialDialog.Builder dialogBuilder = dialog.getBuilder();
            dialogBuilder.icon(new IconicsDrawable(context).icon(CommunityMaterial.Icon.cmd_account_alert).sizeDp(42).colorRes(R.color.colorPrimary))
                    .title(R.string.dialog_perm_internet_title)
                    .content(R.string.dialog_perm_internet_content)
                    .positiveText(R.string.dialog_perm_btn_request_again)
                    .neutralText(R.string.dialog_perm_btn_request_cancel)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                            materialDialog.hide();
                            Preferences.getInstance().requestInternetPermission();
                        }
                    })
                    .show();
        } else if(permission == PERMISSION_EXPLANATION_NETWORK) {
            MaterialDialog.Builder dialogBuilder = dialog.getBuilder();
            dialogBuilder.icon(new IconicsDrawable(context).icon(CommunityMaterial.Icon.cmd_account_alert).sizeDp(42).colorRes(R.color.colorPrimary))
                    .title(R.string.dialog_perm_network_connection_title)
                    .content(R.string.dialog_perm_network_connection_content)
                    .positiveText(android.R.string.ok)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                            materialDialog.hide();
                            Preferences.getInstance().requestNetworkAccessPermission();
                        }
                    })
                    .show();
        }
    }

    public MaterialDialog getDownloadDialog() {
        return dialog.getBuilder()
                .title(R.string.dialog_download_title)
                .content(R.string.dialog_download_content)
                .progress(true, 0)
                .cancelable(false).build();
    }

    public MaterialDialog getDownloadFinishedDialog(boolean downloadSuccessful, @Nullable String pathToFile) {
        if(downloadSuccessful) {
            return dialog.getBuilder()
                    .title(R.string.dialog_download_finished_title_success)
                    .content(context.getResources().getString(R.string.dialog_download_finished_content_success) + pathToFile)
                    .positiveText(android.R.string.ok)
                    .build();
        } else {
            return dialog.getBuilder()
                    .title(R.string.dialog_download_finished_title_error)
                    .content(R.string.dialog_download_finished_content_error)
                    .positiveText(android.R.string.ok)
                    .build();
        }
    }


    public void showExitDialog(final ExitDialogListener exitDialogListener) {
        MaterialDialog.Builder dialogBuilder = dialog.getBuilder();
        dialogBuilder.icon(new IconicsDrawable(context).icon(CommunityMaterial.Icon.cmd_alert_octagon).sizeDp(42).colorRes(R.color.colorPrimary))
                .title(R.string.dialog_exit_title)
                .content(R.string.dialog_exit_content)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .neutralText(R.string.dialog_exit_neutral)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        if (dialogAction.equals(DialogAction.POSITIVE))
                            exitDialogListener.onExitConfirmed(true);
                        else if (dialogAction.equals(DialogAction.NEGATIVE))
                            exitDialogListener.onExitConfirmed(false);
                        else if (dialogAction.equals(DialogAction.NEUTRAL)) {
                            Preferences.getInstance().setShowExitDialog(false);
                            exitDialogListener.onExitConfirmed(true);
                        }
                    }
                })
                .show();
    }

    public void showNoWifiConnectionDialog(final NoWifiDialogCallback noWifiDialogCallback) {
        MaterialDialog.Builder dialogBuilder = dialog.getBuilder();
        dialogBuilder.icon(new IconicsDrawable(context).icon(CommunityMaterial.Icon.cmd_alert).sizeDp(42).colorRes(R.color.colorPrimary))
                .title(R.string.dialog_no_wifi_title)
                .titleColorRes(R.color.colorPrimary)
                .content(Html.fromHtml(context.getResources().getString(R.string.dialog_no_wifi_content)))
                .positiveText(R.string.dialog_no_wifi_positive)
                .negativeText(R.string.dialog_no_wifi_negative)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        noWifiDialogCallback.onLoadWithoutWifiConfirmed();
                    }
                })
                .show();
    }

    public static class CancelLoadingDialog {

        public interface CancelDialogCallback {
            void onCancelConfirmed();
        }

        private final CancelDialogCallback cancelDialogCallback;
        private MaterialDialog dialog;

        public CancelLoadingDialog(Context context, CancelDialogCallback cancelDialogCallback) {
            this.cancelDialogCallback = cancelDialogCallback;
            this.dialog = new MaterialDialog.Builder(context)
                    .title(R.string.dialog_cancel_loading_title)
                    .titleColorRes(R.color.colorPrimary)
                    .content(R.string.dialog_cancel_loading_content)
                    .positiveText(R.string.dialog_loading_cancel_confirm)
                    .negativeText(R.string.dialog_loading_cancel_abort)
                    .typeface(Preferences.getInstance().getTypeface(), Preferences.getInstance().getTypeface())
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                            CancelLoadingDialog.this.cancelDialogCallback.onCancelConfirmed();
                        }
                    })
                    .build();
        }

        protected MaterialDialog getDialog() {
            return dialog;
        }

        public static void showCancelDialog(Context context) {
            CancelLoadingDialog cancelLoadingDialog = new CancelLoadingDialog(context, (CancelDialogCallback) context);
            cancelLoadingDialog.getDialog().show();
        }

    }

}



