package cn.sunxianlei.location.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;

import cn.sunxianlei.location.utils.Util;

public class UpdateManager {
	private static String TAG="UpdateManager";
	
	private Context context;
	public ProgressDialog pDialog;
	private Handler handler=new Handler();
	private int newVerCode=0;
	private String newVerName="";
	public UpdateManager(Context context) {
		// TODO Auto-generated constructor stub
		this.context=context;
	}
	
	public void checkUpdate(){
		if (getServerVerCode()) {
	         int vercode = UpdateConfig.getVerCode(context); // �õ�ǰ���һ��д�ķ���
	         if (newVerCode > vercode) {
	             doNewVersionUpdate(); // �����°汾
	         } else {
	             notNewVersionShow(); // ��ʾ��ǰΪ���°汾
	         }
	     }        
	}
	
	void down(){
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				pDialog.cancel();
				update();
			}
		});
	}
	
	private boolean getServerVerCode() {
	try {
		String verjson = NetworkTool.getContent(Util.UPDATE_SERVER+ Util.UPDATE_VERJSON);
		JSONArray array = new JSONArray(verjson);
		if (array.length() > 0) {
			JSONObject obj = array.getJSONObject(0);
			try {
				newVerCode = Integer.parseInt(obj.getString("verCode"));
				newVerName = obj.getString("verName");
			} catch (Exception e) {
				newVerCode = -1;
				newVerName = "";
				return false;
			}
		}
	} catch (Exception e) {
		Log.e(TAG, e.getMessage());
		return false;
	}
	return true;
	}

	private void notNewVersionShow() {
		int verCode = UpdateConfig.getVerCode(context);
		String verName = UpdateConfig.getVerName(context);
		StringBuffer sb = new StringBuffer();
		sb.append("��ǰ�汾:");
		sb.append(verName);
		//sb.append(" Code:");
		//sb.append(verCode);
		sb.append(",\n�������°�,�������!");
		Dialog dialog = new AlertDialog.Builder(context).setTitle("�������")
				.setMessage(sb.toString())// ��������
				.setPositiveButton("ȷ��",// ����ȷ����ť
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								//pDialog.cancel();
							}
						}).create();// ����
		// ��ʾ�Ի���
		dialog.show();
	}
	private void doNewVersionUpdate() {
		int verCode = UpdateConfig.getVerCode(context);
		String verName = UpdateConfig.getVerName(context);
		StringBuffer sb = new StringBuffer();
		sb.append("��ǰ�汾:");
		sb.append(verName);
		//sb.append(" Code:");
		//sb.append(verCode);
		sb.append(", �����°汾:");
		sb.append(newVerName);
		//sb.append(" Code:");
		//sb.append(newVerCode);
		sb.append(", �Ƿ����?");
		Dialog dialog = new AlertDialog.Builder(context)
			.setTitle("�������")
			.setMessage(sb.toString())
			// ��������
			.setPositiveButton("����",// ����ȷ����ť
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						pDialog = new ProgressDialog(context);
						pDialog.setTitle("��������");
						pDialog.setMessage("���Ժ�...");
						pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						downFile(Util.UPDATE_SERVER + Util.UPDATE_APKNAME);
					}
				})
			.setNegativeButton("�ݲ�����",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton) {
						// ���"ȡ��"��ť֮���˳�����
						//pDialog.cancel();
					}
				}).create();// ����
		// ��ʾ�Ի���
		dialog.show();
	}
	
	void downFile(final String url) {
		pDialog.show();
		new Thread() {
		    public void run() {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			HttpResponse response;
			try {
			    response = client.execute(get);
			    HttpEntity entity = response.getEntity();
			    long length = entity.getContentLength();
			    InputStream is = entity.getContent();
			    FileOutputStream fileOutputStream = null;
			    if (is != null) {
				File file = new File(
					Environment.getExternalStorageDirectory(),
					Util.UPDATE_SAVENAME);
				fileOutputStream = new FileOutputStream(file);
				byte[] buf = new byte[1024];
				int ch = -1;
				int count = 0;
				while ((ch = is.read(buf)) != -1) {
				    fileOutputStream.write(buf, 0, ch);
				    count += ch;
				    if (length > 0) {
				    }
				}
			    }
			    fileOutputStream.flush();
			    if (fileOutputStream != null) {
				fileOutputStream.close();
			    }
			    down();
			} catch (ClientProtocolException e) {
			    e.printStackTrace();
			} catch (IOException e) {
			    e.printStackTrace();
			}
		    }
		}.start();
		}
	
	void update() {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), Util.UPDATE_SAVENAME)),"application/vnd.android.package-archive");
        context.startActivity(intent);
}
}
