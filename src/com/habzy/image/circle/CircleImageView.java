/**
 *
 * Copyright habzyhs@gmail.com
 *
 * Take reference from [RoundedImageView](https://github.com/habzy/RoundedImageView)
 */
package com.habzy.image.circle;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class CircleImageView extends ImageView {

    private static final String TAG = "CircleImageView";

    private int mBorderWidth = 0;
    private ColorStateList mBorderColor = ColorStateList
            .valueOf(CircleDrawable.DEFAULT_BORDER_COLOR);

    private Drawable mDrawable;


    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a =
                context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyle, 0);

        mBorderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_width_border, -1);
        if (mBorderWidth < 0) {
            mBorderWidth = 0;
        }

        mBorderColor = a.getColorStateList(R.styleable.CircleImageView_color_border);
        if (mBorderColor == null) {
            mBorderColor = ColorStateList.valueOf(CircleDrawable.DEFAULT_BORDER_COLOR);
        }

        updateDrawableAttrs();
        a.recycle();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        mDrawable = CircleDrawable.fromDrawable(drawable);
        updateDrawableAttrs();
        super.setImageDrawable(mDrawable);
    }

    private void updateDrawableAttrs() {
        updateAttrs(mDrawable, false);
    }

    private void updateAttrs(Drawable drawable, boolean background) {
        if (drawable == null) {
            Log.w(TAG, "No content to be drawn");
            return;
        }

        if (drawable instanceof CircleDrawable) {
            ((CircleDrawable) drawable).setBorderWidth(mBorderWidth).setBorderColors(mBorderColor);
        } else if (drawable instanceof LayerDrawable) {
            // loop through layers to and set drawable attrs
            LayerDrawable ld = ((LayerDrawable) drawable);
            int layers = ld.getNumberOfLayers();
            for (int i = 0; i < layers; i++) {
                updateAttrs(ld.getDrawable(i), background);
            }
        }
    }
}
