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
  compile 'me.piruin:phototaker:LATEST_VERSION'
}
```
Change `LATEST_VERSION` to latest release version name

## Usage

1. Create instance of `PhotoTaker` with current `Activity` and desire image size
2. set `PhotoTakerListener` with `setListener()`
3. Override `onActivityResult()` and call `onActivityResult()` of instance of `PhotoTaker`
4. Get photo by 3 option
  * Capture with camera app `captureImage()`
  * Choose photo in gallery with `pickImage()`
  * Use `showDialog()` to let user choose by built-in dialog
5. Get your cropped photo via `Intent` parameter of `PhotoTakerListener.onSuccess()`
  * Get raw bitmap by `Bitmap bitmap = intent.getParcelableExtra("data");`
  * Get data's uri by `intent.getData()`

See [SampleActivity] for more Information.

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
