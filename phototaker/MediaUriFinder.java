package com.blayzupe.phototaker;

import android.app.Activity;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.util.Log;

public class MediaUriFinder implements MediaScannerConnectionClient {

    private android.media.MediaScannerConnection msc = null;
    private String mFilePath;
    private MediaScannedListener mListener;

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
    public void onScanCompleted(String path, Uri uri) {
	// where get content uri
	Log.d("MediaUriScanner",
		"blayzupe Path : " + path + ",URI : " + uri.toString());
	mListener.OnScanned(uri);
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
