package me.piruin.phototaker;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;

import java.io.ByteArrayOutputStream;

public final class MediaUriUtils {

    public static Uri getImageUri(Context context, Bitmap image, String title) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = Media.insertImage(context.getContentResolver(), image, title, null);
        return Uri.parse(path);
    }
}
