package com.coolweather.app.util;

import android.text.TextUtils;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

/**
 * 功能:分割Province,City,County从服务器获取到的字符信息并将信息存入CoolWeatherDB中.
 * @author Administrator
 *
 */
public class Utility {
	public synchronized static boolean handleProvincesResponse(String response, CoolWeatherDB coolWeatherDB) {
		if(!TextUtils.isEmpty(response)){
			String[] provincesInfo = response.split(",");
			if(response != null && provincesInfo.length > 0) {//response != null
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
	public synchronized static boolean handleCitiesResponse(String response, int provinceId, CoolWeatherDB coolWeatherDB) {
		if(!TextUtils.isEmpty(response)) {
			String[] citiesInfo = response.split(",");
			if(response != null && citiesInfo.length > 0) {
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
	public synchronized static boolean handleCountiesResponse(String response, int cityId, CoolWeatherDB coolWeatherDB) {
		if(!TextUtils.isEmpty(response)) {
			String[] countiesInfo = response.split(",");
			if(response != null && countiesInfo.length > 0) {
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
}














