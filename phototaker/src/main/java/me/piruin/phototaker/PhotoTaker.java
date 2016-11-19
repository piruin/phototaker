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
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import java.io.File;
import java.util.List;

public class PhotoTaker {
  public static final int IMAGE_CAPTURE = 30;
  public static final int CROP_IMAGE = 31;
  public static final int PICK_FROM_FILE = 32;
  public static final String TAG = "PhotoTaker";

  public int outputX = 240;
  public int outputY = 240;
  public int aspectX = 1;
  public int aspectY = 1;
  public boolean return_data = true;
  public boolean scale = true;
  public boolean faceDetection = true;

  protected String mOutput;
  protected String mTemp;
  protected File mDirectory;
  private Activity mActivity;
  private Uri mCropUri;
  private PhotoSize photoSize;
  private PhotoTakerListener listener;
  private ContentUriScanner.OnScannedListener mScanner = new ContentUriScanner.OnScannedListener() {

    @Override public void OnScanned(Uri uri) {
      doCropImage(uri);
    }
  };

  private Action cropAction = new CropAction();
  private Action captureAction = new CaptureAction();
  private Action pickAction = new PickAction();

  public PhotoTaker(Activity activity, PhotoSize photoSize) {
    mActivity = activity;
    this.photoSize = photoSize;

    //TODO google's Photos app can't edit capture image in private directory, Must find a way out.
    mDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    mDirectory.mkdirs();
    mTemp = "phototaker-temp.jpg";
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
      case IMAGE_CAPTURE:
        captureAction.onResult(data);
        break;
      case PICK_FROM_FILE:
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
      case IMAGE_CAPTURE:
      case PICK_FROM_FILE:
      case CROP_IMAGE:
        listener.onCancel(requestCode);
    }
  }

  private void onResultNotOk(int requestCode) {
    switch (requestCode) {
      case IMAGE_CAPTURE:
      case PICK_FROM_FILE:
      case CROP_IMAGE:
        listener.onError(requestCode);
    }
  }

  public boolean doCropImage(Uri uri) {
    cropAction.action(uri);
    return true;
  }

  public void doShowDialog() {
    final CharSequence[] items = {
      "Take from Camera", "Select from Gallery"
    };
    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
    builder.setItems(items, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int item) {
        switch (item) {
          case 0:
            doImageCapture();
            break; // Take from Camera
          case 1:
            doPickImage();
            break; // From gallery
        }
      }
    });
    AlertDialog alert = builder.create();
    alert.show();
  }

  public boolean doImageCapture() {
    captureAction.action(null);
    return true;
  }

  public boolean doPickImage() {
    pickAction.action(null);
    return true;
  }

  public void setListener(PhotoTakerListener listener) {
    this.listener = listener;
  }

  private interface Action {
    void onResult(Intent data);

    void action(@Nullable Uri data);
  }

  private class CaptureAction implements Action {

    @Override public void onResult(Intent data) {
      Log.e(TAG, "blayzupe IMAGE_CAPTURE");
      final File tempFile = FileUtils.getFile(mDirectory, mTemp);
      Log.d(TAG, "blayzupe tempfile="+tempFile.getAbsolutePath());

      // Create MediaUriScanner to find your Content URI of File
      new ContentUriScanner(mActivity, mScanner).scan(tempFile.getAbsolutePath());
    }

    @Override public void action(Uri data) {
      try {
        File temp = FileUtils.getFile(mDirectory, mTemp);

        // Take Image for Camera and write to tempfile
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("outputFormat", CompressFormat.JPEG.toString());
        intent.putExtra("return-data", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(temp));
        mActivity.startActivityForResult(intent, IMAGE_CAPTURE);
      } catch (ActivityNotFoundException anfe) {
        anfe.printStackTrace();
      }
    }
  }

  private class CropAction implements Action {

    @Override public void onResult(Intent data) {
      Log.e(TAG, "blayzupe CROP_IMAGE");
      FileUtils.getFile(mDirectory, mTemp).delete();

      Bundle extras = data.getExtras();
      //File output = FileUtils.getFile(mDirectory, mOutput);
      if (extras != null) {
        // get data to Bitmap then write to file
        Bitmap croppedImg = extras.getParcelable("data");
        mCropUri = BitmapUtils.getImageUri(mActivity, croppedImg, mOutput);

        //listener.onFinish(output.getAbsolutePath(), mCropUri);

        Intent intent = new Intent();
        intent.setDataAndType(mCropUri, "image/*");
        intent.putExtra("data", croppedImg);
        listener.onFinish(intent);
      }
      Uri crop = data.getData();
      if (crop != null) {
        Log.d(TAG, "onActivityResult: crop data uri="+crop.toString());
        Bitmap image = BitmapUtils.getBitmapFromUri(mActivity, crop);
        //listener.onFinish(output.getAbsolutePath(), crop);

        Intent intent = new Intent();
        intent.setDataAndType(mCropUri, "image/*");
        intent.putExtra("data", image);
        listener.onFinish(intent);
      }
    }

    @Override public void action(Uri data) {
      try {
        // set CropUri for use in onActivityResult Method.
        Log.w(TAG, "blayzupe Start doCropImage(Uri uri) "+"uri="+data.toString());

        CropIntent intent = new CropIntent(data);
        intent.setOutput(photoSize);

        List<ResolveInfo> list = mActivity.getPackageManager().queryIntentActivities(intent, 0);
        if (list.size() > 0) {
          Log.w(TAG, "blayzupe Found Crop Intent");
          mActivity.startActivityForResult(intent, CROP_IMAGE);
        } else {
          Log.e(TAG, "blayzupe doCropImage(Uri)"+" Not Found Support Crop Activity");
          Log.e(TAG, "URI : "+data.toString());
          FileUtils.getFile(mDirectory, mTemp).delete();
        }
      } catch (ActivityNotFoundException anfe) {
        Log.e(TAG, "blayzupe doCropImage(Uri) "+"Not Found Support Crop Activity", anfe);
        anfe.printStackTrace();
      }
    }
  }

  private class PickAction implements Action {

    @Override public void onResult(Intent data) {
      Log.e(TAG, "blayzupe PICK_IMAGE");
      Uri dataUri = data.getData();

      if (dataUri != null) {
        if (dataUri.getScheme().trim().equalsIgnoreCase("content")) {
          Log.d(TAG, "onActivityResult: authority = "+dataUri.getAuthority());
          if (!dataUri.getAuthority().equalsIgnoreCase("media"))
            dataUri = BitmapUtils.getImageUrlWithAuthority(mActivity, dataUri);
          doCropImage(dataUri);
        }

        // if Scheme URI is File then scan for content then Crop it!
        else if (dataUri.getScheme().trim().equalsIgnoreCase("file")) {
          Log.d(TAG, "blayzupe search for Media Content of path="+dataUri.getPath());
          new ContentUriScanner(mActivity, mScanner).scan(dataUri.getPath());
        }
      } else {
        Log.e(TAG, "blayzupe DATA IS NULL");
      }
    }

    @Override public void action(@Nullable Uri data) {
      try {
        Log.d(TAG, "blayzupe doPickImage() START");

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        if (CropIntent.hasSupportActivity(mActivity)) {
          // Use external Crop Intent if found
          Log.d(TAG, "blayzupe doPickImage() Found");
          intent.putExtra("return-data", return_data);

          mActivity.startActivityForResult(intent, PICK_FROM_FILE);
        } else {
          // Use Internal Crop method of GET_CONTENT intent
          // This is more Risk method
          Log.d(TAG, "blayzupe doPickImage() Not found crop activity");
          intent.putExtra("crop", "true");
          intent.putExtra("noFaceDetection", !faceDetection);
          intent.putExtra("aspectX", aspectX);
          intent.putExtra("aspectY", aspectY);
          intent.putExtra("outputX", outputX);
          intent.putExtra("outputY", outputY);
          intent.putExtra("scale", scale);
          intent.putExtra("return-data", return_data);

          mActivity.startActivityForResult(intent, CROP_IMAGE);
        }
      } catch (ActivityNotFoundException anfe) {
        anfe.printStackTrace();
      }
    }
  }
}
