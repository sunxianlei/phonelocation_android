package cn.sunxianlei.location.activity;

import cn.sunxianlei.location.utils.Util;
import cn.sunxianlei.phonelocation.R;
import android.R.anim;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.Visibility;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityStart extends Activity {
	ImageButton renrenLogin;
	ImageButton renrenLogout;
	ImageButton bindingBtn;
	ImageButton settingBtn;
	TextView userInfoTextView;
	TextView imeiTextView;
	SharedPreferences settings;
	String imei;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_activity);
		renrenLogin=(ImageButton)findViewById(R.id.imageButton_login);
		renrenLogout=(ImageButton)findViewById(R.id.imageButton_logout);
		bindingBtn=(ImageButton)findViewById(R.id.imageButton_binding);
		settingBtn=(ImageButton)findViewById(R.id.imageButton_setting);
		userInfoTextView=(TextView)findViewById(R.id.textView_userid);
		imeiTextView=(TextView)findViewById(R.id.textView_imei);
		
		TelephonyManager tmManager=(TelephonyManager)this.getSystemService(TELEPHONY_SERVICE);
		imei=tmManager.getDeviceId();
		String imeiString=getString(R.string.start_imei)+imei;
		imeiTextView.setText(imeiString);
		
		settings = getSharedPreferences("config",Activity.MODE_WORLD_WRITEABLE);
		Util.userid=settings.getInt("userId", 0);
		renrenLogin.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(ActivityStart.this,ActivityRenrenWebview.class);  	
				startActivity(intent);	
			}
		});
		renrenLogout.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(ActivityStart.this).setTitle("退出人人登陆吗？")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							SharedPreferences.Editor editor = settings.edit();
							editor.clear();
							editor.commit();
							Util.userid=0;
							Util.renew=true;
							initSetting();
						}
					})
					.setNegativeButton("返回", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					}).show();
			}
			
		});
		bindingBtn.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bundle bundle=new Bundle();
				bundle.putString("imei", imei);
				bundle.putInt("renrenid", Util.userid);
				Intent intent=new Intent(ActivityStart.this,ActivityBinding.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
			
		});
		settingBtn.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(ActivityStart.this,ActivityMain.class);
				startActivity(intent);
			}
			
		});
		
		initSetting();
	}
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		if (hasFocus) {
			initSetting();
		}
	}
	
	private void initSetting(){
		if(Util.userid!= 0){
			String idString=getString(R.string.start_user_id)+String.valueOf(Util.userid);
			userInfoTextView.setText(idString);
			
			renrenLogin.setEnabled(false);
			renrenLogin.setVisibility(View.INVISIBLE);
			renrenLogout.setEnabled(true);
			renrenLogout.setVisibility(View.VISIBLE);
			
			bindingBtn.setEnabled(true);
			bindingBtn.setVisibility(View.VISIBLE);
		}else {
			userInfoTextView.setText(R.string.start_user_info);
			
			renrenLogin.setEnabled(true);
			renrenLogin.setVisibility(View.VISIBLE);
			renrenLogout.setEnabled(false);
			renrenLogout.setVisibility(View.INVISIBLE);
			
			bindingBtn.setEnabled(false);
			bindingBtn.setVisibility(View.INVISIBLE);
		}
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		new AlertDialog.Builder(ActivityStart.this).setTitle("确认退出吗？")
			.setMessage("退出后，定位服务会根据您的设置决定是否后台正常运行\n\n建议退出前最好确认下服务是否正在运行")
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					ActivityStart.this.finish();
				}
			})
			.setNegativeButton("返回", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			}).show();
	}
	
}
