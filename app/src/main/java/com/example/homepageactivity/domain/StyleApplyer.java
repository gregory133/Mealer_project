package com.example.homepageactivity.domain;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import com.example.homepageactivity.R;

public class StyleApplyer {

    /**
     * applies the theme described by wrapper to the view
     * @param wrapper
     * @param resID
     */
    public static Drawable applyTheme(Context context, ContextWrapper wrapper, int resID){
        return ResourcesCompat.getDrawable(context.getResources(), resID, wrapper.getTheme());

    }
}
