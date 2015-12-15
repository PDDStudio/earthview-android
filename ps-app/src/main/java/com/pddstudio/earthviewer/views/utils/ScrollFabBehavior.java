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

package com.pddstudio.earthviewer.views.utils;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import com.github.clans.fab.FloatingActionMenu;
import com.pddstudio.earthviewer.R;

/**
 * This Class was created by Patrick J
 * on 10.10.15. For more Details and Licensing
 * have a look at the README.md
 */
public class ScrollFabBehavior extends CoordinatorLayout.Behavior<FloatingActionMenu> {

    public ScrollFabBehavior(Context context, AttributeSet attributeSet) {}

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionMenu child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionMenu child, View dependency) {
        Toolbar toolbar = (Toolbar) parent.findViewById(R.id.toolbar_details_view);
        int tHeight = 0;
        int depHeight = dependency.getHeight();
        int depMeasHeight = dependency.getMeasuredHeight();
        int depMinHeight = dependency.getMinimumHeight();


        tHeight = toolbar.getHeight();

        /*Log.i("BEHAVIOR", "tHeight: " + tHeight
                + " depHeight: " + depHeight
                + " depMeasured: " + depMeasHeight
                + " depMinHeight: " + depMinHeight);

        Log.i("FAB", "transY: " + child.getTranslationY() +  " y: " + child.getY() + " x: " + child.getX());*/

        int hideCorn = tHeight + ((tHeight/100) * 95);
        if(child.getY() < hideCorn) {
            if(!child.isMenuButtonHidden()) child.hideMenuButton(true);
        } else if (child.getY() > hideCorn) {
            if(child.isMenuButtonHidden()) child.showMenuButton(true);
        }

        /*AppBarLayout dep = (AppBarLayout) dependency;
        if(dep.getHeight() < (tHeight + 50)) {
            child.hide(true);
        } else {
            child.show(true);
        }*/

        return true;
    }

    @Override
    public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout,
                                       final FloatingActionMenu child,
                                       final View directTargetChild,
                                       final View target,
                                       final int nestedScrollAxes) {
        // Ensure we react to vertical scrolling
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionMenu child,
                               View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed,
                dyUnconsumed);

        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
            child.hideMenuButton(true);
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            child.showMenuButton(true);
        }
    }
}
