package com.example.androidfinalproject.Utilities;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.androidfinalproject.R;

import jp.wasabeef.glide.transformations.BlurTransformation;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;

public class ImageUtils {
    public static String barberBackground = "https://images.unsplash.com/photo-1536520002442-39764a41e987?q=80&w=3774&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D";
    public static String barberBackground2 = "https://img.freepik.com/free-photo/vintage-barber-paper-jobs-career-concept_53876-127077.jpg?w=2000&t=st=1713455548~exp=1713456148~hmac=ddc4b0881dd216c684d5148caae2f1ef9bde8fd663115c0cc04dffcc55371f9a";
    public static void loadImage(AppCompatImageView view, String url)
    {
        Glide.with(view.getContext())
                .load(url)
                .into(view);
    }
    public static void loadImageWithBlur(AppCompatImageView view, String url, int raduis)
    {
        Glide.with(view.getContext())
                .load(url)
                .apply(new RequestOptions().transform(new BlurTransformation(raduis))) // 25 is the blur radius
                .into(view);
    }
    public static void setStatusBarColor(Activity activity, int colorResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(activity, colorResId));
        }
    }
}
