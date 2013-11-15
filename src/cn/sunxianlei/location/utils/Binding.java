package cn.sunxianlei.location.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import cn.sunxianlei.location.activity.ActivityBinding;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

public class Binding {
	static final String TAG="BANDING";
	String resultString;
	ProgressDialog pDialog;
	public String connectServer(StringBuffer sb){
		try {
			HttpClient httpclient = new DefaultHttpClient(); 
			HttpPost httppost = new HttpPost(Util.BINDINGURL); 
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(); 
			nameValuePairs.add(new BasicNameValuePair("bindingdata", sb.toString())); 

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 

			HttpResponse response; 
			response=httpclient.execute(httppost); 
			if (response.getStatusLine().getStatusCode()==200) {
				resultString=EntityUtils.toString(response.getEntity());
			}
			Log.i(TAG,"succeed to send data");
			
		} catch (Exception e) {
			// TODO: handle exception
			Log.i(TAG, "failed to connect server");
		}
		return resultString;
	}
}
