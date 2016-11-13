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

package me.piruin.phototaker.sample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import me.piruin.phototaker.PhotoTaker;

public class SampleActivity extends AppCompatActivity {

  PhotoTaker photoTaker;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sample);

    photoTaker = new PhotoTaker(this);
    photoTaker.setCropfinishListener(new PhotoTaker.OnCropFinishListener() {
      @Override public boolean OnCropFinsh(String path, Uri uri) {
        ImageView imageView = (ImageView)findViewById(R.id.image);
        if (uri != null)
          imageView.setImageURI(uri);
        else {
          Toast.makeText(SampleActivity.this, "Image at "+path, Toast.LENGTH_SHORT).show();
          File imgFile = new File(path);

          if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
          }
        }
        return false;
      }
    });
    photoTaker.setNotFoundCropIntentListener(new PhotoTaker.OnNotFoundCropIntentListener() {
      @Override public boolean OnNotFoundCropIntent(String path, Uri uri) {
        Toast.makeText(SampleActivity.this, "Not found crop intent", Toast.LENGTH_SHORT).show();
        return false;
      }
    });
    findViewById(R.id.take).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        photoTaker.doShowDialog();
      }
    });
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Toast.makeText(this, "Result", Toast.LENGTH_SHORT).show();
    photoTaker.onActivityResult(requestCode, resultCode, data);
  }
}
