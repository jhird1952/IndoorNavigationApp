package com.example.jake1.designproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;

public class NavView extends View {

    private Bitmap mBitmap;
    private int mImageWidth;
    private int mImageHeight;
    private float mScaleFactor = 1.0f;

    private Uri uriFloor1 = Uri.parse("android.resource://com.example.jake1.designproject/drawable/gr2");
    private Uri uriFloor2 = Uri.parse("android.resource://com.example.jake1.designproject/drawable/gr3");
    private Uri uriFloor3 = Uri.parse("android.resource://com.example.jake1.designproject/drawable/gr4");

    public NavView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBitmap != null) {
            canvas.save();
            canvas.scale(mScaleFactor, mScaleFactor);
            canvas.drawBitmap(mBitmap, 0, 0, null);
            canvas.restore();
        }

    }

    public void loadMap() {

        Uri uriMap = uriFloor1;

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uriMap);
        }
        catch (IOException e) {
            e.printStackTrace();
        }


        mBitmap = bitmap.createScaledBitmap(bitmap, mImageWidth, mImageHeight, false);
        invalidate();

    }

    public void setImageWidth(int clNavMapWidth) {

        mImageWidth = clNavMapWidth;

    }

    public void setImageHeight(int clNavMapHeight) {

        mImageHeight = clNavMapHeight;

    }

}
