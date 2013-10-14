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

import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.Logger;
import com.kyakujin.android.autoeco.R;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.view.LayoutInflater;

/**
 * スケジュールリストビューに載せるチェックボックス
 */
public class CustomCheckable extends LinearLayout implements Checkable {
    private final String TAG = Conf.APP_NAME + ":" + this.getClass().getSimpleName();
    
    private CheckBox mCheckBox;
    private int mSchedId;
    
    public CustomCheckable(Context context) {
        super(context);

        View view = ((LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.list_item_sched, this, false);
                
        mCheckBox = (CheckBox) view .findViewById(R.id.checkSchedItem);
        mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {            
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              if (isChecked) {
                  Logger.d(TAG, "onChecked:true, id="+mSchedId);
              } else {
                  Logger.d(TAG, "onChecked:false, id="+mSchedId);
              }
          }
      });
        
        addView(view);
    }

    @Override
    public boolean isChecked() {
        return mCheckBox.isChecked();
    }

    @Override
    public void setChecked(boolean checked) {
        mCheckBox.setChecked(checked);
        Logger.d(TAG, "check:"+checked);        
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }
    
    public void setSchedId(int id) {
        mSchedId = id;
    }

}
