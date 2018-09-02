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

package me.piruin.phototaker.sample.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import java.util.ArrayList;
import me.piruin.phototaker.PhotoSize;
import me.piruin.phototaker.PhotoTaker;
import me.piruin.phototaker.PhotoTakerListener;
import me.piruin.phototaker.sample.R;

public class PhotoTakeFragment extends Fragment {

  private Button takeButton;
  private ImageView imageView;

  private PhotoTaker photoTaker;
  private PhotoSize photoSize = new PhotoSize(400, 400);

  @Nullable @Override public View onCreateView(
    @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
    @Nullable Bundle savedInstanceState)
  {
    View view = inflater.inflate(R.layout.fragment_sample, container, false);
    takeButton = view.findViewById(R.id.take);
    imageView = view.findViewById(R.id.image);
    return view;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    photoTaker = new PhotoTaker(this, photoSize);
    photoTaker.setListener(new PhotoTakerListener() {
      @Override public void onCancel(int action) {
        Toast.makeText(getActivity(), "Cancel", Toast.LENGTH_LONG).show();
      }

      @Override public void onError(int action) {
        Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
      }

      @Override public void onFinish(Intent intent) {
        imageView.setImageURI(intent.getData());
      }
    });
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    takeButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        photoTaker.showDialog();
      }
    });

    TedPermission
      .with(getActivity())
      .setPermissionListener(new PermissionListener() {
        @Override public void onPermissionGranted() {
          takeButton.setEnabled(true);
        }

        @Override public void onPermissionDenied(ArrayList<String> deniedPermissions) {
          takeButton.setEnabled(false);
        }
      })
      .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                      Manifest.permission.WRITE_EXTERNAL_STORAGE)
      .check();
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    photoTaker.onActivityResult(requestCode, resultCode, data);
  }
}
