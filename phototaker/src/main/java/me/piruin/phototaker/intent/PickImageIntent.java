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
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import java.io.File;
import me.piruin.phototaker.PhotoSize;

public class PickImageIntent extends Intent {

  public PickImageIntent(Context context, File outputFile) {
    super(Intent.ACTION_GET_CONTENT);
    setType("image/*");
    putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context,
            context.getPackageName() + ".provider", outputFile));
  }

  public void enableCrop(PhotoSize photoSize) {
    putExtra("crop", "true");
    putExtra("noFaceDetection", true);
    putExtra("aspectX", photoSize.widthRatio());
    putExtra("aspectY", photoSize.heightRatio());
    putExtra("outputX", photoSize.width);
    putExtra("outputY", photoSize.height);
    putExtra("scale", true);
  }
}
