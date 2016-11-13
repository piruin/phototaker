package me.piruin.phototaker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

public class ImageResizer {

    public static final String TAG = "ImageResizer";
    protected int outputX = 200;
    protected int outputY = 200;

    protected boolean doResizeImage(File input, File output) {
        if (input.exists()) {
            try {
                Log.d(TAG,
                    "blayzupe doResizeImage() start input="
                        + input.getAbsolutePath() + " output="
                        + output.getAbsolutePath());
                Bitmap bitmap = BitmapFactory.decodeFile(input
                    .getAbsolutePath());
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int newWidth = outputX;
                // calculate the scale
                float scaleWidth = ((float) newWidth) / width;
                float scaleHeight = scaleWidth;
                // createa matrix for the manipulation
                Matrix matrix = new Matrix();
                // resize the bit map
                matrix.postScale(scaleWidth, scaleHeight);
                Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
                    height, matrix, true);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                // Create Image File from baos
                intoJPEGfile(baos.toByteArray(), output);
                input.delete();
                return true;
            } catch (Exception ex) {
                Log.e(TAG, "Delete temp file cause by can't ResizeImage");
                return false;
            }
        } else
            return false;
    }

    private void intoJPEGfile(byte[] imageData, File output) {
        try {
            FileOutputStream buf;
            buf = new FileOutputStream(output);
            buf.write(imageData);
            buf.flush();
            buf.close();
        } catch (Exception e) {
            Log.v(TAG, "error while write picture to file");
            e.printStackTrace();
        }
    }

}
