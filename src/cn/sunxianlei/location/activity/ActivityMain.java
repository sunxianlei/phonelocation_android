package cn.sunxianlei.location.activity;


import cn.sunxianlei.location.LocationService;
import cn.sunxianlei.location.update.UpdateManager;
import cn.sunxianlei.phonelocation.R;
import android.R.bool;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class ActivityMain extends PreferenceActivity implements OnPreferenceClickListener {
	
	private Preference aboutPreference;
	private Preference checkUpdatePreference;
	//private Preference donatePreference;
	private Preference tipsPreference;
	private Preference serviceStartStoPreference;
	private UpdateManager updateManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.arctivity_main);
		
		checkUpdatePreference=(Preference)findPreference(getString(R.string.setting_key_checkupdate));
		aboutPreference=(Preference)findPreference(getString(R.string.setting_key_about));
		//donatePreference=(Preference)findPreference(getString(R.string.setting_key_donate));
		tipsPreference=(Preference)findPreference(getString(R.string.setting_key_tips));
		serviceStartStoPreference=(Preference)findPreference(getString(R.string.setting_key_service_startstop));
		
		checkUpdatePreference.setOnPreferenceClickListener(this);
		aboutPreference.setOnPreferenceClickListener(this);
		//donatePreference.setOnPreferenceClickListener(this);
		tipsPreference.setOnPreferenceClickListener(this);
		serviceStartStoPreference.setOnPreferenceClickListener(this);
		
		updateServiceStatus();
		
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		
		if (preference.getKey().equals(getString(R.string.setting_key_checkupdate))) {
		//ready for checkupdate
			updateManager=new UpdateManager(this);
			updateManager.checkUpdate();
			
		} else if(preference.getKey().equals(getString(R.string.setting_key_about))){
			new AlertDialog.Builder(this)
			.setTitle(getString(R.string.setting_about_dialog_title))
			.setMessage(getString(R.string.setting_about_dialog_message))
			.setPositiveButton(getString(R.string.setting_about_dialog_buttontext), null)
			.show();
		} else if(preference.getKey().equals(getString(R.string.setting_key_donate))){
		//ready for donate
			showCustomDlalogDonate();
		} else if(preference.getKey().equals(getString(R.string.setting_key_service_startstop))){
		//ready for startstopservice
			toggleServiceStatus();
		} else if (preference.getKey().equals(getString(R.string.setting_key_tips))) {
			new AlertDialog.Builder(this)
			.setTitle(getString(R.string.setting_tips_dialog_title))
			.setMessage(getString(R.string.setting_tips_dialog_message))
			.setPositiveButton(getString(R.string.setting_tips_dialog_buttontext), null)
			.show();
		}
		return false;
	}
	
	private void updateServiceStatus() {
		// TODO Auto-generated method stub
		boolean isServiceRunning=LocationService.isRunning(this);
		serviceStartStoPreference.setTitle(isServiceRunning?R.string.setting_service_stop:R.string.setting_service_start);
		serviceStartStoPreference.setSummary(isServiceRunning?R.string.setting_service_start_summary:R.string.setting_service_stop_summary);
	}

	private void toggleServiceStatus() {
		// TODO Auto-generated method stub
		boolean isServiceRunning=LocationService.isRunning(this);
		int textID;
		if(isServiceRunning){
			LocationService.stop(this);
			textID=R.string.setting_service_stop_summary;
		}else {
			LocationService.start(this);
			textID=R.string.setting_service_start_summary;
		}
		Toast.makeText(this, textID, Toast.LENGTH_SHORT).show();
		updateServiceStatus();
	}

	private void showCustomDlalogDonate(){
		AlertDialog.Builder customDlg=new AlertDialog.Builder(ActivityMain.this);
		View viewDlg=LayoutInflater.from(ActivityMain.this).inflate(R.layout.custom_dialog_donate, null);
		customDlg.setTitle(getString(R.string.setting_donate_dialog_title));
		customDlg.setView(viewDlg);
		customDlg.setPositiveButton(getString(R.string.setting_about_dialog_buttontext), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});
		customDlg.create().show();
		
		ImageButton donateButton=(ImageButton)viewDlg.findViewById(R.id.imgbtn_donate_alipay);
		donateButton.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(getString(R.string.url_donate_alipay))));
			}
			
		});
	}
	



	

}
