package me.piruin.phototaker;

import android.app.Activity;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;

public class MediaUriFinder implements MediaScannerConnectionClient {

    private android.media.MediaScannerConnection msc = null;
    private String mFilePath;
    private MediaScannedListener mListener;

    Handler handler = new Handler(Looper.getMainLooper());

    public MediaUriFinder(Activity activity, String filePath,
                          MediaScannedListener listener) {
        msc = new android.media.MediaScannerConnection(
            activity.getApplicationContext(), this);
        msc.connect();
        mFilePath = filePath;
        mListener = listener;
    }

    @Override
    public void onMediaScannerConnected() {
        // Scan for temp file
        msc.scanFile(mFilePath, "image/*");
    }

    @Override
    public void onScanCompleted(final String path, final Uri uri) {
        // where get content uri
        Log.d("MediaUriScanner",
            "got Content URI of Path : " + path + ",URI : " + uri.toString());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Uri fileUri = Uri.fromFile(new File(path));
                mListener.OnScanned(fileUri);
            }
        });
        msc.disconnect();
    }

    public static MediaUriFinder create(Activity activity, String filePath,
                                        MediaScannedListener listener) {
        return new MediaUriFinder(activity, filePath, listener);
    }

    public static interface MediaScannedListener {
        boolean OnScanned(Uri uri);
    }

}
