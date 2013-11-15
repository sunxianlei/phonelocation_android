package cn.sunxianlei.location.activity;

import cn.sunxianlei.location.utils.Util;
import cn.sunxianlei.phonelocation.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ActivityRenrenWebview extends Activity {
	public static final String TAG="ActivityRenrenWebview";
	WebView webView;
	String urlString="";
	String code;
	ProgressDialog pDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.renren_webview);
		
		webView=(WebView)findViewById(R.id.webviewid);
		
		WebSettings webSettings=webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setCacheMode(webSettings.LOAD_NO_CACHE);
		
		pDialog = new ProgressDialog(ActivityRenrenWebview.this);
		pDialog.setTitle("Õ¯“≥º”‘ÿ÷–");
		pDialog.setMessage("«Î…‘∫Ú...");
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.show();
		
		webView.setWebViewClient(new WebViewClient(){

			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				// TODO Auto-generated method stub
				//super.onReceivedSslError(view, handler, error);
				handler.proceed();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				pDialog.dismiss();
				urlString=webView.getUrl();
				if (urlString!=null) {
					if (urlString.contains("code=")) {
						code=urlString.substring(urlString.indexOf("code=")+5,urlString.length());
						Util.code=code;
						if (Util.code!="") {
							Intent intent=new Intent(ActivityRenrenWebview.this,GetAccessTokenAndSessionKey.class);
                            finish();
							startActivity(intent);
						}
					}
				}
				
			}
			
		});
		
		
		if (Util.renew) {
			webView.loadUrl("https://graph.renren.com/oauth/authorize?x_renew=true&"+
					"client_id=" + Util.RENREN_API_KEY + "&response_type=code"+
					"&display=touch&redirect_uri=http://graph.renren.com/oauth/login_success.html" +
			"&scope=read_user_feed");
		}else {
			webView.loadUrl("https://graph.renren.com/oauth/authorize?"+
					"client_id=" + Util.RENREN_API_KEY + "&response_type=code"+
					"&display=touch&redirect_uri=http://graph.renren.com/oauth/login_success.html" +
			"&scope=read_user_feed");
		}
		
	}
	
	
}
