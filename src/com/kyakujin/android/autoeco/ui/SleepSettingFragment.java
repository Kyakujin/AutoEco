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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.Conf.SleepTime;
import com.kyakujin.android.autoeco.R;
import com.kyakujin.android.autoeco.db.dao.EcoDAO;

/**
 * スリープモードの設定ダイアログ
 */
public class SleepSettingFragment extends DialogFragment {
    private Activity mActivity;
    private int mCurrentEcoId;
    private SleepTime mSleepTime = SleepTime.TIME1;

    public static SleepSettingFragment newInstance() {
        return new SleepSettingFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mActivity = getActivity();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_sleep, null, false);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroupSleep);

        // ラジオグループのチェック状態が変更された時に呼び出されるコールバックリスナー
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioTime1:
                        mSleepTime = SleepTime.TIME1;
                        break;
                    case R.id.radioTime2:
                        mSleepTime = SleepTime.TIME2;
                        break;
                    case R.id.radioTime3:
                        mSleepTime = SleepTime.TIME3;
                        break;
                    case R.id.radioTime4:
                        mSleepTime = SleepTime.TIME4;
                        break;
                    case R.id.radioTime5:
                        mSleepTime = SleepTime.TIME5;
                        break;
                    case R.id.radioTime6:
                        mSleepTime = SleepTime.TIME6;
                        break;
                    default:
                }
            }
        });

        mCurrentEcoId = getArguments().getInt(Conf.SHARED_ECOID);
        mSleepTime = Conf.mapSleepTime.get(getArguments().getInt(Conf.SHARED_SLEEPTIME_ORDINAL));

        // デフォルトラジオボタンの設定
        setDefaultRadioButton(radioGroup);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.setting_sleep);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                EcoDAO dao = new EcoDAO(mActivity);
                dao.updateSleepTime(mCurrentEcoId, mSleepTime.ordinal());
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.setView(view);

        return builder.create();

    }

    private void setDefaultRadioButton(RadioGroup radioGroup) {
        switch (mSleepTime) {
            case TIME1:
                radioGroup.check(R.id.radioTime1);
                break;
            case TIME2:
                radioGroup.check(R.id.radioTime2);
                break;
            case TIME3:
                radioGroup.check(R.id.radioTime3);
                break;
            case TIME4:
                radioGroup.check(R.id.radioTime4);
                break;
            case TIME5:
                radioGroup.check(R.id.radioTime5);
                break;
            case TIME6:
                radioGroup.check(R.id.radioTime6);
                break;
            default:
        }
    }
}
