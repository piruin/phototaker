/*
 * Copyright (c) 2016 Piruin Panichphol
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.piruin.phototaker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import java.io.File;
import me.piruin.phototaker.intent.CaptureIntent;
import me.piruin.phototaker.intent.CropIntent;
import me.piruin.phototaker.intent.PickImageIntent;

public class PhotoTaker {
  public static final int CAPTURE_IMAGE = 1030;
  public static final int CROP_IMAGE = 1031;
  public static final int PICK_IMAGE = 1032;
  public static final String TAG = "PhotoTaker";

  Action cropAction = new CropAction();
  Action captureAction = new CaptureAction();
  Action pickAction = new PickAction();

  private String tempFileName = "phototaker-temp.jpg";
  private File captureTempDir;
  private Activity activity;
  private PhotoSize photoSize;
  private PhotoTakerListener listener;
  private ContentUriScanner.OnScannedListener mScanner = new ContentUriScanner.OnScannedListener() {

    @Override public void OnScanned(Uri uri) {
      doCropImage(uri);
    }
  };

  public PhotoTaker(Activity activity, PhotoSize photoSize) {
    this.activity = activity;
    this.photoSize = photoSize;

    captureTempDir = activity.getExternalCacheDir();
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == Activity.RESULT_CANCELED) {
      onResultCanceled(requestCode);
      return;
    }

    if (resultCode != Activity.RESULT_OK) {
      onResultNotOk(requestCode);
      return;
    }

    switch (requestCode) {
      case CAPTURE_IMAGE:
        captureAction.onResult(data);
        break;
      case PICK_IMAGE:
        pickAction.onResult(data);
        break;
      case CROP_IMAGE: {
        cropAction.onResult(data);
        break;
      }
    }
  }

  private void onResultCanceled(int requestCode) {
    switch (requestCode) {
      case CAPTURE_IMAGE:
      case PICK_IMAGE:
      case CROP_IMAGE:
        listener.onCancel(requestCode);
    }
  }

  private void onResultNotOk(int requestCode) {
    switch (requestCode) {
      case CAPTURE_IMAGE:
      case PICK_IMAGE:
      case CROP_IMAGE:
        listener.onError(requestCode);
    }
  }

  public boolean doCropImage(Uri uri) {
    cropAction.action(uri);
    return true;
  }

  public void showDialog() {
    final CharSequence[] items = {
      "Take from Camera", "Select from Gallery"
    };
    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    builder.setItems(items, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int item) {
        switch (item) {
          case 0:
            captureImage();
            break; // Take from Camera
          case 1:
            pickImage();
            break; // From gallery
        }
      }
    });
    AlertDialog alert = builder.create();
    alert.show();
  }

  public void captureImage() {
    captureAction.action(null);
  }

  public void pickImage() {
    pickAction.action(null);
  }

  public void setListener(PhotoTakerListener listener) {
    this.listener = listener;
  }

  interface Action {

    void action(@Nullable Uri data);

    void onResult(Intent data);
  }

  private class CaptureAction implements Action {

    @Override public void action(Uri data) {
      captureTempDir.mkdirs();
      File temp = BitmapUtils.getFile(captureTempDir, tempFileName);

      CaptureIntent captureIntent = new CaptureIntent(temp);
      if (captureIntent.resolveActivity(activity.getPackageManager()) != null)
        activity.startActivityForResult(captureIntent, CAPTURE_IMAGE);
    }

    @Override public void onResult(Intent data) {
      Logger.log("blayzupe CAPTURE_IMAGE");
      final File tempFile = BitmapUtils.getFile(captureTempDir, tempFileName);
      Logger.log("blayzupe tempfile="+tempFile.getAbsolutePath());

      // Create MediaUriScanner to find your Content URI of File
      new ContentUriScanner(activity, mScanner).scan(tempFile.getAbsolutePath());
    }
  }

  private class PickAction implements Action {

    @Override public void action(@Nullable Uri data) {
      Logger.log("blayzupe pickImage() START");

      PickImageIntent intent = new PickImageIntent();

      if (CropIntent.hasSupportActivity(activity)) {
        // Use external Crop Intent if found
        Logger.log("blayzupe pickImage() Found");
        if (intent.resolveActivity(activity.getPackageManager()) != null)
          activity.startActivityForResult(intent, PICK_IMAGE);
      } else {
        // Use Internal Crop method of GET_CONTENT intent
        // This is more Risk method
        intent.enableCrop(photoSize);
        if (intent.resolveActivity(activity.getPackageManager()) != null)
          activity.startActivityForResult(intent, CROP_IMAGE);
      }
    }

    @Override public void onResult(Intent data) {
      Logger.log("blayzupe PICK_IMAGE");
      Uri dataUri = data.getData();

      if (dataUri != null) {
        if (dataUri.getScheme().trim().equalsIgnoreCase("content")) {
          Logger.log("onActivityResult: authority = "+dataUri.getAuthority());
          if (!dataUri.getAuthority().equalsIgnoreCase("media"))
            dataUri = BitmapUtils.getImageUrlWithAuthority(activity, dataUri);
          doCropImage(dataUri);
        } else if (dataUri.getScheme().trim().equalsIgnoreCase("file")) {
          // if Scheme URI is File then scan for content then Crop it!
          Logger.log("blayzupe search for Media Content of path="+dataUri.getPath());
          new ContentUriScanner(activity, mScanner).scan(dataUri.getPath());
        }
      } else {
        Logger.log("blayzupe DATA IS NULL");
      }
    }
  }

  private class CropAction implements Action {

    @Override public void action(Uri data) {
      // set CropUri for use in onActivityResult Method.
      Logger.log("blayzupe Start doCropImage(Uri uri) uri="+data.toString());
      Logger.log("Start doCropImage uri=%s", data.toString());

      CropIntent intent = new CropIntent(data);
      intent.setOutput(photoSize);

      if (intent.resolveActivity(activity.getPackageManager()) != null) {
        activity.startActivityForResult(intent, CROP_IMAGE);
      }
    }

    @Override public void onResult(Intent data) {
      Logger.log("blayzupe CROP_IMAGE");
      Uri uri = data.getData();
      Bitmap bitmap = data.getParcelableExtra("data");
      if (bitmap == null) {
        if (uri != null) {
          Logger.log("onActivityResult: crop data uri="+uri.toString());
          bitmap = BitmapUtils.getBitmapFromUri(activity, uri);
        }
      } else {
        uri = BitmapUtils.getImageUri(activity, bitmap, TAG);
      }
      Intent intent = new Intent();
      intent.setDataAndType(uri, "image/*");
      intent.putExtra("data", bitmap);
      listener.onFinish(intent);
    }
  }
}
