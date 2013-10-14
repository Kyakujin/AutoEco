/*
 * Copyright 2013 Yoshihiro Miyama
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kyakujin.android.autoeco.ui;

/**
 * スケジュールリストアイテム<br>
 * リストビューにバインドする。
 */
public class SchedListItem {
    private int mId;
    private CharSequence mTimeString;
    private Boolean mChecked;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public SchedListItem() {
        mTimeString = "";
        mChecked = false;
    }

    public SchedListItem(CharSequence timestring, Boolean checked) {
        mTimeString = timestring;
        mChecked = checked;
    }

    public CharSequence getTimeString() {
        return mTimeString;
    }

    public void setTitle(CharSequence timestring) {
        mTimeString = timestring;
    }

    public Boolean getChecked() {
        return mChecked;
    }

    public void setChecked(Boolean checked) {
        mChecked = checked;
    }
}
