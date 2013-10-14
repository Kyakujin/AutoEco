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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.kyakujin.android.autoeco.Conf;
import com.kyakujin.android.autoeco.Logger;
import com.kyakujin.android.autoeco.R;
import com.kyakujin.android.autoeco.db.dao.EcoDAO;

/**
 * 画面輝度の設定ダイアログ
 */
public class BrightnessSettingFragment extends DialogFragment {
    private final String TAG = Conf.APP_NAME + ":" + this.getClass().getSimpleName();

    private Activity mActivity;
    private SeekBar mSeekBar;
    private int mBrightnessVal = 50;
    private TextView mBrightnessDesc;
    private CheckBox mAutoBrihtness;
    private LinearLayout mLayoutMask;
    private int mCurrentEcoId;

    /**
     * New instance.
     * 
     * @return an instance of {@link BrightnessDialogFragment}
     */
    public static BrightnessSettingFragment newInstance() {
        return new BrightnessSettingFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mActivity = getActivity();

        mCurrentEcoId = getArguments().getInt(Conf.SHARED_ECOID);
        mBrightnessVal = getArguments().getInt(Conf.SHARED_BRIGHTNESSVALUE);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_setting_brightness, null, false);
        mBrightnessDesc = (TextView) view.findViewById(R.id.textBrightnessLabel);
        mBrightnessDesc.setText(mActivity.getResources()
                .getString(R.string.desc_brightness) + mBrightnessVal);
        
        mLayoutMask = (LinearLayout) view.findViewById(R.id.layoutMask);
        mLayoutMask.setVisibility(View.INVISIBLE);
        
        
        mSeekBar = (SeekBar) view.findViewById(R.id.seekBrightness);
        mSeekBar.setMax(255);
        mSeekBar.setProgress(mBrightnessVal);
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Logger.d(TAG, "onStartTrackingTouch(): " +
                        String.valueOf(seekBar.getProgress()));
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
                Logger.d(TAG, "onProgressChanged(): " +
                        String.valueOf(progress) + ", " + String.valueOf(fromTouch));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                Logger.v(TAG, "onStopTrackingTouch(): " +
                        String.valueOf(seekBar.getProgress()));
                if (progress < 20) {
                    mBrightnessVal = 20;
                } else {
                    mBrightnessVal = progress;
                }
                mBrightnessDesc.setText(mActivity.getResources()
                        .getString(R.string.desc_brightness) + mBrightnessVal);
            }
        });

        
        mAutoBrihtness = (CheckBox) view.findViewById(R.id.checkAutoBrightness);
        if(getArguments().getInt(Conf.SHARED_BRIGHTNESSAUTO) == 1) {
            mAutoBrihtness.setChecked(true);
            mLayoutMask.setVisibility(View.VISIBLE);
            mSeekBar.setEnabled(false);
        } else {
            mAutoBrihtness.setChecked(false);
            mLayoutMask.setVisibility(View.INVISIBLE);
            mSeekBar.setEnabled(true);            
        }        
        mAutoBrihtness.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mLayoutMask.setVisibility(View.VISIBLE);
                } else {
                    mLayoutMask.setVisibility(View.INVISIBLE);
                }
                EcoDAO dao = new EcoDAO(mActivity);
                dao.updateBrightnessAuto(mCurrentEcoId, isChecked);
                mSeekBar.setEnabled(!isChecked);
            }
        });
        
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(R.string.setting_brightness);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                EcoDAO dao = new EcoDAO(mActivity);
                dao.updateBrightnessValue(mCurrentEcoId, mBrightnessVal);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.setView(view);

        return builder.create();

    }
    
    // Below methods are not used.
    private int toPercentage(int brightnessRaw) {
        return brightnessRaw * 100 / 255;
    }
    
    private int toRawVal(int brightnessPer) {
        return brightnessPer * 255 / 100;
    }
}
