package cn.sunxianlei.location.update;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class NetworkTool {
	
	public static String getContent(String url)throws Exception{
		StringBuilder sb=new StringBuilder();
		HttpClient client=new DefaultHttpClient();
		HttpParams httpParams=client.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
		HttpConnectionParams.setSoTimeout(httpParams, 5000);
		HttpResponse response=client.execute(new HttpGet(url));
		HttpEntity entity=response.getEntity();
		if(entity!=null){
			BufferedReader reader=new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"), 8192);
			String lineString=null;
			while((lineString=reader.readLine())!=null){
				sb.append(lineString+"\n");
			}
			reader.close();
		}
		return sb.toString();
	}

}
