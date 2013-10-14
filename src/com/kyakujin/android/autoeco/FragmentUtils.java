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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * フラグメント関連のユーティリティクラス
 */
public class FragmentUtils {

    /**
     * フラグメントを置き換えるユーティリティ。
     *
     * @param manager フラグメントマネージャ
     * @param fragment 遷移先のフラグメントクラス
     * @param bundle 遷移先のフラグメントへ渡すデータセット用
     */
    public static void replaceFragment(FragmentManager manager, Fragment fragment, Bundle bundle, String Tag) {
        FragmentTransaction transaction = manager.beginTransaction();

        // 遷移先のフラグメントに渡す値をセット
        if (bundle != null) {
            fragment.setArguments(bundle);
        }

        transaction.replace(android.R.id.content, fragment, Tag);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
