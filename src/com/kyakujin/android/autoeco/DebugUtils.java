package com.kyakujin.android.autoeco;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 *  デバッグの設定に関するクラス
 */
public class DebugUtils {
    public static boolean isDebuggable(Context context) {
        PackageManager manager = context.getPackageManager();
        ApplicationInfo appInfo = null;
        try {
            appInfo = manager.getApplicationInfo(context.getPackageName(), 0);
        } catch(NameNotFoundException e) {
            return false;
        }
        
        if ((appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) 
                == ApplicationInfo.FLAG_DEBUGGABLE) {
            return true;
        }
        return false;
    }
}
