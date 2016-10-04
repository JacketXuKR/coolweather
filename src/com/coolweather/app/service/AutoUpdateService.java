package com.coolweather.app.service;

import com.coolweather.app.receiver.AutoUpdateReceiver;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class AutoUpdateService extends Service {
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				updateWeather();
			}
		});
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int anhour = 60 * 60 * 1000;
		long triggerAtTime = SystemClock.elapsedRealtime() + anhour;
		Intent i = new Intent(this, AutoUpdateReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		return super.onStartCommand(intent, flags, startId);
	}
	public void updateWeather() {
		SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this);
		String weatherCode = pre.getString("weatherCode", "");
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		HttpUtil.sendsHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				if(!TextUtils.isEmpty(response)) {
					Utility.handleWeatherResponse(AutoUpdateService.this, response);
					
				}
			}
			
			@Override
			public void onError(Exception e) {
				e.printStackTrace();
			}
		});
	}
}
