package cn.sunxianlei.location;


import cn.sunxianlei.phonelocation.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class LocationPreferences {
	private final SharedPreferences preferences;
	private final Context context;
	
	public LocationPreferences(Context context){
		this.context=context;
		this.preferences=PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public boolean isStartAtBootEnabled(){
		return preferences.getBoolean(context.getString(R.string.setting_key_service_startup), false);
	}
	
	public boolean isUsedGPSEnabled(){
		return preferences.getBoolean(context.getString(R.string.setting_key_args_gps), false);
	}
	
	public String getGPSScanTime(){
		return preferences.getString(context.getString(R.string.setting_key_args_time), "3");
	}
	
	public String getDistance(){
		return preferences.getString(context.getString(R.string.setting_key_args_distance), "500");
	}
	
	public String getRadius(){
		return preferences.getString(context.getString(R.string.setting_key_args_radius), "1000");
	}
}
