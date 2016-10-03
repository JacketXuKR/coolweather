package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	private int level = LEVEL_PROVINCE;
	public static final int TYPE_PROVINCE = 3;
	public static final int TYPE_CITY = 4;
	public static final int TYPE_COUNTY = 5;
	
	private TextView textView;//1
	private ListView listView;//1
	private ArrayAdapter<String> adapter;//1
	private List<String> dataList = new ArrayList<String>();
	private ProgressDialog progress;
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	private Province selectProvince;
	private City selectCity;
	private CoolWeatherDB coolWeatherDB;
	private County selectCounty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		textView = (TextView) findViewById(R.id.text_view);
		listView = (ListView) findViewById(R.id.list_view);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (level == LEVEL_PROVINCE) {
					selectProvince = provinceList.get(position);
					queryCities();
				} else if (level == LEVEL_CITY) {
					selectCity = cityList.get(position);
					queryCounties();
				}
			}
		});
		// 列出省
		queryProvinces();
	}

	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvince_name());
			}
			// //////////////////////////
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			// //////////////////////////
			textView.setText("中国");
			level = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, TYPE_PROVINCE);
		}
	}

	private void queryCities() {
		cityList = coolWeatherDB.loadCities(selectProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCity_name());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText(selectProvince.getProvince_name());
			level = LEVEL_CITY;//
		} else {
			queryFromServer(selectProvince.getProvince_code(), TYPE_CITY);
		}

	}

	private void queryCounties() {
		countyList = coolWeatherDB.loadCounties(selectCity.getId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCounty_name());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			textView.setText(selectCity.getCity_name());
			level = LEVEL_COUNTY;
		} else {
			queryFromServer(selectCity.getCity_code(), TYPE_COUNTY);
		}
	}

	private void queryFromServer(String code, final int type) {
		// progress工作
		showProgressDialog();
		String address;
		if (code != null) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		HttpUtil.sendsHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				boolean result = false;
				if (type == TYPE_PROVINCE) {
					result = Utility.handleProvincesResponse(response,
							coolWeatherDB);
				} else if (type == TYPE_CITY) {
					result = Utility.handleCitiesResponse(response,
							selectProvince.getId(), coolWeatherDB);
				} else if (type == TYPE_COUNTY) {
					result = Utility.handleCountiesResponse(response,
							selectCity.getId(), coolWeatherDB);
				}
				if(result) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeProgressDialog();
							if(type == TYPE_PROVINCE) {
								queryProvinces();
							} else if(type == TYPE_CITY) {
								queryCities();
							} else if(type == TYPE_COUNTY) {
								queryCounties();
							}
						}
					});
					
				} else {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeProgressDialog();
							// 能读取到,只是读取为空
							Toast.makeText(ChooseAreaActivity.this, "读取错误",
									Toast.LENGTH_SHORT).show();
						}
					});
				}
 			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// 连接不到
						Toast.makeText(ChooseAreaActivity.this, "连接服务器出错",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	/**
	 * showProgressDialog();
	 */
	private void showProgressDialog() {
		if(progress == null) {
			progress = new ProgressDialog(this);
			progress.setMessage("正在加载...");
			progress.setCancelable(false);
		}
		progress.show();
	}
	/**
	 * closeProgressDialog();
	 */
	private void closeProgressDialog() {
		if(progress != null) {
			progress.dismiss();
		}
	}
	@Override
	public void onBackPressed() {
		if(level == LEVEL_COUNTY) {
			queryCities();
		} else if(level == LEVEL_CITY) {
			queryProvinces();
		} else {
			finish();
		}
	}
}
