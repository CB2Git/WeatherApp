package com.jay.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.jay.bean.County;
import com.jay.customview.IndexView;
import com.jay.customview.IndexView.OnIndexTouchListener;
import com.jay.customview.SuperEditText;
import com.jay.customview.SuperEditText.OnCancelClickListener;
import com.jay.dao.CityDB;
import com.jay.dao.CountyDB;
import com.jay.locationselect.R;
import com.jay.util.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("DefaultLocale")
public class AddressSelect extends Activity {
	public static final int REQUEST_ADDR = 100;
	public static final int REQUEST_ERROR = 101;
	public static final int REQUEST_OK = 102;
	// 定位按钮
	private Button mLocation;
	// 返回按钮
	private ImageView mBack;
	// 搜索框
	private SuperEditText mEditText;
	// 城市列表
	private ListView mCityList;
	// 城市列表的适配器
	private CityListAdapter mAdapter;
	// 索引菜单
	private IndexView mIndexView;

	// 百度定位需要的变量
	private LocationClient mLocationClient = null;
	private BDLocationListener myListener = new MyLocationListener();
	// 百度地图定位到的位置
	private String mBDLocationStr;
	// 是否定位成功
	private boolean mRequestOk = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.address_select);
		initView();
		initLocation();
	}

	private void initView() {
		initLocationBtn();
		initSearchText();
		initCountyList();
		mBack = (ImageView) findViewById(R.id.back);
		mBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * 初始化城市列表
	 */
	private void initCountyList() {
		// 实例化索引菜单
		mIndexView = (IndexView) findViewById(R.id.indexview);
		// 实例化城市列表
		mCityList = (ListView) findViewById(R.id.citylist);

		mAdapter = new CityListAdapter(this);
		mCityList.setAdapter(mAdapter);

		mCityList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				setResult(REQUEST_OK, (County) mAdapter.getItem(position));
			}
		});

		mIndexView.setOnIndexTouchListener(new OnIndexTouchListener() {

			@Override
			public void OnIndexTouch(String sel) {

				int positon = mAdapter.setPositionByIndex(sel.toUpperCase());
				if (positon >= 0)
					mCityList.setSelection(positon);
			}
		});
	}

	/**
	 * 初始化搜索框
	 */
	private void initSearchText() {
		mEditText = (SuperEditText) findViewById(R.id.superedit);
		// 添加清空按钮点击消息
		mEditText.setOnCancelClickListener(new OnCancelClickListener() {
			@Override
			public void OnCancelClick() {
				mEditText.setText("");
			}
		});

		// 增加文字改变监听，当文字改变的时候显示/隐藏按钮
		mEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().length() > 0) {
					mEditText.showCancelIcon(true);
					mAdapter.loadData(s.toString());
				} else {
					mAdapter.loadData(null);
					mEditText.showCancelIcon(false);
				}
			}
		});
	}

	/**
	 * 初始化定位按钮
	 */
	private void initLocationBtn() {
		mLocation = (Button) findViewById(R.id.location);
		// 为定位按钮添加定位图片
		Drawable location_icon = getResources()
				.getDrawable(R.drawable.location);
		int bound = Math.min(mLocation.getWidth(), mLocation.getHeight());
		bound = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				25, getResources().getDisplayMetrics());
		location_icon.setBounds(-5, 0, bound - 5, bound);
		mLocation.setCompoundDrawables(location_icon, null, null, null);
		mLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 如果没有定位信息
				if (!mRequestOk) {
					Toast.makeText(AddressSelect.this, "正在获取位置信息!",
							Toast.LENGTH_SHORT).show();
				} else {
					mLocation.getText();
					County county = CityDB.getCountyByName(AddressSelect.this,
							mLocation.getText().toString());
					if (county == null) {
						Toast.makeText(AddressSelect.this, "无法解析对应的地理信息",
								Toast.LENGTH_SHORT).show();
						setResult(REQUEST_ERROR, county);
						mLocation.setText("获取位置信息失败");
					} else {
						setResult(REQUEST_OK, county);
					}
				}
			}
		});
	}

	/**
	 * 初始化定位信息
	 */
	private void initLocation() {
		// 注册监听器
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		option.setScanSpan(3000);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(false);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(false);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(true);// 可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}

	/**
	 * 定位监听器
	 */
	private class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {

			if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
				Log.v("DB", "gps定位成功");
				mRequestOk = true;
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
				Log.v("DB", "网络定位成功");
				mRequestOk = true;
			} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
				mRequestOk = true;
				Log.v("DB", "离线定位成功，离线定位结果也是有效的");
			} else if (location.getLocType() == BDLocation.TypeServerError) {
				Log.v("DB", "服务端网络定位失败");
			} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
				Log.v("DB", "网络不同导致定位失败，请检查网络是否通畅");
			} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
				Log.v("DB",
						"无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
			}
			if (!mRequestOk)
				return;
			mLocationClient.stop();
			mBDLocationStr = location.getCity() + location.getDistrict();
			Log.v("x", mBDLocationStr);
			mLocation.setText(mBDLocationStr);
		}
	}

	@Override
	protected void onDestroy() {
		mLocationClient.stop();
		super.onDestroy();
	}

	/**
	 * 城市列表的适配器
	 */
	private class CityListAdapter extends BaseAdapter {

		private Context mContext;
		// 是否是查询模式
		private boolean isSearch = false;
		// 所有城市数据
		private ArrayList<County> mCountyList = new ArrayList<County>();
		// 所偶遇城市索引
		private Map<String, Integer> mBeginPos = new HashMap<String, Integer>();

		public CityListAdapter(Context context) {
			mContext = context;
			loadData(null);
		}

		/**
		 * 加载数据源
		 * 
		 * @param fifter
		 *            数据源过滤字符
		 */
		public void loadData(String fifter) {
			if (fifter == null || fifter.length() == 0) {
				isSearch = false;
				// 初始化数据源
				if (mCountyList != null)
					mCountyList.clear();
				mCountyList = CountyDB.getAll(AddressSelect.this);
				// 为数据源添加热门城市
				addHotCounty();
			} else {
				isSearch = true;
				mCountyList = CountyDB.getAllByFifter(AddressSelect.this,
						fifter);
			}
			this.notifyDataSetChanged();
		}

		/**
		 * 给数据集里面添加热门城市
		 */
		private void addHotCounty() {
			County county1 = new County("010101", "0101", "北京", "101010100",
					"BeiJing");
			County county2 = new County("020101", "0201", "上海", "101020100",
					"ShangHai");
			County county3 = new County("030101", "0301", "天津", "101030100",
					"TianJin");
			County county4 = new County("040101", "0401", "重庆", "101040100",
					"ChongQing");
			County county5 = new County("060201", "0602", "吉林", "101060201",
					"JiLin");
			mCountyList.add(0, county5);
			mCountyList.add(0, county4);
			mCountyList.add(0, county3);
			mCountyList.add(0, county2);
			mCountyList.add(0, county1);
			for (int i = mCountyList.size() - 1; i >= 5; i--) {
				mBeginPos
						.put(mCountyList.get(i).getPinyin().substring(0, 1), i);
			}
			mBeginPos.put("#", 0);
		}

		@Override
		public int getCount() {
			return mCountyList.size();
		}

		@Override
		public Object getItem(int position) {
			return mCountyList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public int setPositionByIndex(String str) {
			if (mBeginPos.get(str) == null) {
				return -1;
			}
			return mBeginPos.get(str);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			County county = mCountyList.get(position);
			if (convertView == null) {
				convertView = View
						.inflate(mContext, R.layout.ltem_layout, null);
			}

			TextView title = ViewHolder.get(convertView, R.id.item_title);
			TextView context = ViewHolder.get(convertView, R.id.item_context);

			String firstChar = county.getPinyin().substring(0, 1);
			if (mBeginPos.get(firstChar) == position)
				title.setVisibility(View.VISIBLE);
			else
				title.setVisibility(View.GONE);
			title.setText(firstChar);
			context.setText(county.getName());
			// 如果不在搜索模式下，且显示的是第一个，就显示热门城市
			if (!isSearch && position == 0) {
				title.setVisibility(View.VISIBLE);
				title.setText("热门城市");
			}
			return convertView;
		}
	}

	/**
	 * 返回结果给调用的Activity
	 * 
	 * @param resultCode
	 * @param county
	 */
	public void setResult(int resultCode, County county) {
		if (resultCode == AddressSelect.REQUEST_OK)
			setResult(resultCode, getIntent().putExtra("county", county));
		finish();
	}
}