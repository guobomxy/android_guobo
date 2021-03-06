package com.eastime.paycar.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookiePolicy;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;


import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	private String ip = "";
	private String port = "";
	private String systemscheme = "http";
	
	private String url_ftp = "";
	private String port_ftp = "";
	private String username = "";
	private String password = "";
	private String remotePath = "";
	// constructor
	public JSONParser() {

		setResult();
		
	}
	
	//获取服务端的ip 端口
	private void setResult() {
		
		SharedPreferences settings = AppContext.getInstance().getSharedPreferences("setting", 0);
		ip = settings.getString("ip", "0.0.0.0");
		port = settings.getString("port", "0");
		
		url_ftp = settings.getString("url_ftp", "0.0.0.0");
		port_ftp = settings.getString("port_ftp", "0");
		username = settings.getString("username", "");
		password = settings.getString("password", "");
		remotePath = settings.getString("remotepath", "");

	}

	// function get json from url
	// by making HTTP POST or GET mehtod
	public String makeHttpRequest(String url, String method,
			List<NameValuePair> params) {
		String json = "";
		// Making HTTP request
		try {
			
			// check for request method
			if(method == "POST"){
				// request method is POST
				// defaultHttpClient
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(systemscheme +"://"
						+ip+":"
						+port
						+url);
				httpPost.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				json = EntityUtils.toString(httpEntity);
				//json = URLDecoder.decode(json, "UTF-8");

				//is = httpEntity.getContent();
				
			}else if(method == "GET"){
				// request method is GET
				DefaultHttpClient httpClient = new DefaultHttpClient();
				//HttpClientParams.setCookiePolicy(httpClient.getParams(),CookiePolicy.BROWSER_COMPATIBILITY);
//				String paramString = URLEncodedUtils.format(params, "UTF-8");
//				url += "?" + paramString;
//				HttpGet httpGet = new HttpGet(url);
				

				URI uri = null;
				try {
					uri = URIUtils.createURI(systemscheme,ip+":"+port, -1,url,URLEncodedUtils.format(params, "UTF-8"),null);
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //
				HttpGet httpGet = new HttpGet(uri);
				

				HttpResponse httpResponse = httpClient.execute(httpGet);

				HttpEntity httpEntity = httpResponse.getEntity();
				json = EntityUtils.toString(httpEntity);
				//json = URLDecoder.decode(json, "UTF-8");
				//is = httpEntity.getContent();
			}
			

		} catch (UnsupportedEncodingException e) {
			json="{\"result\":\"error\"}";
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			json="{\"result\":\"error\"}";
			e.printStackTrace();
		} catch (IOException e) {
			json="{\"result\":\"error\"}";
			e.printStackTrace();
		}

		// return JSON String
		return json;

	}
	
	//ftp上传
	public String uploadFTP( String fileNamePath,
			String fileName) {
		String returnMessage = "0";
		
		FTPClient ftpClient = new FTPClient();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(fileNamePath + fileName);
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {

			ftpClient.connect(url_ftp, Integer.parseInt(port_ftp));

			boolean loginResult = ftpClient.login(username, password);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);// 设置上传图片数据的方式
			int returnCode = ftpClient.getReplyCode();
			if (loginResult && FTPReply.isPositiveCompletion(returnCode)) {// 如果登录成功
				ftpClient.makeDirectory(remotePath);
				// 设置上传目录
				ftpClient.changeWorkingDirectory(remotePath);
				ftpClient.setBufferSize(1024);
				ftpClient.setControlEncoding("UTF-8");
				ftpClient.enterLocalPassiveMode();
				
				ftpClient.storeFile(fileName, fis);

				returnMessage = "1"; // 上传成功
			} else {// 如果登录失败
				returnMessage = "0";
			}

		} catch (IOException e) {
			e.printStackTrace();
			returnMessage = "FTP客户端出错";
			//throw new RuntimeException("FTP客户端出错！", e);
		}
		finally {
			try {
				fis.close();
			} catch (IOException e1) {
				Log.i("关闭文件流", "失败");
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}			
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				Log.i("关闭ftp客户端", "失败");
				e.printStackTrace();				
				//throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
		return returnMessage;
	}
	
	
	
}
