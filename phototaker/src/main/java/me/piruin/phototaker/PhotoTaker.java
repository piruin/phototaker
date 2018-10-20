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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import me.piruin.phototaker.intent.CaptureIntent;
import me.piruin.phototaker.intent.CropIntent;
import me.piruin.phototaker.intent.PickImageIntent;

public class PhotoTaker {
  public static final int CAPTURE_IMAGE = 1030;
  public static final int CROP_IMAGE = 1031;
  public static final int PICK_IMAGE = 1032;
  static final String TAG = "PhotoTaker";

  Action cropAction;
  Action captureAction;
  Action pickAction;

  private String tempFileName = "phototaker-temp.jpg";
  private File captureTempDir;
  private UiComponent ui;
  private PhotoSize photoSize;
  private PhotoTakerListener listener;
  private ContentUriScanner.OnScannedListener mScanner = new ContentUriScanner.OnScannedListener() {

    @Override public void OnScanned(Uri uri) {
      doCropImage(uri);
    }
  };

  public PhotoTaker(@NonNull Activity activity, @NonNull PhotoSize photoSize) {
    this.ui = new ActivityComponent(activity);
    this.photoSize = photoSize;

    init(activity);
  }

  private void init(Context context) {
    captureAction = new CaptureAction(context);
    pickAction = new PickAction(context);
    cropAction = new CropAction(context);

    captureTempDir = context.getExternalCacheDir();
  }

  public PhotoTaker(@NonNull Fragment fragment, @NonNull PhotoSize photoSize) {
    this.ui = new SupportFragmentComponent(fragment);
    this.photoSize = photoSize;

    init(ui.getContext());
  }

  public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (resultCode == Activity.RESULT_CANCELED) {
      onResultCanceled(requestCode);
      return;
    }

    if (resultCode != Activity.RESULT_OK) {
      onResultNotOk(requestCode);
      return;
    }

    if (data == null) {
      onResultNotOk(requestCode);
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

  public boolean doCropImage(@NonNull Uri uri) {
    cropAction.action(uri);
    return true;
  }

  public void showDialog() {
    final CharSequence[] items = {
      "Take from Camera", "Select from Gallery"
    };
    AlertDialog.Builder builder = new AlertDialog.Builder(ui.getContext());
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
    tempFileName = generateTempFileName();
    captureAction.action(null);
  }

  public void pickImage() {
    tempFileName = generateTempFileName();
    pickAction.action(null);
  }

  private String generateTempFileName() {
    return String.format(Locale.getDefault(),
      "%s-%d-temp.jpg",
      ui.getContext().getPackageName().replace(".", ""),
      Calendar.getInstance().getTimeInMillis());
  }

  public void setListener(@NonNull PhotoTakerListener listener) {
    this.listener = listener;
  }

  interface Action {

    void action(Uri data);

    void onResult(Intent data);
  }

  private interface UiComponent {

    void startActivityForResult(Intent intent, int requestCode);

    Context getContext();
  }

  private class CaptureAction implements Action {

    private final Context context;

    CaptureAction(Context context) {
      this.context = context;
    }

    @Override public void action(Uri data) {
      captureTempDir.mkdirs();
      File temp = BitmapUtils.getFile(captureTempDir, tempFileName);

      CaptureIntent captureIntent = new CaptureIntent(context, temp);
      if (captureIntent.resolveActivity(context.getPackageManager()) != null)
        ui.startActivityForResult(captureIntent, CAPTURE_IMAGE);
    }

    @Override public void onResult(Intent data) {
      Logger.log("CAPTURE_IMAGE");
      final File tempFile = BitmapUtils.getFile(captureTempDir, tempFileName);
      Logger.log("tempfile=" + tempFile.getAbsolutePath());

      // Create MediaUriScanner to find your Content URI of File
      new ContentUriScanner(context, mScanner).scan(tempFile.getAbsolutePath());
    }
  }

  private class PickAction implements Action {

    private final Context context;

    PickAction(Context context) {
      this.context = context;
    }

    @Override public void action(Uri data) {
      Logger.log("pickImage() START");

      File output = BitmapUtils.getFile(captureTempDir, tempFileName);

      PickImageIntent intent = new PickImageIntent(context, output);

      if (CropIntent.hasSupportActivity(context)) {
        // Use external Crop Intent if found
        Logger.log("pickImage() Found");
        if (intent.resolveActivity(context.getPackageManager()) != null)
          ui.startActivityForResult(intent, PICK_IMAGE);
      } else {
        // Use Internal Crop method of GET_CONTENT intent
        // This is more Risk method
        intent.enableCrop(photoSize);
        if (intent.resolveActivity(context.getPackageManager()) != null)
          ui.startActivityForResult(intent, CROP_IMAGE);
      }
    }

    @Override public void onResult(Intent data) {
      Logger.log("PICK_IMAGE");
      Uri dataUri = data.getData();

      if (dataUri != null) {
        if (dataUri.getScheme().trim().equalsIgnoreCase("content")) {
          Logger.log("onActivityResult: authority = " + dataUri.getAuthority());
          if (!dataUri.getAuthority().equalsIgnoreCase("media"))
            dataUri = BitmapUtils.getImageUrlWithAuthority(context, dataUri);
          doCropImage(dataUri);
        } else if (dataUri.getScheme().trim().equalsIgnoreCase("file")) {
          // if Scheme URI is File then scan for content then Crop it!
          Logger.log("search for Media Content of path=" + dataUri.getPath());
          new ContentUriScanner(context, mScanner).scan(dataUri.getPath());
        }
      } else {
        Logger.log("DATA IS NULL");
      }
    }
  }

  private class CropAction implements Action {

    private final Context context;

    CropAction(Context context) {
      this.context = context;
    }

    @Override public void action(Uri data) {
      // set CropUri for use in onActivityResult Method.
      Logger.log("Start doCropImage(Uri uri) uri=" + data.toString());
      Logger.log("Start doCropImage uri=%s", data.toString());

      CropIntent intent = new CropIntent(data);
      intent.setOutput(photoSize);

      if (intent.resolveActivity(context.getPackageManager()) != null) {
        ui.startActivityForResult(intent, CROP_IMAGE);
      }
    }

    @Override public void onResult(Intent data) {
      Logger.log("CROP_IMAGE");
      Uri uri = data.getData();
      Bitmap bitmap = data.getParcelableExtra("data");
      if (bitmap == null) {
        if (uri != null) {
          Logger.log("onActivityResult: crop data uri=" + uri.toString());
          bitmap = BitmapUtils.getBitmapFromUri(context, uri);
        }
      } else {
        uri = BitmapUtils.getImageUri(context, bitmap, TAG);
      }
      Intent intent = new Intent();
      intent.setDataAndType(uri, "image/*");
      intent.putExtra("data", bitmap);
      listener.onFinish(intent);
    }
  }

  private class ActivityComponent implements UiComponent {

    final private Activity activity;

    private ActivityComponent(Activity activity) {
      this.activity = activity;
    }

    @Override public void startActivityForResult(Intent intent, int requestCode) {
      activity.startActivityForResult(intent, requestCode);
    }

    @Override public Context getContext() {
      return activity;
    }
  }

  private class FragmentComponent implements UiComponent {

    final private android.app.Fragment fragment;

    private FragmentComponent(android.app.Fragment fragment) {
      this.fragment = fragment;
    }

    @Override public void startActivityForResult(Intent intent, int requestCode) {
      fragment.startActivityForResult(intent, requestCode);
    }

    @Override public Context getContext() {
      return fragment.getActivity();
    }
  }

  private class SupportFragmentComponent implements UiComponent {

    final private Fragment fragment;

    private SupportFragmentComponent(Fragment fragment) {
      this.fragment = fragment;
    }

    @Override public void startActivityForResult(Intent intent, int requestCode) {
      fragment.startActivityForResult(intent, requestCode);
    }

    @Override public Context getContext() {
      return fragment.getContext();
    }
  }
}
