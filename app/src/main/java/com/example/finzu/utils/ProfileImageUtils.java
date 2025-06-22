package com.example.finzu.utils;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.example.finzu.R;
import com.squareup.picasso.Picasso;

public class ProfileImageUtils {

    public static void loadProfilePicture(Context context, String imageUrl, ImageView imageView) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            Picasso.get()
                    .load(R.drawable.ic_default_profile)
                    .into(imageView);
        } else {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_default_profile)
                    .error(R.drawable.ic_default_profile)
                    .into(imageView);
        }
    }

    public static void loadFromUri(Context context, Uri uri, ImageView imageView) {
        if (uri != null) {
            Picasso.get()
                    .load(uri)
                    .placeholder(R.drawable.ic_default_profile)
                    .error(R.drawable.ic_default_profile)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_default_profile);
        }
    }

    public static void resetToDefault(ImageView imageView) {
        imageView.setImageResource(R.drawable.ic_default_profile);
    }
}
