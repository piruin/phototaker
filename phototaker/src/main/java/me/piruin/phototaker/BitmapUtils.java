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

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

final class BitmapUtils {

  private BitmapUtils() {
  }

  static Uri getImageUrlWithAuthority(Context context, Uri uri) {
    Bitmap bitmap = getBitmapFromUri(context, uri);
    return getImageUri(context, bitmap, "Temp");
  }

  static Uri getImageUri(Context context, Bitmap image, String title) {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
    String path = Media.insertImage(context.getContentResolver(), image, title, null);
    return Uri.parse(path);
  }

  static Bitmap getBitmapFromUri(Context context, Uri uri) {
    try {
      return Media.getBitmap(context.getContentResolver(), uri);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  static File getFile(File dir, String name) {
    File output = new File(dir, name);
    if (!output.exists()) {
      try {
        //noinspection ResultOfMethodCallIgnored
        output.createNewFile();
      } catch (IOException e) {
        Log.e("PhotoTaker", "Error create "+output.getAbsolutePath());
        e.printStackTrace();
      }
    }
    return output;
  }
}
