package com.coolweather.app.activity;

import com.coolweather.app.R;
import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity {
	private LinearLayout weather_info_layout;
	private TextView city_name;
	private TextView publish_text;
	private TextView current_date;
	private TextView weather_desp;
	private TextView temp1;
	private TextView temp2;
	private Button refresh;
	private String countyCode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		weather_info_layout = (LinearLayout) findViewById(R.id.weather_info_layout);
		city_name = (TextView) findViewById(R.id.city_name);
		publish_text = (TextView) findViewById(R.id.publish_text);
		current_date = (TextView) findViewById(R.id.current_date);
		weather_desp = (TextView) findViewById(R.id.weather_desp);
		temp1 = (TextView) findViewById(R.id.temp1);
		temp2 = (TextView) findViewById(R.id.temp2);
		refresh = (Button) findViewById(R.id.refresh);
		refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				publish_text.setText("同步中...");
				countyCode = getIntent().getStringExtra("countyCode");
				if (!TextUtils.isEmpty(countyCode)) {
					weather_info_layout.setVisibility(View.INVISIBLE);
					city_name.setVisibility(View.INVISIBLE);
					queryWeatherCode(countyCode);
				}
			}
		});
		countyCode = getIntent().getStringExtra("countyCode");
		if (!TextUtils.isEmpty(countyCode)) {
			// 等待
			publish_text.setText("正在同步...");
			weather_info_layout.setVisibility(View.INVISIBLE);
			city_name.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		} /*
		 * else { showWeather(); }
		 */
	}

	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}

	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		queryFromServer(address, "weatherInfo");
	}

	private void showWeather() {
		SharedPreferences pre = PreferenceManager
				.getDefaultSharedPreferences(this);
		city_name.setText(pre.getString("cityName", ""));
		publish_text.setText(pre.getString("ptime", ""));
		current_date.setText(pre.getString("currentDate", ""));
		weather_desp.setText(pre.getString("weatherDesp", ""));
		temp1.setText(pre.getString("temp1", ""));
		temp2.setText(pre.getString("temp2", ""));
		city_name.setVisibility(View.VISIBLE);
		weather_info_layout.setVisibility(View.VISIBLE);
		Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
		startService(intent);
	}

	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendsHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				} else if ("weatherInfo".equals(type)) {
					Utility.handleWeatherResponse(WeatherActivity.this,
							response);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							showWeather();
						}
					});
				}
			}

			@Override
			public void onError(final Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						publish_text.setText("同步失败");
						// weather_info_layout.setVisibility(View.VISIBLE);
						// weather_desp.setTextSize(10);
						// e.printStackTrace();
					}
				});
			}
		});
	}
}
