package com.jay.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.apistore.sdk.ApiCallBack;
import com.google.gson.Gson;
import com.jay.bean.County;
import com.jay.bean.weather.Basic;
import com.jay.bean.weather.Now;
import com.jay.bean.weather.WeatherBean;
import com.jay.locationselect.R;
import com.jay.util.WeatherUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WeatherContentFragment extends Fragment {
	// 显示的城市信息
	private County mCounty;
	// 获取的天气信息
	private WeatherBean mWeatherBean;
	// 回调接口，用于与Activity进行通信
	private UpdateListener mListener;
	// 根布局
	private View mRootView;
	// 是否正在更新天气
	private boolean mIsFlush = false;
	// 布局控件
	private ImageView mWeatherIcon;
	private TextView mNowTemp;
	private TextView mLocation;
	private TextView mNowWeather;
	private TextView mNowDate;
	private TextView mNowWeek;
	private TextView mLastUpdateTime;
	// 1 2 3 4 5 6 7 转汉字
	private String[] week = new String[] { "日", "一", "二", "三", "四", "五", "六" };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 获取传递进来的城市信息
		if (mCounty == null && getArguments() != null) {
			this.mCounty = (County) getArguments().getSerializable("county");
		}
		// 没有获取天气信息则获取天气
		if (mWeatherBean == null) {
			updateWeather();
		}
		// 第一次显示的时候才实例化布局
		if (mRootView == null) {
			initView(inflater, container);
		}
		return mRootView;
	}

	/**
	 * 更新天气
	 * 
	 * @return 是否正在更新天气
	 */
	public boolean updateWeather() {
		if (mIsFlush == false) {
			mIsFlush = true;
			Log.v("x", "加载天气信息");
			WeatherUtil.getWeather(mCounty, new WeatherCallBack());
		}
		mListener.startUpdate(mIsFlush);
		return mIsFlush;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mListener = (UpdateListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ "must implement UpdateListener");
		}
	}

	/**
	 * 初始化布局
	 */
	private void initView(LayoutInflater inflater, ViewGroup container) {
		mRootView = inflater
				.inflate(R.layout.weather_content, container, false);
		mWeatherIcon = (ImageView) mRootView.findViewById(R.id.weatherIcon);
		mNowTemp = (TextView) mRootView.findViewById(R.id.nowTemp);
		mLocation = (TextView) mRootView.findViewById(R.id.location);
		mNowWeather = (TextView) mRootView.findViewById(R.id.nowWeather);
		mNowDate = (TextView) mRootView.findViewById(R.id.nowDate);
		mNowWeek = (TextView) mRootView.findViewById(R.id.nowWeek);
		mLastUpdateTime = (TextView) mRootView
				.findViewById(R.id.lastUpdateTime);
	}

	public WeatherBean getWeatherBean() {
		return mWeatherBean;
	}

	public County getCounty() {
		return mCounty;
	}

	/**
	 * 将天气信息设置给Fragment
	 * 
	 * @param weatherBean
	 */
	private void setWeatherDate(WeatherBean weatherBean) {
		Now now = weatherBean.getHeweather_data_service().get(0).getNow();
		String picName = "w" + now.getCond().getCode();
		int identifier = getResources().getIdentifier(picName, "drawable",
				getActivity().getCallingPackage());
		mWeatherIcon.setBackgroundResource(identifier);
		mNowTemp.setText(now.getTmp() + "℃");
		mNowWeather.setText(now.getCond().getTxt());
		mLocation.setText(mCounty.getName());

		Basic basic = weatherBean.getHeweather_data_service().get(0).getBasic();
		String loc = basic.getUpdate().getLoc();
		// 2016-03-30 15:52
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm",
				Locale.getDefault());
		Calendar calendar = Calendar.getInstance();
		try {
			Date date = sdfDate.parse(loc);
			calendar.setTime(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int mouth = calendar.get(Calendar.MONTH);
		int dayOfMouth = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minue = calendar.get(Calendar.MINUTE);
		mNowDate.setText(mouth + "月" + dayOfMouth + "日");
		mNowWeek.setText("星期" + week[dayOfWeek]);
		mLastUpdateTime.setText(hour + ":" + minue + "更新");
	}

	@Override
	public void onDestroyView() {
		// 复用已经生成的View，已经被添加到ViewGroup的视图不能再次添加，所以先删除
		if (mRootView != null) {
			ViewGroup group = (ViewGroup) mRootView.getParent();
			group.removeView(mRootView);
		}
		super.onDestroyView();
	}

	/**
	 * 获取天气
	 */
	private class WeatherCallBack extends ApiCallBack {

		@Override
		public void onComplete() {
			mIsFlush = false;
			mListener.endUpdate();
			super.onComplete();
		}

		@Override
		public void onError(int status, String responseString, Exception e) {
			mListener.onError(status, responseString, e);
			mWeatherBean = null;
		}

		@Override
		public void onSuccess(int status, String responseString) {
			mWeatherBean = new Gson().fromJson(responseString,
					WeatherBean.class);
			setWeatherDate(mWeatherBean);
			mListener.onSuccess(status, responseString);
		}
	}

	/**
	 * 回调接口，在OnAttach中与Activity绑定，然后进行回调
	 * 
	 * @author Jay
	 * 
	 */
	public interface UpdateListener {
		/**
		 * 开始更新天气
		 * 
		 * @return 是否已经开始更新天气
		 */
		public boolean startUpdate(boolean isFlush);

		/**
		 * 更新天气失败
		 */
		public void onError(int status, String responseString, Exception e);

		/**
		 * 更新成功
		 */
		public void onSuccess(int status, String responseString);

		/**
		 * 更行天气完毕
		 */
		public void endUpdate();
	}
}
