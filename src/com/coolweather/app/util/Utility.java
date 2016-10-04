package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

/**
 * 功能:分割Province,City,County从服务器获取到的字符信息并将信息存入CoolWeatherDB中.
 * 
 * @author Administrator
 * 
 */
public class Utility {

	public synchronized static boolean handleProvincesResponse(String response,
			CoolWeatherDB coolWeatherDB) {
		if (!TextUtils.isEmpty(response)) {
			String[] provincesInfo = response.split(",");
			if (response != null && provincesInfo.length > 0) {// response !=
																// null
				for (String provinceInfo : provincesInfo) {
					String[] provinceCodeAndName = provinceInfo.split("\\|");//
					Province province = new Province();
					province.setProvince_code(provinceCodeAndName[0]);
					province.setProvince_name(provinceCodeAndName[1]);
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	public synchronized static boolean handleCitiesResponse(String response,
			int provinceId, CoolWeatherDB coolWeatherDB) {
		if (!TextUtils.isEmpty(response)) {
			String[] citiesInfo = response.split(",");
			if (response != null && citiesInfo.length > 0) {
				for (String cityInfo : citiesInfo) {
					String[] cityCodeAndName = cityInfo.split("\\|");
					City city = new City();
					city.setCity_code(cityCodeAndName[0]);
					city.setCity_name(cityCodeAndName[1]);
					city.setProvince_id(provinceId);
					coolWeatherDB.saveCity(city);
				}
				return true;
			}

		}
		return false;
	}

	public synchronized static boolean handleCountiesResponse(String response,
			int cityId, CoolWeatherDB coolWeatherDB) {
		if (!TextUtils.isEmpty(response)) {
			String[] countiesInfo = response.split(",");
			if (response != null && countiesInfo.length > 0) {
				for (String countyInfo : countiesInfo) {
					String[] countyCodeAndName = countyInfo.split("\\|");
					County county = new County();
					county.setCounty_code(countyCodeAndName[0]);
					county.setCounty_name(countyCodeAndName[1]);
					county.setCity_id(cityId);
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}

	public synchronized static void handleWeatherResponse(Context context,
			String response) {
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String ptime = weatherInfo.getString("ptime");
			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, ptime);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private static void saveWeatherInfo(Context context, String cityName, String weatherCode,
			String temp1, String temp2, String weatherDesp, String ptime) {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putString("cityName", cityName);
		editor.putString("weatherCode", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weatherDesp", weatherDesp);
		editor.putString("ptime", ptime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
		editor.putString("currentDate", sdf.format(new Date()));
		editor.commit();
	}
}
