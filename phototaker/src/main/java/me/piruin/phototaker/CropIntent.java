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
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import java.util.List;

public class CropIntent extends Intent {

  public static final String ACTION = "com.android.camera.action.CROP";

  private int outputX = 240;
  private int outputY = 240;
  private int aspectX = 1;
  private int aspectY = 1;
  private boolean return_data = true;
  private boolean scale = true;
  private boolean faceDetection = true;

  public CropIntent(Uri uri) {
    super(ACTION);
    setDataAndType(uri, "image/*");
    putExtra("noFaceDetection", faceDetection);
    putExtra("aspectX", aspectX);
    putExtra("aspectY", aspectY);
    putExtra("outputX", outputX);
    putExtra("outputY", outputY);
    putExtra("scale", scale);
    putExtra("return-data", return_data);
  }

  public static boolean hasSupportActivity(Context context) {
    Intent intent = new Intent(ACTION);
    intent.setType("image/*");
    List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, 0);
    return (list.size() > 0) ? true : false;
  }
}
