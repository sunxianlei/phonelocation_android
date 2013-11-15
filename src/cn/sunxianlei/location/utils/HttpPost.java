package cn.sunxianlei.location.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class HttpPost {
	public static final String TAG = "HttpPost";
	URL url = null;
	HttpURLConnection httpurlconnection = null;
	
	String access_token;
	String expires_in;
	String refresh_token;
	String scope;
	String session_key;
	 
	/**
	 * 获取access_token and refresh_token
	 */
	public void getAccessToken(){
		 
		try {
			url = new URL("https://graph.renren.com/oauth/token?");

			httpurlconnection = (HttpURLConnection) url.openConnection();
			httpurlconnection.setDoOutput(true);
			httpurlconnection.setRequestMethod("POST");
			String param1 = "grant_type=authorization_code";
			String param2 = "&code=" + Util.code; 
			String param3 = "&client_id=" + Util.RENREN_API_KEY;
			String param4 = "&client_secret=" + Util.RENREN_SECRET;
			String param5 = "&redirect_uri=http://graph.renren.com/oauth/login_success.html";  // 此参数不能改变，必须和请求code时的回调url�?��
			httpurlconnection.getOutputStream().write(
					(param1 + param2 + param3 + param4 + param5).getBytes());

			httpurlconnection.getOutputStream().flush();
			httpurlconnection.getOutputStream().close();
			int statusCode = httpurlconnection.getResponseCode();
			
			// 状�?码返�?00代表成功
			if (200 == statusCode) {
				try {
					InputStream is = httpurlconnection.getInputStream();
					Reader reader = new BufferedReader(new InputStreamReader(is), 4000);
					//int len =con.getContentLength();   执行这一句时就出�? 把这�?��删掉 程序就顺利运�?
					//如果这个文件是流形式传�?，不能得到其大小�?
					//HTTP header里有content length�?stream 流除�?
					//Log.i(TAG, "httpurlconnection.getResponseMessage()="+httpurlconnection.getResponseMessage());
					StringBuilder buffer = new StringBuilder( );
					try {
						char[] tmp = new char[1024];
						int l;
						while ((l = reader.read(tmp)) != -1) {
							buffer.append(tmp, 0, l);
						}
					} catch(Exception e){
						e.printStackTrace();
					}finally {
						reader.close();
					}
					String string = buffer.toString();
					JSONObject jb = new JSONObject(string);
					access_token = jb.getString("access_token");
					expires_in = jb.getString("expires_in"); 
				    refresh_token = jb.getString("refresh_token");
					//scope = jb.getString("scope");  // org.json.JSONException: No value for scope
 
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	 
			
			Util.access_token = access_token;
			Util.expires_in = expires_in;
			Util.refresh_token = refresh_token;
			
			Log.i(TAG, "access_token = " + Util.access_token);
			//access_token = 147396|6.b1a5beab94fc6a1efdd8dcde388e6b5c.2592000.1315638000-244248724
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (httpurlconnection != null)
				httpurlconnection.disconnect();
		}

		getSessionKey();
	}

	/**
	 * 获取session_key
	 */
	public void getSessionKey() {
		try {
			url = new URL("https://graph.renren.com/renren_api/session_key?");

			httpurlconnection = (HttpURLConnection) url.openConnection();
			httpurlconnection.setDoOutput(true);
			httpurlconnection.setRequestMethod("POST");
			String param1 = "oauth_token=" + Util.access_token;   //建议对oauth_token进行url编码后再调用

			httpurlconnection.getOutputStream().write(param1.getBytes());
			httpurlconnection.getOutputStream().flush();
			httpurlconnection.getOutputStream().close();
			int statusCode = httpurlconnection.getResponseCode();
			
			
			
			// 状�?码返�?00代表成功
			if (200 == statusCode) {
				try {
					InputStream is = httpurlconnection.getInputStream();
					Reader reader = new BufferedReader(new InputStreamReader(is), 4000);
					//int len =con.getContentLength();   执行这一句时就出�? 把这�?��删掉 程序就顺利运�?
					//如果这个文件是流形式传�?，不能得到其大小�?
					//HTTP header里有content length�?stream 流除�?
					//Log.i(TAG, "httpurlconnection.getResponseMessage()="+httpurlconnection.getResponseMessage());
					StringBuilder buffer = new StringBuilder( );
					try {
						char[] tmp = new char[1024];
						int l;
						while ((l = reader.read(tmp)) != -1) {
							buffer.append(tmp, 0, l);
						}
					} catch(Exception e){
						e.printStackTrace();
					}finally {
						reader.close();
					}
					String string = buffer.toString();
					JSONObject value = new JSONObject(string);
					// 先获取数组中的三个大的�?�?
					String renrenToken = value.getString("renren_token");
					String oauth_token = value.getString("oauth_token");
					String userToken = value.getString("user");
					// 解析第一个renren_token,并获取其中的三个�?
					JSONObject renren_token = new JSONObject(renrenToken);
					session_key = renren_token.getString("session_key");
					String expires_in = renren_token.getString("expires_in");
					String session_secret = renren_token.getString("session_secret");
					//第二个不用解析，不是数组
					//第三个，并获取userId
					JSONObject user = new JSONObject(userToken);
					int userId = user.getInt("id");
					//Log.i(TAG, ""+userToken+"   "+user+"   "+userId);
					Util.userid = userId;
					Log.i(TAG,Integer.toString(userId));
					Util.SESSION_KEY = session_key;
					
//              这里不能用数组进行解析，原因是第二个参数不是数组，如下所示：
//				    {
//						"renren_token":{
//							"session_secret":"6710946aed8cd28b97333f2109bb68e9",
//							"expires_in":2594255,
//							"session_key":"6.b1a5beab94fc6a1efdd8dcde388e6b5c.2592000.1315638000-244248724"
//						},
//						"oauth_token":"147396|6.b1a5beab94fc6a1efdd8dcde388e6b5c.2592000.1315638000-244248724",		
//						"user":{
//							"id":244248724
//							}
//					}
//					JSONArray data = new JSONArray(string);
//					for(int i = 0; i < data.length(); i++){
//						JSONObject renren_token = data.getJSONObject(i);
//						Log.i(TAG, "renren_token = "+renren_token.toString());
//						if(renren_token != null){ 
//							String session_secret = renren_token.getString("session_secret");
//							String expires_in = renren_token.getString("expires_in");
//							String session_key = renren_token.getString("session_key");
//							Log.i(TAG, "验证�? + session_secret+"   "+expires_in+"   "+session_key);
//						} 
//					} 
				 
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (httpurlconnection != null){
				httpurlconnection.disconnect(); 
			} 
		}
		
		
	}
	
	
	/**
	 * 用户每次登陆时，使用refresh_token刷新access_token,使用户保持长期登陆状�?
	 */
	public void useRefreshTokenRefreshAccessToken () {
		try {
			url = new URL("https://graph.renren.com/oauth/token?");

			httpurlconnection = (HttpURLConnection) url.openConnection();
			httpurlconnection.setDoOutput(true);
			httpurlconnection.setRequestMethod("POST");
			String grant_type = "refresh_token";
			String refresh_token = Util.refresh_token;
			String client_id = Util.RENREN_API_KEY;
			String client_secret = Util.RENREN_SECRET;
			String param = "grant_type=refresh_token&" + "refresh_token=" + Util.refresh_token + 
						"&client_id=" + client_id + "&client_secret=" + client_secret;
			
			Log.i(TAG, param);
			httpurlconnection.getOutputStream().write(param.getBytes());
			httpurlconnection.getOutputStream().flush();
			httpurlconnection.getOutputStream().close();
			int statusCode = httpurlconnection.getResponseCode();
			 
			// 状�?码返�?00代表成功
			if (200 == statusCode) {
				try {
					InputStream is = httpurlconnection.getInputStream();
					Reader reader = new BufferedReader(new InputStreamReader(is), 4000);
					//int len =con.getContentLength();   执行这一句时就出�? 把这�?��删掉 程序就顺利运�?
					//如果这个文件是流形式传�?，不能得到其大小�?
					//HTTP header里有content length�?stream 流除�?
					//Log.i(TAG, "httpurlconnection.getResponseMessage()="+httpurlconnection.getResponseMessage());
					StringBuilder buffer = new StringBuilder( );
					try {
						char[] tmp = new char[1024];
						int l;
						while ((l = reader.read(tmp)) != -1) {
							buffer.append(tmp, 0, l);
						}
					} catch(Exception e){
						e.printStackTrace();
					}finally {
						reader.close();
					}
					String string = buffer.toString(); 
					JSONObject jb = new JSONObject(string);
					access_token = jb.getString("access_token");
					expires_in = jb.getString("expires_in"); 
				    refresh_token = jb.getString("refresh_token");
					//scope = jb.getString("scope");  // org.json.JSONException: No value for scope
				    Log.i(TAG, "access_token = " + access_token);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (httpurlconnection != null){
				httpurlconnection.disconnect(); 
			} 
		}
		
		
	}
}


