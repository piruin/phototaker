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
import android.content.Intent;
import android.net.Uri;

public class CropIntent extends Intent {

  public static final String ACTION = "com.android.camera.action.CROP";
  public static final PhotoSize DEFAULT_SIZE = new PhotoSize(600, 600);

  private boolean return_data = true;
  private boolean scale = true;
  private boolean faceDetection = true;

  public CropIntent(Uri uri) {
    super(ACTION);
    setDataAndType(uri, "image/*");
    putExtra("noFaceDetection", faceDetection);
    putExtra("scale", scale);
    putExtra("return-data", return_data);
    setOutput(DEFAULT_SIZE);
  }

  public void setOutput(PhotoSize size) {
    putExtra("aspectX", size.widthRatio());
    putExtra("aspectY", size.heightRatio());
    putExtra("outputX", size.width);
    putExtra("outputY", size.height);
  }

  public static boolean hasSupportActivity(Context context) {
    Intent intent = new Intent(ACTION);
    intent.setType("image/*");
    return intent.resolveActivity(context.getPackageManager()) != null;
  }
}
