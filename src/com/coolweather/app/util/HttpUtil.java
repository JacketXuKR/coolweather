package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 功能:向服务器发送指令,读取数据.
 * @author Administrator
 * 使用子线程,原因:读取服务器信息需要时间,防止主线程阻塞.
 */
public class HttpUtil {
	public static void sendsHttpRequest(final String address, final HttpCallbackListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection connection = null;
				StringBuilder builder = new StringBuilder();//
				try {
					URL url = new URL(address);
					connection  = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream inputStream = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
					//builder = new StringBuilder();
					String line = "";
					while((line = reader.readLine()) != null){
						builder.append(line);
					}
					if(listener != null) {
						listener.onFinish(builder.toString());//由调用参数设置,使用Utility类的方法
					}
				} catch (Exception e) {
					if(listener != null) {
						listener.onError(e);
					}
				} finally {
					if(connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}
}
