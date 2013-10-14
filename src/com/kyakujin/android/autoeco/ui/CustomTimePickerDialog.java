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

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TimePicker;

/**
 * カスタマイズした時刻設定ダイアログ
 */
public class CustomTimePickerDialog extends TimePickerDialog implements TimeSetting {

    private TimeData mTimeSettingData;
    private TimePicker mTimePicker;

    public CustomTimePickerDialog(Context context, OnTimeSetListener callBack,
            int hourOfDay, int minute, boolean is24HourView) {
        super(context, callBack, hourOfDay, minute, is24HourView);
    }
    
    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        super.onTimeChanged(view, hourOfDay, minute);
        if(mTimeSettingData != null) {
            mTimeSettingData.setHour(hourOfDay);
            mTimeSettingData.setMinute(minute);
            mTimePicker = view;
        }
    }

    @Override
    public TimeData getTimeData() {
        return mTimeSettingData;
    }

    @Override
    public void setTimeData(TimeData data) {
        mTimeSettingData = data;
    }
    
    public TimePicker testGetTimePicker() {
        return mTimePicker;
    }
}
