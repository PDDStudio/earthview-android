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

package com.pddstudio.earthviewer.views.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.aboutlibraries.ui.LibsFragment;

/**
 * This Class was created by Patrick J
 * on 13.12.15. For more Details and Licensing
 * have a look at the README.md
 */
public class AboutFragment extends LibsFragment {

    private AboutFragmentCompat libsFragmentCompat = new AboutFragmentCompat();

    public AboutFragment() {
        super();
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return this.libsFragmentCompat.onCreateView(container.getContext(), inflater, container, savedInstanceState, this.getArguments());
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.libsFragmentCompat.onViewCreated(view, savedInstanceState);
    }

    public void onDestroyView() {
        this.libsFragmentCompat.onDestroyView();
        super.onDestroyView();
    }

}
