package cn.sunxianlei.location.activity;

import cn.sunxianlei.location.utils.Binding;
import cn.sunxianlei.location.utils.Util;
import cn.sunxianlei.phonelocation.R;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityBinding extends Activity {
	static final String TAG="ActivityBinding";
	String imei="";
	int renrenid=0;
	
	TextView imeiTextView;
	TextView renrenIdTextView;
	TextView bindingResultTextView;
	ImageButton bindingIn;
	ImageButton bindingOut;
	StringBuffer sBuffer;
	ProgressDialog pDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
			.detectDiskReads()
			.detectDiskWrites()
			.detectAll()
			.penaltyLog()
			.build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
			.detectLeakedSqlLiteObjects()
			.detectAll()
			.penaltyLog()
			.penaltyDeath()
			.build());
		
		setContentView(R.layout.binding_activity);
		
		imeiTextView=(TextView)findViewById(R.id.textView_imei);
		renrenIdTextView=(TextView)findViewById(R.id.textView_renrenid);
		bindingResultTextView=(TextView)findViewById(R.id.textView_bindingresult);
		bindingIn=(ImageButton)findViewById(R.id.imageButton_binding_in);
		bindingOut=(ImageButton)findViewById(R.id.imageButton_binding_out);
		
		Bundle bundle=this.getIntent().getExtras();
		imei=bundle.getString("imei");
		renrenid=bundle.getInt("renrenid");
		
		String imeiString=getString(R.string.binding_imei)+imei;
		imeiTextView.setText(imeiString);
		String renrenIdString=getString(R.string.binding_renrenid)+String.valueOf(renrenid);
		renrenIdTextView.setText(renrenIdString);
		
		/*
		imeiTextView.setBackgroundColor(Color.argb(155, 255, 255, 255));
		renrenIdTextView.setBackgroundColor(Color.argb(155, 255, 255, 255));
		bindingResultTextView.setBackgroundColor(Color.argb(155, 255, 255, 255));
		imeiTextView.setTextColor(Color.argb(255, 0, 0, 0));
		renrenIdTextView.setTextColor(Color.argb(255, 0, 0, 0));
		bindingResultTextView.setTextColor(Color.argb(255, 0, 0, 0));
		*/
		
		sBuffer=new StringBuffer();
		sBuffer.append(imei);
		sBuffer.append(",");
		sBuffer.append("renrenid");
		sBuffer.append(",");
		sBuffer.append(renrenid);
		sBuffer.append(",");
		
		/*
		pDialog = new ProgressDialog(ActivityBinding.this);
		pDialog.setTitle("检查绑定状态");
		pDialog.setMessage("请稍候...");
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.show();
		*/
		
		
		bindingIn.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				StringBuffer sBindingBuffer=new StringBuffer();
				sBindingBuffer=sBuffer;
				sBindingBuffer=sBindingBuffer.delete(sBindingBuffer.lastIndexOf(",")+1, sBindingBuffer.length());
				sBindingBuffer=sBindingBuffer.append("binding");
				Log.i(TAG, sBindingBuffer.toString());
				if (Binding(sBindingBuffer)) {
					bindingResultTextView.setText("已绑定");
					bindingIn.setVisibility(View.INVISIBLE);
					bindingOut.setVisibility(View.VISIBLE);
					Toast.makeText(ActivityBinding.this, "绑定成功", Toast.LENGTH_LONG).show();
				} else {
					bindingResultTextView.setText("未绑定");
					bindingOut.setVisibility(View.INVISIBLE);
					bindingIn.setVisibility(View.VISIBLE);
					Toast.makeText(ActivityBinding.this, "绑定失败，请检查网络是否通畅", Toast.LENGTH_LONG).show();
				}
			}
			
		});
		
		bindingOut.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				new AlertDialog.Builder(ActivityBinding.this).setTitle("确认解除绑定吗？")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						StringBuffer sUnBindingBuffer=new StringBuffer();
						sUnBindingBuffer=sBuffer;
						sUnBindingBuffer=sUnBindingBuffer.delete(sUnBindingBuffer.lastIndexOf(",")+1, sUnBindingBuffer.length());
						sUnBindingBuffer=sUnBindingBuffer.append("unbinding");
						Log.i(TAG, sUnBindingBuffer.toString());
						if (!UnBinding(sUnBindingBuffer)) {
							bindingResultTextView.setText("已绑定");
							bindingIn.setVisibility(View.INVISIBLE);
							bindingOut.setVisibility(View.VISIBLE);
							Toast.makeText(ActivityBinding.this, "解除绑定失败，请检查网络是否通畅", Toast.LENGTH_LONG).show();
						} else {
							bindingResultTextView.setText("未绑定");
							bindingOut.setVisibility(View.INVISIBLE);
							bindingIn.setVisibility(View.VISIBLE);
							Toast.makeText(ActivityBinding.this, "解除绑定成功", Toast.LENGTH_LONG).show();
						}
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
		
		StringBuffer sQueryBuffer=new StringBuffer();
		sQueryBuffer=sBuffer;
		sQueryBuffer=sQueryBuffer.append("query");
		if (IsBinding(sQueryBuffer)) {
			bindingResultTextView.setText("已绑定");
			bindingIn.setVisibility(View.INVISIBLE);
		} else {
			bindingResultTextView.setText("未绑定");
			bindingOut.setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			
		}
		
	}

	private Boolean IsBinding(StringBuffer sBuffer){
		String resultString="";
		Binding binding=new Binding();
		resultString=binding.connectServer(sBuffer);
		Log.i(TAG, resultString);
		if (resultString.equals("Binding")) {
			return true;
		} else {
			return false;
		}
	}
	
	private Boolean UnBinding(StringBuffer sBuffer){
		String resultString="";
		Binding binding=new Binding();
		resultString=binding.connectServer(sBuffer);
		Log.i(TAG, resultString);
		if (resultString.equals("UnBinding")) {
			return true;
		} else {
			return false;
		}
	}
	private Boolean Binding(StringBuffer sBuffer){
		String resultString="";
		Binding binding=new Binding();
		resultString=binding.connectServer(sBuffer);
		Log.i(TAG, resultString);
		if (resultString.equals("Binding")) {
			return true;
		} else {
			return false;
		}
	}
}
