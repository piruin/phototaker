/*
 * Copyright 2016 Piruin Panichphol
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.piruin.phototaker;

import android.app.Activity;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.io.File;

public class MediaUriFinder implements MediaScannerConnectionClient {

  Handler handler = new Handler(Looper.getMainLooper());
  private android.media.MediaScannerConnection msc = null;
  private String mFilePath;
  private MediaScannedListener mListener;

  public MediaUriFinder(
    Activity activity, String filePath, MediaScannedListener listener)
  {
    msc = new android.media.MediaScannerConnection(activity.getApplicationContext(), this);
    msc.connect();
    mFilePath = filePath;
    mListener = listener;
  }

  public static MediaUriFinder create(
    Activity activity, String filePath, MediaScannedListener listener)
  {
    return new MediaUriFinder(activity, filePath, listener);
  }

  @Override public void onMediaScannerConnected() {
    // Scan for temp file
    msc.scanFile(mFilePath, "image/*");
  }

  @Override public void onScanCompleted(final String path, final Uri uri) {
    // where get content uri
    Log.d("MediaUriScanner", "got Content URI of Path : "+path+",URI : "+uri.toString());
    handler.post(new Runnable() {
      @Override public void run() {
        Uri fileUri = Uri.fromFile(new File(path));
        mListener.OnScanned(fileUri);
      }
    });
    msc.disconnect();
  }

  public static interface MediaScannedListener {
    boolean OnScanned(Uri uri);
  }
}
