package cn.sunxianlei.location;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.jar.JarEntry;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import cn.sunxianlei.location.activity.ActivityMain;
import cn.sunxianlei.location.utils.Distance;
import cn.sunxianlei.location.utils.Util;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import android.R;
import android.R.integer;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.StrictMode;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service {

	private static final String TAG="LocationService";//日志标签
	private LocationPreferences preferences;//读取程序配置信息类
	private int DELAY_TIME=10*1000;//间隔时间单位毫秒
	private LocationClient mLocationClient;//定位SDK核心类
	private MyLocationListenner mLocationListenner;//定位结果处理类
	private String IMEI;//手机序列号
	private BDLocation mCurLocation=null;//存取上一次的位置点
	private double POSITION_MOVE;//默认位置移动距离
	private double POSITION_RADIUS;
	Timer timer;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
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
		
		preferences=new LocationPreferences(this);
		
		String gpsscantime=preferences.getGPSScanTime();
		int gpsscantimeint=Integer.parseInt(gpsscantime);
		
		String distanceString=preferences.getDistance();
		POSITION_MOVE=Double.parseDouble(distanceString);
		
		String radiusString=preferences.getRadius();
		POSITION_RADIUS=Double.parseDouble(radiusString);
		
		initLocationService();
		
		timer=new Timer();
		timer.schedule(new MyTask(), 0, gpsscantimeint*60*1000);
		Log.i(TAG,"oncreated");
	}
	
	class MyTask extends java.util.TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(mLocationClient.isStarted()){
				mLocationClient.stop();
			}
			mLocationClient.start();
		}
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mLocationClient.stop();
		timer.cancel();
		Log.i(TAG,"ondestoryed");
		//Intent intent=new Intent(getApplicationContext(),LocationService.class);
		//this.startService(intent);
	}
	private void initLocationService(){
		mLocationClient=new LocationClient(this.getApplicationContext());
		mLocationClient.setAK(Util.BAIDU_AK);//key for sdk for sunxialei
		mLocationListenner=new MyLocationListenner();
		mLocationClient.registerLocationListener(mLocationListenner);
		
		LocationClientOption locationOption=new LocationClientOption();
		locationOption.setCoorType("bd09ll");
		locationOption.disableCache(true);

		if(preferences.isUsedGPSEnabled())
		{
			locationOption.setOpenGps(true);
			locationOption.setPriority(LocationClientOption.GpsFirst);
		}else {
			locationOption.setPriority(LocationClientOption.NetWorkFirst);
		}

		locationOption.setScanSpan(DELAY_TIME);
		locationOption.setProdName(Util.BAIDU_PRODNAME);
		
		mLocationClient.setLocOption(locationOption);
		Log.i(TAG,"initLocationServiced");
		//Log.i(TAG,String.valueOf(DELAY_TIME));
		//Log.i(TAG, String.valueOf(preferences.isUsedGPSEnabled()));
	}
	
	private String GetIMEI(){
		TelephonyManager tmManager=(TelephonyManager)this.getSystemService(TELEPHONY_SERVICE);
		String imei=tmManager.getDeviceId();
		return imei;
	}
	
	class MyLocationListenner implements BDLocationListener {
		@Override
		
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return ;
			if(checkLocationReasonable(location)){//检查位置合理性
				if (preferences.isUsedGPSEnabled()) {//检查是否开启GPS设置
					if (checkLocationRadius(location)) {//在开GPS的前提下才有检查精度的意义
						if (mCurLocation==null) {
							mCurLocation=location;
							
							IMEI=GetIMEI();
							StringBuffer sb = new StringBuffer();
							
							sb.append(location.getLongitude());
							sb.append(",");
							sb.append(location.getLatitude());
							sb.append(",");
							sb.append(location.getRadius());
							sb.append(",");
							sb.append(location.getTime());
							sb.append(",");
							sb.append(IMEI);
							sendMessageToServer(sb);
							mLocationClient.stop();
						}else {
							if (checkPositionChange(mCurLocation,location)) {
								IMEI=GetIMEI();
								StringBuffer sb = new StringBuffer();
								
								sb.append(location.getLongitude());
								sb.append(",");
								sb.append(location.getLatitude());
								sb.append(",");
								sb.append(location.getRadius());
								sb.append(",");
								sb.append(location.getTime());
								sb.append(",");
								sb.append(IMEI);
								
								sendMessageToServer(sb);
								mCurLocation=location;
								mLocationClient.stop();
							}else {
								mCurLocation=location;
								mLocationClient.stop();
							}
						}
					}else {
						mLocationClient.stop();
					}
				} else {//没有开启GPS则跳过检查精度
					if (mCurLocation==null) {
						mCurLocation=location;
						
						IMEI=GetIMEI();
						StringBuffer sb = new StringBuffer();
						
						sb.append(location.getLongitude());
						sb.append(",");
						sb.append(location.getLatitude());
						sb.append(",");
						sb.append(location.getRadius());
						sb.append(",");
						sb.append(location.getTime());
						sb.append(",");
						sb.append(IMEI);
						sendMessageToServer(sb);
						mLocationClient.stop();
					}else {
						if (checkPositionChange(mCurLocation,location)) {
							IMEI=GetIMEI();
							StringBuffer sb = new StringBuffer();
							
							sb.append(location.getLongitude());
							sb.append(",");
							sb.append(location.getLatitude());
							sb.append(",");
							sb.append(location.getRadius());
							sb.append(",");
							sb.append(location.getTime());
							sb.append(",");
							sb.append(IMEI);
							
							sendMessageToServer(sb);
							mCurLocation=location;
							mLocationClient.stop();
						}else {
							mCurLocation=location;
							mLocationClient.stop();
						}
					}
				}
				
			}
		}
		

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null){
				return ; 
			}
		}
		
	}
	
	private boolean checkLocationReasonable(BDLocation location) {
		// TODO Auto-generated method stub
		if (location.getLatitude()<1) {
			return false;
		}
		if (location.getLongitude()<1) {
			return false;
		}
		if(location.getLatitude()>180){
			return false;
		}
		if(location.getLongitude()>180){
			return false;
		}
		return true;
	}
	private boolean checkLocationRadius(BDLocation location){
		if(location.getRadius()>POSITION_RADIUS){
			return false;
		}
		return true;
	}

	private boolean checkPositionChange(BDLocation mCurLocation,
			BDLocation location) {
		// TODO Auto-generated method stub
		double distance=Distance.GetDistance(mCurLocation.getLongitude(),mCurLocation.getLatitude(),location.getLongitude(),location.getLatitude());
		if (distance>POSITION_MOVE) {
			return true;
		}
		return false;
	}
	private void sendMessageToServer(StringBuffer sb){
		try {
			Log.i(TAG,"sendMessageToServerStart");
			HttpClient httpclient = new DefaultHttpClient(); 
			HttpPost httppost = new HttpPost(Util.POSTURL); 
			
			String resultString;
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(); 
			nameValuePairs.add(new BasicNameValuePair("locationdata", sb.toString())); 
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
			
			Log.i(TAG,"sendMessageToresponseStart");
			HttpResponse response; 
			response=httpclient.execute(httppost);  
			Log.i(TAG,"sendMessageToresponseStop");
			
			if (response.getStatusLine().getStatusCode()==200) {
				resultString=EntityUtils.toString(response.getEntity());
			}
			else {
				resultString=String.valueOf(response.getStatusLine().getStatusCode());
			}
			Log.i(TAG,"succeed to send data");
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.i(TAG, "failed to connect server");
		}
	}
	
	public static boolean isRunning(Context context){
		ActivityManager activityManager=(ActivityManager)context.getSystemService(ACTIVITY_SERVICE);
		List<RunningServiceInfo>serviceInfos=activityManager.getRunningServices(Integer.MAX_VALUE);
		
		for(RunningServiceInfo serviceInfo:serviceInfos){
			ComponentName componentName=serviceInfo.service;
			String serviceName=componentName.getClassName();
			if(serviceName.equals(LocationService.class.getName())){
				return true;
			}
		}
		return false;
	}

	public static void stop(Context context) {
		// TODO Auto-generated method stub
		context.stopService(new Intent(context,LocationService.class));
	}

	public static void start(Context context) {
		// TODO Auto-generated method stub
		context.startService(new Intent(context,LocationService.class));
	}
}



