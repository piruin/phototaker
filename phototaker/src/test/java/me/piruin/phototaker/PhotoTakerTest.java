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

package me.piruin.phototaker;

import android.app.Activity;
import android.content.Intent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_FIRST_USER;
import static android.app.Activity.RESULT_OK;
import static me.piruin.phototaker.PhotoTaker.CAPTURE_IMAGE;
import static me.piruin.phototaker.PhotoTaker.CROP_IMAGE;
import static me.piruin.phototaker.PhotoTaker.PICK_IMAGE;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PhotoTakerTest {

  private static final int OTHER_ACTION = 1;
  private final PhotoSize size = new PhotoSize(400, 400);

  @Mock PhotoTakerListener listener;
  @Mock Activity activity;
  private PhotoTaker taker;

  @Before public void setUp() throws Exception {
    taker = new PhotoTaker(activity, size);
    taker.setListener(listener);
  }

  @Test public void onResultCancel() throws Exception {
    taker.onActivityResult(CROP_IMAGE, RESULT_CANCELED, null);
    verify(listener).onCancel(CROP_IMAGE);

    taker.onActivityResult(CAPTURE_IMAGE, RESULT_CANCELED, null);
    verify(listener).onCancel(CAPTURE_IMAGE);
  }

  @Test public void onResultNotOk() throws Exception {
    taker.onActivityResult(CAPTURE_IMAGE, RESULT_FIRST_USER, null);
    verify(listener).onError(CAPTURE_IMAGE);

    taker.onActivityResult(CROP_IMAGE, RESULT_FIRST_USER, null);
    verify(listener).onError(CROP_IMAGE);
  }

  @Test public void onResultOfOtherAction() throws Exception {
    taker.onActivityResult(OTHER_ACTION, RESULT_FIRST_USER, null);
    verify(listener, never()).onError(anyInt());

    taker.onActivityResult(OTHER_ACTION, RESULT_CANCELED, null);
    verify(listener, never()).onCancel(anyInt());
  }

  @Test public void onResultOk() throws Exception {
    taker.cropAction = mock(PhotoTaker.Action.class);
    taker.captureAction = mock(PhotoTaker.Action.class);
    taker.pickAction = mock(PhotoTaker.Action.class);
    Intent intent = mock(Intent.class);

    taker.onActivityResult(CROP_IMAGE, RESULT_OK, intent);
    verify(taker.cropAction).onResult(intent);

    taker.onActivityResult(CAPTURE_IMAGE, RESULT_OK, intent);
    verify(taker.captureAction).onResult(intent);

    taker.onActivityResult(PICK_IMAGE, RESULT_OK, intent);
    verify(taker.pickAction).onResult(intent);
  }
}
