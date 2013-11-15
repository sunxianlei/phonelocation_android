package cn.sunxianlei.location.update;

import cn.sunxianlei.phonelocation.R;
import android.content.Context;
import android.util.Log;

public class UpdateConfig {
	private static final String TAG="Config";
	
	public static int getVerCode(Context context){
		int verCode=-1;
		try {
			verCode=context.getPackageManager().getPackageInfo("cn.sunxianlei.phonelocation", 0).versionCode;
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, e.getMessage());
		}
		return verCode;
	}
	
	public static String getVerName(Context context){
		String verName="";
		try {
			verName=context.getPackageManager().getPackageInfo("cn.sunxianlei.phonelocation", 0).versionName;
		} catch (Exception e) {
			// TODO: handle exception
			Log.e(TAG, e.getMessage());
		}
		return verName;
	}
	
	public static String getAppName(Context context){
		String verName=context.getResources().getText(R.string.app_name).toString();
		return verName;
	}

}
