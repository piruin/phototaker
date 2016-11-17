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

public class PhotoSize {
  public final int width;
  public final int height;
  private int heightRatio;
  private int widthRatio;

  public PhotoSize(int width, int height) {
    this.width = width;
    this.height = height;

    aspect(width < height ? width : height, width < height ? height : width);
  }

  private void aspect(int most, int less) {
    int nextLess = most%less;
    if (nextLess != 0) {
      aspect(less, nextLess);
    } else {
      this.widthRatio = width/less;
      this.heightRatio = height/less;
    }
  }

  public int widthRatio() {
    return widthRatio;
  }

  public int heightRatio() {
    return heightRatio;
  }
}
