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

package com.kyakujin.android.autoeco;

import java.util.ArrayList;

import android.view.View;
import android.widget.CheckBox;

/**
 * チェックボックスのクリックリスナー登録ユーティリティ
 *
 */
public abstract class CheckBoxUtils {

    protected ArrayList<CheckBox> mViewList;

    public void setCheckBoxListenerControl() {

        for (CheckBox cb : mViewList) {
            cb.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v)
                {
                    function((CheckBox) v);
                }
            });
        }

    }

    public void addCheckBox(CheckBox cb) {
        if (mViewList == null) {
            mViewList = new ArrayList<CheckBox>();
        }
        mViewList.add(cb);
    }

    abstract public void function(CheckBox v);
}