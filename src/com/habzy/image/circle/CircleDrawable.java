package com.habzy.image.circle;



import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;

/**
 * @author Habzy Huang
 * 
 */
public class CircleDrawable extends Drawable {

    private static final String TAG = CircleDrawable.class.getName();

    private final RectF mBitmapRect = new RectF();
    private final BitmapShader mBitmapShader;
    private final Paint mBitmapPaint;
    private final int mBitmapWidth;
    private final int mBitmapHeight;
    private float mRadius;
    private final Paint mBorderPaint;
    private final Matrix mShaderMatrix = new Matrix();

    private float mBorderWidth = 0;
    private ColorStateList mBorderColor = ColorStateList.valueOf(DEFAULT_BORDER_COLOR);

    public static final int DEFAULT_BORDER_COLOR = Color.BLACK;

    public CircleDrawable(Bitmap bitmap) {

        mBitmapWidth = bitmap.getWidth();
        mBitmapHeight = bitmap.getHeight();
        mBitmapRect.set(0, 0, mBitmapWidth, mBitmapHeight);

        mRadius = mBitmapWidth < mBitmapHeight ? mBitmapWidth / 2 : mBitmapHeight / 2;

        mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapShader.setLocalMatrix(mShaderMatrix);

        mBitmapPaint = new Paint();
        mBitmapPaint.setStyle(Paint.Style.FILL);
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        mBorderPaint = new Paint();
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor.getColorForState(getState(), DEFAULT_BORDER_COLOR));
        mBorderPaint.setStrokeWidth(mBorderWidth);
    }

    /*
     * (non-Javadoc)
     * @see android.graphics.drawable.Drawable#getOpacity()
     */
    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    /*
     * (non-Javadoc)
     * @see android.graphics.drawable.Drawable#setAlpha(int)
     */
    @Override
    public void setAlpha(int alpha) {
        mBitmapPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mBitmapPaint.setColorFilter(cf);
        invalidateSelf();

    }

    @Override
    public void setDither(boolean dither) {
        mBitmapPaint.setDither(dither);
        invalidateSelf();
    }

    @Override
    public void setFilterBitmap(boolean filter) {
        mBitmapPaint.setFilterBitmap(filter);
        invalidateSelf();
    }

    @Override
    public int getIntrinsicWidth() {
        return mBitmapWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mBitmapHeight;
    }

    public static Drawable fromDrawable(Drawable drawable) {
        if (drawable != null) {
            if (drawable instanceof CircleDrawable) {
                // just return if it's already a RoundedDrawable
                return drawable;
            } else if (drawable instanceof LayerDrawable) {
                LayerDrawable ld = (LayerDrawable) drawable;
                int num = ld.getNumberOfLayers();

                // loop through layers to and change to RoundedDrawables if possible
                for (int i = 0; i < num; i++) {
                    Drawable d = ld.getDrawable(i);
                    ld.setDrawableByLayerId(ld.getId(i), fromDrawable(d));
                }
                return ld;
            }

            // try to get a bitmap from the drawable and
            Bitmap bm = drawableToBitmap(drawable);
            if (bm != null) {
                return new CircleDrawable(bm);
            } else {
                Log.w(TAG, "Failed to create bitmap from drawable!");
            }
        }
        return drawable;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap;
        int width = Math.max(drawable.getIntrinsicWidth(), 1);
        int height = Math.max(drawable.getIntrinsicHeight(), 1);
        try {
            bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        } catch (Exception e) {
            e.printStackTrace();
            bitmap = null;
        }

        return bitmap;
    }

    public CircleDrawable setBorderWidth(int width) {
        mBorderWidth = width;
        mBorderPaint.setStrokeWidth(mBorderWidth);
        return this;
    }

    public int getBorderColor() {
        return mBorderColor.getDefaultColor();
    }

    public CircleDrawable setBorderColor(int color) {
        return setBorderColors(ColorStateList.valueOf(color));
    }

    public CircleDrawable setBorderColors(ColorStateList colors) {
        mBorderColor = colors != null ? colors : ColorStateList.valueOf(0);
        mBorderPaint.setColor(mBorderColor.getColorForState(getState(), DEFAULT_BORDER_COLOR));
        return this;
    }

    @Override
    public void draw(Canvas canvas) {
        Log.d(TAG, "=========");
        Log.d(TAG, "======mBorderWidth:"+mBorderWidth+"   mRadius:"+mRadius);
        if (mBorderWidth > 0) {
            mRadius = mRadius - mBorderWidth;
            canvas.drawCircle(mBitmapWidth / 2, mBitmapHeight / 2, mRadius, mBorderPaint);
            canvas.drawCircle(mBitmapWidth / 2, mBitmapHeight / 2, mRadius, mBitmapPaint);
        } else {
            canvas.drawCircle(mBitmapWidth / 2, mBitmapHeight / 2, mRadius, mBitmapPaint);
        }
    }

}
