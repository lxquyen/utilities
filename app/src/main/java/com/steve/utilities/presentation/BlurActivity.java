package com.steve.utilities.presentation;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.steve.utilities.R;
import com.steve.utilities.common.widget.BlurBuilder;

public class BlurActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bur);


        transparentStatusAndNavigation();
        blurWindowBackground();
    }

    private void blurWindowBackground() {
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final BitmapDrawable wallpaperDrawable = (BitmapDrawable) wallpaperManager.getDrawable();
        Bitmap wallpaperBitmap = wallpaperDrawable.getBitmap();
        Bitmap blurredBitmap = BlurBuilder.blur(this, wallpaperBitmap);
        BitmapDrawable newWallpaper = new BitmapDrawable(getResources(), blurredBitmap);
        Window w = getWindow();
        w.setBackgroundDrawable(newWallpaper);
    }

    private void transparentStatusAndNavigation() {
        //make full transparent statusBar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );
        setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
    }

    private void setWindowFlag(final int bits, boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}