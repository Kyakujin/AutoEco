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
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.R;
import com.kyakujin.android.autoeco.db.dao.EcoDAO;

/**
 * マナーモードの設定ダイアログ
 */
public class SilentModeSettingFragment extends DialogFragment {
    
    private int mCurrentEcoId;
    private Activity mActivity;
    private int mSilentMode;
    
    /**
     * New instance.
     *
     * @return an instance of {@link BrightnessDialogFragment}
     */
    public static SilentModeSettingFragment newInstance() {
        return new SilentModeSettingFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mActivity = getActivity();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_silentmode, null, false);
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroupSilentMode);
        
        // ラジオグループのチェック状態が変更された時に呼び出されるコールバックリスナー
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioNormal:
                        mSilentMode = AudioManager.RINGER_MODE_NORMAL;
                        break;
                    case R.id.radioSilent:
                        mSilentMode = AudioManager.RINGER_MODE_SILENT;
                        break;
                    case R.id.radioVibrate:
                        mSilentMode = AudioManager.RINGER_MODE_VIBRATE;
                        break;
                    default:
                }
            }
        });

        mCurrentEcoId = getArguments().getInt(Conf.SHARED_ECOID);        
        mSilentMode = getArguments().getInt(Conf.SHARED_SILENTMODE);
        
        switch (mSilentMode) {
            case AudioManager.RINGER_MODE_NORMAL:
                radioGroup.check(R.id.radioNormal);
                break;
            case AudioManager.RINGER_MODE_SILENT:
                radioGroup.check(R.id.radioSilent);
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                radioGroup.check(R.id.radioVibrate);
                break;
            default:
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.setting_silent);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                EcoDAO dao = new EcoDAO(mActivity);
                dao.updateSilentMode(mCurrentEcoId, mSilentMode);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.setView(view);

        return builder.create();

    }

}
