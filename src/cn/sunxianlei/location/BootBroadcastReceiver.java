package cn.sunxianlei.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG="BootBroadcastReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		LocationPreferences preferences=new LocationPreferences(context);
		if(!preferences.isStartAtBootEnabled()){
			return;
		}
		Log.i(TAG, String.valueOf(preferences.isStartAtBootEnabled()));
		Intent service=new Intent(context,LocationService.class);
		context.startService(service);
	}
}
