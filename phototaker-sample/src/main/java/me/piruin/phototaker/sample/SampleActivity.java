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

package me.piruin.phototaker.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import me.piruin.phototaker.PhotoSize;
import me.piruin.phototaker.PhotoTaker;
import me.piruin.phototaker.PhotoTakerListener;

public class SampleActivity extends AppCompatActivity {

  PhotoTaker photoTaker;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sample);

    photoTaker = new PhotoTaker(this, new PhotoSize(400, 400));
    photoTaker.setListener(new PhotoTakerListener() {
      @Override public void onCancel(int action) {
        Toast.makeText(SampleActivity.this, "User canceled", Toast.LENGTH_SHORT).show();
      }

      @Override public void onError(int action) {
        Toast
          .makeText(SampleActivity.this, "Something error on "+action, Toast.LENGTH_SHORT)
          .show();
      }

      @Override public void onFinish(String path, Uri uri) {
        //ImageView imageView = (ImageView)findViewById(R.id.image);
        //if (uri != null)
        //  imageView.setImageURI(uri);
        //else {
        //  Toast.makeText(SampleActivity.this, "Image at "+path, Toast.LENGTH_SHORT).show();
        //  File imgFile = new File(path);
        //
        //  if (imgFile.exists()) {
        //    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        //    imageView.setImageBitmap(myBitmap);
        //  }
        //}
      }

      @Override public void onFinish(Intent intent) {
        ImageView imageView = (ImageView)findViewById(R.id.image);
        imageView.setImageURI(intent.getData());
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
