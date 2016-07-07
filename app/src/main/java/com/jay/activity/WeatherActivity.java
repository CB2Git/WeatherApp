package com.jay.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jay.bean.County;
import com.jay.bean.UseRecord;
import com.jay.bean.weather.DailyForecast;
import com.jay.bean.weather.WeatherBean;
import com.jay.dao.CountyDB;
import com.jay.dao.UseRecordDB;
import com.jay.fragment.WeatherContentFragment;
import com.jay.fragment.WeatherContentFragment.UpdateListener;
import com.jay.locationselect.R;
import com.jay.util.TimeUtil;

@SuppressLint("DefaultLocale")
public class WeatherActivity extends FragmentActivity implements
		OnClickListener, UpdateListener {
	// 添加城市按钮
	private ImageView mAddCounty;
	// 刷新天气按钮
	private ImageView mFlushWeather;
	// 刷新天气动画
	private ObjectAnimator mFlushAnimator;
	// 天气pager
	private ViewPager mWeatherPager;
	// 天气Pager适配器
	private MyFragmentPagerAdapter myFragmentPagerAdapter;
	// 天气未来几天预报的ListView
	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_layout);
		initView();
		loadDate();
	}

	/**
	 * 加载预先的天气信息
	 */
	private void loadDate() {
		ArrayList<UseRecord> arrayList = UseRecordDB.getAll(this);
		if (arrayList == null || arrayList.size() <= 0) {
			startAddCountyActivity();
			return;
		}
		for (UseRecord record : arrayList) {
			County county = CountyDB.getCountyById(this, record.getCounty_no());
			myFragmentPagerAdapter.addFragmentByCounty(county);
		}
	}

	/**
	 * 初始化所有view，并添加事件监听
	 */
	private void initView() {
		mAddCounty = (ImageView) findViewById(R.id.addCounty);
		mAddCounty.setOnClickListener(this);
		mFlushWeather = (ImageView) findViewById(R.id.flushWeather);
		mFlushWeather.setOnClickListener(this);
		// 初始化ViewPager并设置适配器
		mWeatherPager = (ViewPager) findViewById(R.id.weatherPager);
		// 初始化适配器
		myFragmentPagerAdapter = new MyFragmentPagerAdapter(
				getSupportFragmentManager());
		// 设置适配器
		mWeatherPager.setAdapter(myFragmentPagerAdapter);
		// 设置ViewPager滑动监听
		mWeatherPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int positon) {
				WeatherContentFragment fragment = (WeatherContentFragment) myFragmentPagerAdapter
						.getItem(positon);
				WeatherBean mWeatherBean = fragment.getWeatherBean();
				setWeatherDetail(mWeatherBean);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		// 初始化下面的ListView显示详细信息
		mListView = (ListView) findViewById(R.id.below_detail);
	}

	/**
	 * Fragment开始更新天气的时候回调
	 */
	@Override
	public synchronized boolean startUpdate(boolean isflush) {
		// 如果没有初始化天气动画，那么初始化
		if (mFlushAnimator == null) {
			mFlushAnimator = ObjectAnimator.ofFloat(mFlushWeather, "Rotation",
					0, 360);
			mFlushAnimator.setDuration(600);
			mFlushAnimator.setRepeatCount(Animation.INFINITE);
			// 添加线性插值器则没有停顿
			mFlushAnimator.setInterpolator(new LinearInterpolator());
			mFlushAnimator.setRepeatMode(Animation.RESTART);
		}
		// 如果正在更新，则旋转
		if (isflush) {
			mFlushAnimator.start();
		}
		return isflush;
	}

	/**
	 * Fragment天气更新完毕
	 */
	@Override
	public synchronized void endUpdate() {
		Log.v("x", "天气更新完毕");
		mFlushAnimator.cancel();
	}

	/**
	 * Fragment天气更新失败
	 */
	@Override
	public void onError(int status, String responseString, Exception e) {
		Toast.makeText(this, "获取天气信息失败！", Toast.LENGTH_SHORT).show();
		Log.v("x", "activity 回调 更新失败");
	}

	/**
	 * Fragment天气更新成功
	 */
	@Override
	public void onSuccess(int status, String responseString) {
		Log.v("x", "activity 回调 更新成功");
		WeatherBean weatherBean = new Gson().fromJson(responseString,
				WeatherBean.class);
		// 存储当前的天气信息
		UseRecord uRecord = new UseRecord();
		int position = mWeatherPager.getCurrentItem();
		WeatherContentFragment contentFragment = (WeatherContentFragment) myFragmentPagerAdapter
				.getItem(position);
		County county = contentFragment.getCounty();
		uRecord.setCounty_no(county.getId());
		uRecord.setWeather(responseString);
		uRecord.setUpdate_time(TimeUtil.getCurrentTime());
		UseRecordDB.saveUseRecord(this, uRecord);
		setWeatherDetail(weatherBean);
	}

	/**
	 * 设置天气未来几天预报信息
	 */
	private void setWeatherDetail(WeatherBean weatherBean) {
		if (weatherBean == null)
			return;
		ArrayList<Map<String, String>> dateList = new ArrayList<Map<String, String>>();
		List<DailyForecast> forecast = weatherBean.getHeweather_data_service()
				.get(0).getDailyForecast();
		String date = null;
		String cond = null;
		int minTemp = 0;
		int maxTemp = 0;
		String temp = "";
		// 加载数据
		for (DailyForecast f : forecast) {
			Map<String, String> map = new HashMap<String, String>();
			date = f.getDate();
			cond = f.getCond().getTxt_d();
			maxTemp = Integer.parseInt(f.getTmp().getMax());
			minTemp = Integer.parseInt(f.getTmp().getMin());
			temp = String.format("%d~%d℃", minTemp, maxTemp);
			date = date.replace("-", "/");
			map.put("date", date);
			map.put("cond", cond);
			map.put("temp", temp);
			dateList.add(map);
		}

		SimpleAdapter simpleAdapter = new SimpleAdapter(this, dateList,
				R.layout.weather_buttom_detail, new String[] { "date", "cond",
						"temp" }, new int[] { R.id.forcast_date,
						R.id.forcast_weather, R.id.forcast_temp });
		mListView.setAdapter(simpleAdapter);
	}

	/**
	 * 处理点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addCounty:
			startAddCountyActivity();
			break;
		case R.id.flushWeather:
			if (mFlushAnimator != null && mFlushAnimator.isRunning())
				Toast.makeText(this, "正在获取天气信息", Toast.LENGTH_SHORT).show();
			else {
				int item = mWeatherPager.getCurrentItem();
				WeatherContentFragment fragment = (WeatherContentFragment) myFragmentPagerAdapter
						.getItem(item);
				fragment.updateWeather();
			}
			break;
		}
	}

	/**
	 * 跳转到添加城市Activity
	 */
	private void startAddCountyActivity() {
		Intent intent = new Intent(this, AddressSelect.class);
		startActivityForResult(intent, AddressSelect.REQUEST_ADDR);
	}

	/**
	 * 接收添加城市Activity返回的城市信息
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 成功获取到数据，则添加一个Fragment去显示信息
		if (requestCode == AddressSelect.REQUEST_ADDR
				&& resultCode == AddressSelect.REQUEST_OK) {
			County county = (County) data.getSerializableExtra("county");
			int nowPosition = myFragmentPagerAdapter
					.addFragmentByCounty(county);
			// 滑动到最新添加的Fragment
			mWeatherPager.setCurrentItem(nowPosition, true);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
		private ArrayList<Fragment> mFragmentList;

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
			mFragmentList = new ArrayList<Fragment>();
		}

		@Override
		public Fragment getItem(int postion) {
			return mFragmentList.get(postion);
		}

		@Override
		public int getCount() {
			return mFragmentList.size();
		}

		public void addFragment(Fragment fragment) {
			mFragmentList.add(fragment);
			notifyDataSetChanged();
		}

		public int addFragmentByCounty(County county) {
			for (int i = 0; i < mFragmentList.size(); i++) {
				County county2 = ((WeatherContentFragment) mFragmentList.get(i))
						.getCounty();
				if (county2 != null && county2.getNo().equals(county.getNo())) {
					return i;
				}
			}
			WeatherContentFragment fragment = new WeatherContentFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable("county", county);
			fragment.setArguments(bundle);
			addFragment(fragment);
			return mFragmentList.size() - 1;
		}

		public void removeFragment(Fragment fragment) {
			mFragmentList.remove(fragment);
			notifyDataSetChanged();
		}
	}
}
