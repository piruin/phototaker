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

import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class PhotoSizeRatioTest {

  @Parameter(0) public int width;
  @Parameter(1) public int height;
  @Parameter(2) public int xRatio;
  @Parameter(3) public int yRatio;

  @Parameters(name = "{0}x{1} = {2}x{3}") public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
      {400, 400, 1, 1}, {800, 400, 2, 1}, {400, 1200, 1, 3}, {800, 600, 4, 3},
      });
  }

  @Test public void name() throws Exception {
    PhotoSize image = new PhotoSize(width, height);
    assertEquals(xRatio, image.widthRatio());
    assertEquals(yRatio, image.heightRatio());
  }
}

