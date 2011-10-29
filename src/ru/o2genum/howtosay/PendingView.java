/*
    Copyright (c) 2011, Andrey Moiseev

    Licensed under the Apache License, Version 2.0 (the "License"); you may
    not use this file except in compliance with the License. You may obtain
    a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package ru.o2genum.howtosay;

/**
 * Progress indicator view
 *
 * @author Andrey Moiseev
 */

import android.content.Context;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.util.AttributeSet;

public class PendingView extends ImageView {
    Context context;

    public PendingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initializeUI();
    }

    public void initializeUI() {
        setImageResource(R.drawable.preloader);
        Animation animation = AnimationUtils.loadAnimation(context,
                R.anim.rotation);
        startAnimation(animation);
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        initializeUI();
    }
}
