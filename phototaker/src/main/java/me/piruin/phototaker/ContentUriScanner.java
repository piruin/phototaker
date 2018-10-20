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

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

class ContentUriScanner implements MediaScannerConnectionClient {

  private Handler handler = new Handler(Looper.getMainLooper());
  private MediaScannerConnection mediaScannerConnection = null;
  private String path;
  OnScannedListener listener;

  public ContentUriScanner(Context context, OnScannedListener listener)
  {
    mediaScannerConnection = new MediaScannerConnection(context, this);
    this.listener = listener;
  }

  public void scan(String path) {
    this.path = path;
    mediaScannerConnection.connect();
  }

  @Override public void onMediaScannerConnected() {
    mediaScannerConnection.scanFile(path, "image/*");
  }

  @Override public void onScanCompleted(final String path, final Uri uri) {
    handler.post(new Runnable() {
      @Override public void run() {
        listener.OnScanned(uri);
      }
    });
    mediaScannerConnection.disconnect();
  }

  interface OnScannedListener {
    void OnScanned(Uri uri);
  }
}
