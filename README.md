# PhotoTaker
[![Build Status](https://travis-ci.org/piruin/phototaker.svg?branch=master)](https://travis-ci.org/piruin/phototaker)
[![Download](https://api.bintray.com/packages/blazei/maven/PhotoTaker/images/download.svg)](https://bintray.com/blazei/maven/PhotoTaker/_latestVersion)

> Make `capture` or `choose` photo -> `crop` process as easy as it should be.

This Android Library aim to wrap complexity those step

1. Capture image by external :camera: camera app or choose image in :framed_picture: gallery.
2. Then :scissors: crop image from `1.` with app that have _crop_ function.

## Installation

### Gradle

- **Step 1** - set [JCenter] repository (This step not require for modern android project)
- **Step 2** - Add dependencies on app module

```groovy
dependencies {
  implementation 'me.piruin:phototaker:LATEST_VERSION'
}
```
Change `LATEST_VERSION` to latest release version name

This library require `com.android.support:support-core-utils` and `com.android.support:support-fragment` that already come with `com.android.support:appcompat-v7`. If you already use AppCompat library (Who don't?). So, Don't have to worry about it.

## Usage

1. Create instance of `PhotoTaker` with current `Activity` and desire image size
2. set `PhotoTakerListener` with `setListener()`

```java
class PhotoTakeActivity extend Activity {

  PhotoTaker photoTaker;

  @Override protected void onCreate(Bundle savedInstanceState) {
    photoTaker = new PhotoTaker(this, new PhotoSize(1000, 1000));
    photoTaker.setListener(new PhotoTakerListener() {
        @Override public void onCancel(int action) {}

        @Override public void onError(int action) {}

        @Override public void onFinish(Intent intent) {
          if (intent.getData() != null) {
            imageView.setImageURI(intent.getData());
          }
        }
      });
    }
  }
```

3. Override `onActivityResult()` and call `onActivityResult()` of instance of `PhotoTaker`

```java
  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    photoTaker.onActivityResult(requestCode, resultCode, data);
  }
```

4. Get photo by 3 option
  * Capture with camera app `captureImage()`
  * Choose photo in gallery with `pickImage()`
  * Use `showDialog()` to let user choose by built-in dialog

``` java
  findViewById(R.id.someButton).setOnClickListener(new View.OnClickListener() {
    @Override public void onClick(View view) {
      photoTaker.showDialog();
      // photoTaker.captureImage()
      // photoTaker.pickImage()
    }
  });
```

5. Get your cropped photo via `Intent` parameter of `PhotoTakerListener.onSuccess()`
  * Get image's uri by `intent.getData()`

See [SampleActivity] for more Information.

## Setup your application

### Add FileProvider
cause by [file:// scheme is now not allowed to be attached with Intent on targetSdkVersion 24 (Android Nougat)](https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en) make you have do more little work

Add `FileProvider` at your application manifest

```xml
   <provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="${applicationId}.provider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/provider_paths"/>
   </provider>
```

Add `res/xml/provider_paths.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-path name="external_files" path="."/>
</paths>

```

### Request for Read/Write-External-Storage permission

To read content Uri of crop image, your user must grant `READ_EXTERNAL_STORAGE` permission.
and for *pick image* from gallery your user must grant `WRITE_EXTERNAL_STORAGE` instead.

There are many great library waiting to help you handle that.

## License

This project under [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0) license

    Copyright (c) 2016 Piruin Panichphol
      National Electronics and Computer Technology Center, Thailand

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[JCenter]: https://bintray.com/bintray/jcenter
[SampleActivity]: https://github.com/piruin/phototaker/blob/master/phototaker-sample/src/main/java/me/piruin/phototaker/sample/SampleActivity.java
