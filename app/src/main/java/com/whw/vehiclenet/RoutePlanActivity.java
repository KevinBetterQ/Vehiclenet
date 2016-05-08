package com.whw.vehiclenet;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.whw.pubclass.PublicClass;
import com.whw.pubclass.PublicTextWatcherClass;

public class RoutePlanActivity extends Activity
{
	// 声明 后退 按钮
	private ImageView imageView_route_back;

	// 声明 三种出行方式 按钮
	private ImageView imageView_route_bus;
	private ImageView imageView_route_car;
	private ImageView imageView_route_foot;

	// 声明 搜索 按钮
	private Button btn_route_search;

	// 声明 切换起终点 按钮
	private ImageView route_switch;

	// 声明 起终点 输入框
	private EditText route_start;
	private EditText route_end;

	// 建议搜索 相关
	private SuggestionSearch suggestionSearch;

	// 建议列表 listview
	private ListView netSearchListView;

	// 建议列表填充数组
	private List<ResultObject> resultList;

	private MyListViewAdapter listViewAdapter;

	// 三种方式：BUS表示公交，CAR表示驾车，FOOT表示步行
	private String routeMethod;

	// loading
	private View include_progressbar;
	private TextView textView_loading;

	// 上下文
	private Context context;

	// 常用数据
	private String[] commonAddr = new String[] { "山东科技大学校园", "山东科技大学-北门",
			"山东科技大学-南门", "火车站", "金沙滩景区" };
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.layout_routeplan_set);

		// 上下文
		this.context = this;

		// 是否应该继续检索，当item被选中时不应该继续检索
		PublicClass.shouldSearch = true;

		MyClickListener clickListener = new MyClickListener();

		// 后退按钮
		imageView_route_back = (ImageView) findViewById(R.id.imageView_route_back);
		imageView_route_back.setOnClickListener(clickListener);

		// 公交路线规划。
		imageView_route_bus = (ImageView) findViewById(R.id.imageView_route_bus);
		imageView_route_bus.setOnClickListener(clickListener);



		// 自驾车路线规划，设置默认为汽车查询
		imageView_route_car = (ImageView) findViewById(R.id.imageView_route_car);
		imageView_route_car.setOnClickListener(clickListener);
		imageView_route_car.setImageResource(R.drawable.route_car_pressed);
		routeMethod = "CAR";

		// 步行路线规划
		imageView_route_foot = (ImageView) findViewById(R.id.imageView_route_foot);
		imageView_route_foot.setOnClickListener(clickListener);

		// 搜索按钮
		btn_route_search = (Button) findViewById(R.id.btn_route_search);
		btn_route_search.setOnClickListener(clickListener);

		// 起终点切换按钮
		route_switch = (ImageView) findViewById(R.id.route_switch);
		route_switch.setOnClickListener(clickListener);

		resultList = setDatas();

		// 建议列表
		netSearchListView = (ListView) findViewById(R.id.smartListView);
		netSearchListView.setOnItemClickListener(new MyItemClickListener());
		listViewAdapter = new MyListViewAdapter(this);
		netSearchListView.setAdapter(listViewAdapter);

		// loading
		include_progressbar = findViewById(R.id.include_progressbar);
		include_progressbar.setVisibility(View.INVISIBLE);
		textView_loading = (TextView) findViewById(R.id.textView_loading_small);

		// 初始化建议搜索
		suggestionSearch = SuggestionSearch.newInstance();
		suggestionSearch
				.setOnGetSuggestionResultListener(new MyGetSuggestionResultListener());

		// 需要初始化各参数，注意避免空指针
		PublicTextWatcherClass textWatcher = new PublicTextWatcherClass(context);
		textWatcher.initTextWatcher(include_progressbar, textView_loading,
				netSearchListView, suggestionSearch);

		// 起点 EditText 输入框
		route_start = (EditText) findViewById(R.id.route_start);
		textWatcher.startTextWatcher(route_start);

		// 终点 EditText 输入框
		route_end = (EditText) findViewById(R.id.route_end);
		textWatcher.startTextWatcher(route_end);
	}

	private List<ResultObject> setDatas()
	{
		// 初始化结果数组
		resultList = new ArrayList<ResultObject>();

		ResultObject object = null;
		for (String item : commonAddr)
		{
			object = new ResultObject(item, "");
			resultList.add(object);
		}
		return resultList;
	}

	public class MyListViewAdapter extends BaseAdapter
	{
		private LayoutInflater mInflater;

		private MyListViewAdapter(Context context)
		{
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount()
		{
			return resultList.size();
		}

		@Override
		public Object getItem(int position)
		{
			return position;
		}

		@Override
		public long getItemId(int position)
		{
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			MyHolder myHolder;
			if (convertView == null)
			{
				convertView = mInflater.inflate(
						R.layout.layout_routeplan_listview_item, null);

				myHolder = new MyHolder();

				myHolder.textView_routeplan_items_key = (TextView) convertView
						.findViewById(R.id.textView_routeplan_items_key);

				myHolder.textView_routeplan_items_district = (TextView) convertView
						.findViewById(R.id.textView_routeplan_items_district);

				convertView.setTag(myHolder);
			} else
			{
				myHolder = (MyHolder) convertView.getTag();
			}

			String key = resultList.get(position).key;
			String district = resultList.get(position).district;

			myHolder.textView_routeplan_items_key.setText(key);
			myHolder.textView_routeplan_items_district.setText(district);

			return convertView;
		}
	}

	private class MyGetSuggestionResultListener implements
			OnGetSuggestionResultListener
	{
		@Override
		public void onGetSuggestionResult(SuggestionResult res)
		{
			if (res == null || res.getAllSuggestions() == null)
			{
				return;
			}
			resultList.clear();

			ResultObject object = null;

			for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions())
			{
				// 填充适配器
				if (!info.key.isEmpty())
				{
					object = new ResultObject(info.key, info.district);
				}

				resultList.add(object);
			}

			listViewAdapter.notifyDataSetChanged();

			include_progressbar.setVisibility(View.INVISIBLE);
		}
	}

	// listview item 选中填充 Edittext
	private class MyItemClickListener implements OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id)
		{
			// item 点击时，edittext 填充，此时不应该再检索
			PublicClass.shouldSearch = false;

			String key = resultList.get(position).key;
			if (route_start.isFocused())
			{
				route_start.setText(key);
				route_start.setSelection(route_start.getText().length());
			} else if (route_end.isFocused())
			{
				route_end.setText(key);
				route_end.setSelection(route_end.getText().length());
			}
		}
	}

	// layout_routeplan_set_item.xml 构造器
	public class MyHolder
	{
		public TextView textView_routeplan_items_key;
		public TextView textView_routeplan_items_district;
	}

	// 建议查询返回结果对象
	public class ResultObject
	{
		private String key;
		private String district;

		public ResultObject(String key, String district)
		{
			this.key = key;
			this.district = district;
		}
	}

	private class MyClickListener implements OnClickListener
	{

		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
			case R.id.imageView_route_back:
				RoutePlanActivity.this.finish();
				break;

			case R.id.imageView_route_bus:
				imageView_route_bus
						.setImageResource(R.drawable.route_bus_pressed);
				imageView_route_car
						.setImageResource(R.drawable.route_car_normal);
				imageView_route_foot
						.setImageResource(R.drawable.route_foot_normal);

				routeMethod = "BUS";
				break;

			case R.id.imageView_route_car:
				imageView_route_bus
						.setImageResource(R.drawable.route_bus_normal);
				imageView_route_car
						.setImageResource(R.drawable.route_car_pressed);
				imageView_route_foot
						.setImageResource(R.drawable.route_foot_normal);

				routeMethod = "CAR";
				break;

			case R.id.imageView_route_foot:
				imageView_route_bus
						.setImageResource(R.drawable.route_bus_normal);
				imageView_route_car
						.setImageResource(R.drawable.route_car_normal);
				imageView_route_foot
						.setImageResource(R.drawable.route_foot_pressed);

				routeMethod = "FOOT";
				break;

			// TODO
			case R.id.btn_route_search:

				String start = route_start.getText().toString().trim();
				String end = route_end.getText().toString().trim();

				if (start.isEmpty())
				{
					PublicClass.showToast(context, "起点不能为空！");
					return;
				} else if (end.isEmpty())
				{
					PublicClass.showToast(context, "终点不能为空！");
					return;
				} else if (start.equals(end))
				{
					PublicClass.showToast(context, "起点不能与终点相同");
					return;
				}

				Intent intent = new Intent();
				intent.putExtra("routeMethod", routeMethod);
				intent.putExtra("routeStart", start);
				intent.putExtra("routeEnd", end);
				intent.setClass(RoutePlanActivity.this,
						RoutePlanMapActivity.class);
				startActivity(intent);

				break;

			case R.id.route_switch:
				String start2 = route_start.getText().toString();
				String end2 = route_end.getText().toString();

				if (start2.isEmpty() && end2.isEmpty())
				{
					return;
				}

				route_start.setText(end2);
				route_end.setText(start2);

				break;

			default:
				break;
			}
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (suggestionSearch != null)
		{
			suggestionSearch.destroy();
		}
	}

}
