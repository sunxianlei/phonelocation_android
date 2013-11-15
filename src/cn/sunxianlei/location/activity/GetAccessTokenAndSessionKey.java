package cn.sunxianlei.location.activity;

import cn.sunxianlei.location.utils.HttpPost;
import cn.sunxianlei.location.utils.Util;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class GetAccessTokenAndSessionKey extends Activity {
	HttpPost httpPost;
	private String sessionKey;
	private int userId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		
		httpPost = new HttpPost();
		httpPost.getAccessToken();
		 
		sessionKey = Util.SESSION_KEY;
		userId = Util.userid;
		
		SharedPreferences settings = getSharedPreferences("config", Activity.MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("sessionKey", sessionKey);
		editor.putInt("userId", userId);
		editor.commit();
		
		if(String.valueOf(Util.userid) != null){
			Intent intent = new Intent(this, ActivityStart.class);
			Bundle bundle = new Bundle();
			bundle.putString("sessionKey", sessionKey);
			intent.putExtras(bundle);
			finish();
			startActivity(intent);
		}else{
			Toast.makeText(GetAccessTokenAndSessionKey.this, " ß∞‹¡À ...", Toast.LENGTH_LONG);
		}
	}
}
