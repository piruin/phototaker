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
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class PhotoTaker {

  public static final String ACTION_CROP_IMAGE = "com.android.camera.action.CROP";
  public static final String TEMP_PREFIX = "tmp_";
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
  private OnCropFinishListener mOnFinishListener;
  private OnNotFoundCropIntentListener mNotFoundCropIntentListener;
  private Uri mCropUri;
  private ContentUriScanner.OnScannedListener mScanner = new ContentUriScanner.OnScannedListener() {

    @Override public void OnScanned(Uri uri) {
      /*
       * Start Crop Activity with URI that we get once scanned if not
	     * found Support Crop Activity then run OnNotFoundCropIntent()
	     */
      if (!doCropImage(uri) && mNotFoundCropIntentListener != null)
        mNotFoundCropIntentListener.OnNotFoundCropIntent(mDirectory.getAbsolutePath(), mCropUri);
    }
  };

  public PhotoTaker(
    Activity activity, String path, String name, OnCropFinishListener listener)
  {
    this(activity, path, name);
    mOnFinishListener = listener;
  }

  // Base Constructor
  public PhotoTaker(Activity activity, String path, String name) {
    mActivity = activity;
    setOutput(path, name);
  }

  public void setOutput(String path, String name) {
    mDirectory = createDirectory(path);
    mOutput = name;
    mTemp = TEMP_PREFIX.concat(name);
  }

  private File createDirectory(String path) {
    File directory = new File(path);
    if (!directory.exists()) {
      directory.mkdirs();
    }
    return directory;
  }

  public PhotoTaker(Activity activity, OnCropFinishListener listener) {
    this(activity);
    mOnFinishListener = listener;
  }

  public PhotoTaker(Activity activity) {
    this(activity, "/sdcard/", "PhotoTaker.jpg");
  }

  public void setOutput(String name) {
    mOutput = name;
    mTemp = TEMP_PREFIX.concat(name);
  }

  public void setCropfinishListener(OnCropFinishListener listener) {
    mOnFinishListener = listener;
  }

  public void setNotFoundCropIntentListener(OnNotFoundCropIntentListener listener) {
    mNotFoundCropIntentListener = listener;
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode != Activity.RESULT_OK) {
      Log.e(TAG, "blayzupe Result Not OK!!! "+Activity.RESULT_FIRST_USER);
      return;
    }

    switch (requestCode) {
      case IMAGE_CAPTURE:
        Log.e(TAG, "blayzupe IMAGE_CAPTURE");
        final File tempFile = getFile(mDirectory, mTemp);
        Log.d(TAG, "blayzupe tempfile="+tempFile.getAbsolutePath());

        // Create MediaUriScanner to find your Content URI of File
        new ContentUriScanner(mActivity, mScanner).scan(tempFile.getAbsolutePath());
        break;
      case PICK_FROM_FILE:
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
        break;
      case CROP_IMAGE: {

        Log.e(TAG, "blayzupe CROP_IMAGE");
        getFile(mDirectory, mTemp).delete();

        Bundle extras = data.getExtras();
        if (extras != null) {

          // get data to Bitmap then write to file
          Bitmap croppedImg = extras.getParcelable("data");
          File output = getFile(mDirectory, mOutput);
          mCropUri = BitmapUtils.getImageUri(mActivity, croppedImg, mOutput);
          if (mOnFinishListener != null)
            mOnFinishListener.OnCropFinsh(output.getAbsolutePath(), mCropUri);
        }
      }
      break;
      default:
        Log.e(TAG, "blayzupe Result some thing");
        break;
    }// end Switch case
  }

  public boolean doCropImage(Uri uri) {
    try {
      // set CropUri for use in onActivityResult Method.
      Log.w(TAG, "blayzupe Start doCropImage(Uri uri) "+"uri="+uri.toString());

      mCropUri = uri;
      CropIntent intent = new CropIntent(mCropUri);

      List<ResolveInfo> list = mActivity.getPackageManager().queryIntentActivities(intent, 0);
      if (list.size() > 0) {
        Log.w(TAG, "blayzupe Found Crop Intent");
        mActivity.startActivityForResult(intent, CROP_IMAGE);
        return true;
      } else {
        Log.e(TAG, "blayzupe doCropImage(Uri)"+" Not Found Support Crop Activity");
        Log.e(TAG, "URI : "+uri.toString());
        getFile(mDirectory, mTemp).delete();
        return false;
      }
    } catch (ActivityNotFoundException anfe) {
      Log.e(TAG, "blayzupe doCropImage(Uri) "+"Not Found Support Crop Activity", anfe);
      anfe.printStackTrace();
      return false;
    }
  }

  private File getFile(File dir, String name) {
    File output = new File(dir, name);
    if (!output.exists()) {
      try {
        output.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return output;
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
    try {
      File temp = getFile(mDirectory, mTemp);

      // Take Image for Camera and write to tempfile
      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      intent.putExtra("outputFormat", CompressFormat.JPEG.toString());
      intent.putExtra("return-data", true);
      intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(temp));

      mActivity.startActivityForResult(intent, IMAGE_CAPTURE);
      return true;
    } catch (ActivityNotFoundException anfe) {
      anfe.printStackTrace();
      return false;
    }
  }

  public boolean doPickImage() {
    try {
      Log.d(TAG, "blayzupe doPickImage() START");

      Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
      intent.setType("image/*");

      // Use external Crop Intent if found
      if (CropIntent.hasSupportActivity(mActivity)) {
        Log.d(TAG, "blayzupe doPickImage() Found");
        intent.putExtra("return-data", return_data);

        mActivity.startActivityForResult(intent, PICK_FROM_FILE);

        // Use Internal Crop method of GET_CONTENT intent
        // This is more Risk method
      } else {
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
      return true;
    } catch (ActivityNotFoundException anfe) {
      anfe.printStackTrace();
      return false;
    }
  }

  public interface OnCropFinishListener {
    boolean OnCropFinsh(String path, Uri uri);
  }

  public interface OnNotFoundCropIntentListener {
    boolean OnNotFoundCropIntent(String path, Uri uri);
  }
}
