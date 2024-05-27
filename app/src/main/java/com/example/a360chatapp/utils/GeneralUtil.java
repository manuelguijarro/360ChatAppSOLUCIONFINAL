package com.example.a360chatapp.utils;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;

public class GeneralUtil {

    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }
    public static void setImagenPerfil(Context context, Uri imagenUri, ImageView imageView){
        Glide.with(context).load(imagenUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}
