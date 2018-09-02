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

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import java.io.File;
import java.util.ArrayList;
import me.piruin.phototaker.PhotoSize;
import me.piruin.phototaker.PhotoTaker;
import me.piruin.phototaker.PhotoTakerListener;
import me.piruin.phototaker.PhotoTakerUtils;
import me.piruin.quickaction.ActionItem;
import me.piruin.quickaction.QuickAction;

public class SampleActivity extends AppCompatActivity {

  PhotoTaker photoTaker;
  private ImageView imageView;
  private QuickAction quickAction;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sample);

    imageView = (ImageView)findViewById(R.id.image);
    imageView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        quickAction.show(view);
      }
    });

    findViewById(R.id.take).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        photoTaker.showDialog();
      }
    });

    createPhotoTaker();
    createQuickAction();

    TedPermission.with(this)
            .setPermissionListener(new PermissionListener() {
              @Override
              public void onPermissionGranted() {
                imageView.setEnabled(true);
              }

              @Override
              public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                imageView.setEnabled(false);
              }
            })
            .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
            .check();
  }

  private void createPhotoTaker() {
    photoTaker = new PhotoTaker(this, new PhotoSize(1000, 1000));
    photoTaker.setListener(new PhotoTakerListener() {
      @Override public void onCancel(int action) {
        Toast.makeText(SampleActivity.this, "User canceled", Toast.LENGTH_SHORT).show();
      }

      @Override public void onError(int action) {
        Toast
          .makeText(SampleActivity.this, "Something error on "+action, Toast.LENGTH_SHORT)
          .show();
      }

      @Override public void onFinish(Intent intent) {
        if (intent.getData() != null) {
          imageView.setImageURI(intent.getData());
        } else if (intent.getParcelableExtra("data") != null) {
          Bitmap bitmap = intent.getParcelableExtra("data");
          imageView.setImageBitmap(bitmap);

          PhotoTakerUtils.writeBitmapToFile(bitmap, new File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES), "photo.jpg"));
        }
      }
    });
  }

  private void createQuickAction() {
    quickAction = new QuickAction(SampleActivity.this, QuickAction.VERTICAL);
    quickAction.setColor(Color.LTGRAY);
    quickAction.addActionItem(new ActionItem(1, "Take from Camera"));
    quickAction.addActionItem(new ActionItem(2, "Select from Gallery"));
    quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
      @Override public void onItemClick(ActionItem item) {
        switch (item.getActionId()) {
          case 1:
            photoTaker.captureImage();
            break;
          case 2:
            photoTaker.pickImage();
            break;
        }
      }
    });
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    Toast.makeText(this, "Result", Toast.LENGTH_SHORT).show();
    photoTaker.onActivityResult(requestCode, resultCode, data);
  }
}
