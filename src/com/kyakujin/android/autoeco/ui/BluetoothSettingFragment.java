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
import com.kyakujin.android.autoeco.R;
import com.kyakujin.android.autoeco.db.dao.EcoDAO;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

/**
 * Bluetooth機能の設定ダイアログ
 */
public class BluetoothSettingFragment extends DialogFragment  {
    private final String TAG = Conf.APP_NAME +":" + this.getClass().getSimpleName();

    private Activity mActivity;
    private int mCurrentEcoId;
    private boolean mEnabled;

    public static BluetoothSettingFragment newInstance() {
        return new BluetoothSettingFragment();
    }
   
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mActivity = getActivity();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_setting_bluetooth, null, false);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroupBluetooth);

        // ラジオグループのチェック状態が変更された時に呼び出されるコールバックリスナー
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            // ラジオグループのチェック状態が変更された時にコール
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioBluetoothOn:
                        mEnabled = true;
                        break;
                    case R.id.radioBluetoothOff:
                        mEnabled = false;
                        break;
                    default:
                        mEnabled = false;
                }
            }
        });
        
        mCurrentEcoId = getArguments().getInt(Conf.SHARED_ECOID);
        mEnabled = getArguments().getInt(Conf.SHARED_BLUETOOTH_ENABLED) == 1 ? true : false;
        
        // デフォルトラジオボタンの設定
        setDefaultRadioButton(radioGroup);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.setting_bluetooth);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                EcoDAO dao = new EcoDAO(mActivity);
                dao.updateBluetoothEnabled(mCurrentEcoId, mEnabled);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.setView(view);

        return builder.create();

    }

    private void setDefaultRadioButton(RadioGroup radioGroup) {
        int enabled = (mEnabled == true ? 1 : 0);
        switch (enabled) {
            case 1:
                radioGroup.check(R.id.radioBluetoothOn);
                break;
            case 0:
                radioGroup.check(R.id.radioBluetoothOff);
                break;
            default:
        }
    }
}
