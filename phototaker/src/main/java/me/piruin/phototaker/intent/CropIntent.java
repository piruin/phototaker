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

package me.piruin.phototaker.intent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import me.piruin.phototaker.PhotoSize;

@SuppressWarnings("WeakerAccess")
public class CropIntent extends Intent {

  @NonNull public static final String ACTION_CROP = "com.android.camera.action.CROP";
  @NonNull public static final PhotoSize DEFAULT_SIZE = new PhotoSize(1024, 1024);

  public CropIntent(@NonNull Uri uri) {
    super(ACTION_CROP);
    setDataAndType(uri, "image/*");
    putExtra("noFaceDetection", false);
    putExtra("scale", true);
    putExtra("return-data", true);
    setOutput(DEFAULT_SIZE);
  }

  public void setOutput(@NonNull PhotoSize size) {
    putExtra("aspectX", size.widthRatio());
    putExtra("aspectY", size.heightRatio());
    putExtra("outputX", size.width);
    putExtra("outputY", size.height);
  }

  public static boolean hasSupportActivity(@NonNull Context context) {
    Intent intent = new Intent(ACTION_CROP);
    intent.setType("image/*");
    return intent.resolveActivity(context.getPackageManager()) != null;
  }
}
