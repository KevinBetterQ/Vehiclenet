package com.whw.vehiclenet;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.core.VehicleInfo;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.SuggestAddrInfo;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRouteLine.TransitStep;
import com.baidu.mapapi.search.route.TransitRouteLine.TransitStep.TransitRouteStepType;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.whw.pubclass.MyDrivingRouteOverlay;
import com.whw.pubclass.MyOrientationListener;
import com.whw.pubclass.MyTransitRouteOverlay;
import com.whw.pubclass.MyWalkingRouteOverlay;
import com.whw.pubclass.PublicClass;
import com.whw.pubclass.PublicFirstLocClass;
import com.whw.pubclass.PublicLocModeClass;
import com.whw.pubclass.PublicNodeclickClass;
import com.whw.pubclass.PublicZoomClass;

public class RoutePlanMapActivity extends Activity
{
	// 地图相关
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private MapStatus mapStatus;

	// 路线规划相关
	private ImageView imageView_route_back;
	private ImageView imageView_route_bus;
	private ImageView imageView_route_car;
	private ImageView imageView_route_foot;
	private Button btn_route_search;

	@SuppressWarnings("rawtypes")
	private RouteLine route;
	private RoutePlanSearch mSearch;

	/**
	 * 表示多条路线集合
	 *
	 **/
	private List<TransitRouteLine> transitRouteList;

	// 地图 缩放 控件
	private ImageButton btn_zoomIn;
	private ImageButton btn_zoomOut;
	private PublicZoomClass publicZoomClass;

	// 地图 节点查询 控件
	private View group_node;
	private ImageButton mBtnPre;// 上一个节点
	private ImageButton mBtnNext;// 下一个节点
	private TextView detail_textView;
	private PublicNodeclickClass publicNodeclickClass;

	// loading
	private View include_loading;
	private ImageView imageView_loading;
	private TextView textView_loading;

	// 当前上下文
	private Context context;

	// 路径规划起终点
	private String routeStart;
	private String routeEnd;

	// 路径规划方式
	private String routeMethod;

	// 起终点有歧义，歧义列表
	private SuggestAddrInfo suggestAddrInfo;
	private List<PoiInfo> poiInfoList;

	// 歧义列表相关
	private View view_Ambiguous;
	private ListView ambiguousListView;
	private TextView ambiguousTextView;
	private MyAdapter listViewAdapter;

	// 公交信息相关
	private View view_Busline;
	private ListView buslineListView;
	private MyBuslineListViewAdapter buslineListViewAdapter;

	private List<MyBuslineDetail> myBuslineDetailList;

	private boolean buslineFlag;

	private isAmbiguous flag = isAmbiguous.AMBIGUOUS_NA;

	// 定义两个经纬度变量，用于存储地理编码
	private LatLng latLngStart;
	private LatLng latLngEnd;

	// 地理编码相关
	private GeoCodeOption geoCodeOption;
	private GeoCoder geoCoder;

	// 定位控件
	private ImageButton btn_location;
	private PublicLocModeClass publicLocClass;
	private boolean isFirstLoc = true;

	// 定位 监听器
	private LocationClient mLocClient;
	private LocationClientOption option;

	// 方向传感器
	private MyOrientationListener myOrientationListener;

	// 定位
	private PublicFirstLocClass firstLoc;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_routeplan_map);

		// 当前上下文
		this.context = this;

		// 置空
		PublicClass.nodeMarker = null;

		/**
		 * 地图相关
		 *
		 * **/
		mMapView = (MapView) findViewById(R.id.map);
		mMapView.showZoomControls(false);
		mMapView.showScaleControl(false);
		mBaiduMap = mMapView.getMap();
		// 地图单击事件监听器
		mBaiduMap.setOnMapClickListener(new MyMapClickListener());
		// 设置是否允许 定位
		mBaiduMap.setMyLocationEnabled(true);
		// 设置是否允许 楼块 效果
		mBaiduMap.setBuildingsEnabled(true);
		// 当前视图初始化为山东科技大学，缩放级别为17级
		mapStatus = new MapStatus.Builder().target(PublicClass.LATLNG_SDKJ)
				.zoom(17).build();
		// 更新地图状态
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
				.newMapStatus(mapStatus);
		mBaiduMap.setMapStatus(mMapStatusUpdate);

		/**
		 * 路线规划相关
		 *
		 * **/
		MyClickListener clickListener = new MyClickListener();

		// 返回按钮
		imageView_route_back = (ImageView) findViewById(R.id.imageView_route_back);
		imageView_route_back.setOnClickListener(clickListener);

		// 公交查询按钮
		imageView_route_bus = (ImageView) findViewById(R.id.imageView_route_bus);
		imageView_route_bus.setOnClickListener(clickListener);

		// 自驾车查询按钮
		imageView_route_car = (ImageView) findViewById(R.id.imageView_route_car);
		imageView_route_car.setOnClickListener(clickListener);

		// 步行查询按钮
		imageView_route_foot = (ImageView) findViewById(R.id.imageView_route_foot);
		imageView_route_foot.setOnClickListener(clickListener);

		// 搜索按钮，该界面默认不显示
		btn_route_search = (Button) findViewById(R.id.btn_route_search);
		btn_route_search.setOnClickListener(clickListener);
		btn_route_search.setVisibility(View.INVISIBLE);

		/**
		 * 地图 缩放 控件
		 *
		 * **/
		// 放大按钮
		btn_zoomIn = (ImageButton) findViewById(R.id.btn_zoomIn);
		btn_zoomIn.setOnClickListener(clickListener);

		// 缩小按钮
		btn_zoomOut = (ImageButton) findViewById(R.id.btn_zoomOut);
		btn_zoomOut.setOnClickListener(clickListener);

		// 绑定公共类
		publicZoomClass = new PublicZoomClass(btn_zoomIn, btn_zoomOut,
				mBaiduMap, context);

		/**
		 * 地图 节点 控件
		 *
		 * **/
		// 节点按钮，由于需要传入 route 参数，所以绑定公共类操作在返回 route 数据之后
		group_node = findViewById(R.id.group_node);
		group_node.setVisibility(View.GONE);

		// 下一节点按钮，路径查询之前不显示
		mBtnPre = (ImageButton) findViewById(R.id.pre);
		mBtnPre.setOnClickListener(clickListener);

		// 上一节点按钮，路径查询之前不显示
		mBtnNext = (ImageButton) findViewById(R.id.next);
		mBtnNext.setOnClickListener(clickListener);

		detail_textView = (TextView) findViewById(R.id.detail_textView);

		/**
		 * 地图 定位 控件
		 *
		 * **/
		// 定位按钮
		btn_location = (ImageButton) findViewById(R.id.btn_location);
		btn_location.setOnClickListener(clickListener);

		publicLocClass = new PublicLocModeClass(mapStatus, mBaiduMap,
				btn_location, LocationMode.NORMAL);

		/**
		 * loading相关
		 *
		 * **/
		// include 标签，用于控制 loading 的显示与隐藏
		include_loading = findViewById(R.id.include_loading);
		imageView_loading = (ImageView) findViewById(R.id.imageView_loading);
		textView_loading = (TextView) findViewById(R.id.textView_loading);
		AnimationDrawable animationDrawable = (AnimationDrawable) imageView_loading
				.getDrawable();
		animationDrawable.start();

		/**
		 * 歧义列表相关，默认不显示
		 *
		 * **/
		// 初始化，避免空指针
		poiInfoList = new ArrayList<PoiInfo>();

		// 歧义 listview 视图
		view_Ambiguous = findViewById(R.id.view_Ambiguous);
		view_Ambiguous.setVisibility(View.INVISIBLE);

		// 标题：您要选择的起点是：
		ambiguousTextView = (TextView) findViewById(R.id.ambiguousTextView);

		// 初始化歧义 listview 视图，绑定适配器
		ambiguousListView = (ListView) findViewById(R.id.ambiguousListView);
		ambiguousListView.setOnItemClickListener(new MyItemClickListener());
		listViewAdapter = new MyAdapter(this);
		ambiguousListView.setAdapter(listViewAdapter);

		/**
		 * 公交信息相关，默认不显示
		 *
		 * **/
		// 初始化，避免空指针
		myBuslineDetailList = new ArrayList<MyBuslineDetail>();

		// 公交详细信息相关
		view_Busline = findViewById(R.id.view_Busline);
		view_Busline.setVisibility(View.INVISIBLE);

		// 初始化公交详细信息 listview 视图，绑定适配器
		buslineListView = (ListView) findViewById(R.id.buslineListView);
		buslineListView
				.setOnItemClickListener(new MyBuslineItemClickListener());
		buslineListViewAdapter = new MyBuslineListViewAdapter(this);
		buslineListView.setAdapter(buslineListViewAdapter);

		// 初始化地理编码
		geoCoder = GeoCoder.newInstance();
		geoCoder.setOnGetGeoCodeResultListener(new MyGeoCoderResultListener());

		Intent intent = getIntent();
		if (intent.hasExtra("routeMethod"))
		{
			include_loading.setVisibility(View.VISIBLE);
			textView_loading.setText("正在检索数据,请稍候……");

			Bundle bundle = intent.getExtras();
			routeMethod = bundle.getString("routeMethod");
			routeStart = bundle.getString("routeStart");
			routeEnd = bundle.getString("routeEnd");

			System.out.println("From:" + routeStart + ";to:" + routeEnd);

			if (routeMethod.equals("BUS"))
			{
				imageView_route_bus
						.setImageResource(R.drawable.route_bus_pressed);
				imageView_route_car
						.setImageResource(R.drawable.route_car_normal);
				imageView_route_foot
						.setImageResource(R.drawable.route_foot_normal);
				imageView_route_bus.setSelected(true);
				imageView_route_car.setSelected(false);
				imageView_route_car.setSelected(false);
			} else if (routeMethod.equals("CAR"))
			{
				imageView_route_bus
						.setImageResource(R.drawable.route_bus_normal);
				imageView_route_car
						.setImageResource(R.drawable.route_car_pressed);
				imageView_route_foot
						.setImageResource(R.drawable.route_foot_normal);
				imageView_route_bus.setSelected(false);
				imageView_route_car.setSelected(true);
				imageView_route_car.setSelected(false);
			} else if (routeMethod.equals("FOOT"))
			{
				imageView_route_bus
						.setImageResource(R.drawable.route_bus_normal);
				imageView_route_car
						.setImageResource(R.drawable.route_car_normal);
				imageView_route_foot
						.setImageResource(R.drawable.route_foot_pressed);
				imageView_route_bus.setSelected(false);
				imageView_route_car.setSelected(false);
				imageView_route_foot.setSelected(true);
			}

			mSearch = RoutePlanSearch.newInstance();
			mSearch.setOnGetRoutePlanResultListener(new MyRoutePlanResultListener());

			RouteSearch(routeMethod);
		}
	}

	/**
	 * 发起路线规划
	 *
	 * **/
	private void RouteSearch(String routeMethod)
	{
		if (!PublicClass.isNetReachable(context))
		{
			PublicClass.showToast(context, "暂时无法进行路径规划，请检查网络！");
			include_loading.setVisibility(View.INVISIBLE);
			return;
		}

		include_loading.setVisibility(View.VISIBLE);
		textView_loading.setText("正在进行路径规划，请稍候……");

		// 重置浏览节点的路线数据
		route = null;

		mBaiduMap.clear();
		// 处理搜索按钮响应
		// 设置起终点信息，对于tranist search 来说，城市名无意义
		PlanNode stNode, enNode = null;
		if (latLngStart != null)
		{
			stNode = PlanNode.withLocation(latLngStart);
			System.out.println("起点位置：" + latLngStart.latitude + ";"
					+ latLngStart.longitude);
		} else
		{
			stNode = PlanNode.withCityNameAndPlaceName("青岛", routeStart);
			System.out.println("起点名称：" + routeStart);
		}

		if (latLngEnd != null)
		{
			enNode = PlanNode.withLocation(latLngEnd);
			System.out.println("终点位置：" + latLngEnd.latitude + ";"
					+ latLngEnd.longitude);
		} else
		{
			enNode = PlanNode.withCityNameAndPlaceName("青岛", routeEnd);
			System.out.println("终点名称：" + routeEnd);
		}

		// 实际使用中请对起点终点城市进行正确的设定
		if (routeMethod.equals("BUS"))
		{
			mSearch.transitSearch((new TransitRoutePlanOption()).from(stNode)
					.city("青岛").to(enNode));
		} else if (routeMethod.equals("CAR"))
		{
			mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode)
					.to(enNode));
		} else if (routeMethod.equals("FOOT"))
		{
			mSearch.walkingSearch((new WalkingRoutePlanOption()).from(stNode)
					.to(enNode));
		}
	}

	/**
	 * 路线规划结果监听
	 *
	 * **/
	private class MyRoutePlanResultListener implements
			OnGetRoutePlanResultListener
	{
		@Override
		public void onGetTransitRouteResult(TransitRouteResult result)
		{
			CheckError(result, routeMethod);

			if (result.error == SearchResult.ERRORNO.NO_ERROR)
			{
				PublicClass.nodeIndex = -1;
				group_node.setVisibility(View.VISIBLE);
				detail_textView.setText(routeStart + " - " + routeEnd);

				// 获取第一条路线方案
				route = result.getRouteLines().get(0);

				System.out.println("公交路线共" + result.getRouteLines().size()
						+ "条方案");

				if (result.getRouteLines().size() > 0)
				{
					btn_route_search.setVisibility(View.VISIBLE);
					btn_route_search.setText("路线");

					transitRouteList = new ArrayList<TransitRouteLine>();
					transitRouteList = result.getRouteLines();

					// 开启线程，处理公交数据
					MyThread myThread = new MyThread();
					new Thread(myThread).start();
				}

				publicNodeclickClass = new PublicNodeclickClass(route,
						mBaiduMap, detail_textView);

				// TODO
				MyTransitRouteOverlay overlay = new MyTransitRouteOverlay(
						mBaiduMap);
				overlay.initOverlay(group_node, route, detail_textView);
				mBaiduMap.setOnMarkerClickListener(overlay);

				overlay.setData((TransitRouteLine) route);
				overlay.addToMap();
				overlay.zoomToSpan();

				include_loading.setVisibility(View.INVISIBLE);
			}
		}

		@Override
		public void onGetDrivingRouteResult(DrivingRouteResult result)
		{
			CheckError(result, routeMethod);

			if (result.error == SearchResult.ERRORNO.NO_ERROR)
			{
				PublicClass.nodeIndex = -1;
				group_node.setVisibility(View.VISIBLE);
				detail_textView.setText(routeStart + " - " + routeEnd);

				// 获取第一条路线方案
				route = result.getRouteLines().get(0);

				System.out.println("驾车路线共" + result.getRouteLines().size()
						+ "条方案");

				publicNodeclickClass = new PublicNodeclickClass(route,
						mBaiduMap, detail_textView);

				MyDrivingRouteOverlay overlay = new MyDrivingRouteOverlay(
						mBaiduMap);
				overlay.initOverlay(group_node, route, detail_textView);
				mBaiduMap.setOnMarkerClickListener(overlay);

				overlay.setData((DrivingRouteLine) route);
				overlay.addToMap();
				overlay.zoomToSpan();

				include_loading.setVisibility(View.INVISIBLE);
			}
		}

		@Override
		public void onGetWalkingRouteResult(WalkingRouteResult result)
		{
			CheckError(result, routeMethod);

			if (result.error == SearchResult.ERRORNO.NO_ERROR)
			{
				PublicClass.nodeIndex = -1;
				group_node.setVisibility(View.VISIBLE);
				detail_textView.setText(routeStart + " - " + routeEnd);

				// 获取第一条路线方案
				route = result.getRouteLines().get(0);

				System.out.println("步行路线共" + result.getRouteLines().size()
						+ "条方案");

				publicNodeclickClass = new PublicNodeclickClass(route,
						mBaiduMap, detail_textView);

				MyWalkingRouteOverlay overlay = new MyWalkingRouteOverlay(
						mBaiduMap);
				overlay.initOverlay(group_node, route, detail_textView);
				mBaiduMap.setOnMarkerClickListener(overlay);

				overlay.setData((WalkingRouteLine) route);
				overlay.addToMap();
				overlay.zoomToSpan();

				include_loading.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * 检查路径规划返回结果基本错误
	 *
	 * **/
	private void CheckError(SearchResult result, String routeMethod)
	{
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR)
		{
			include_loading.setVisibility(View.INVISIBLE);

			if (routeMethod.equals("BUS"))
			{
				suggestAddrInfo = ((TransitRouteResult) result)
						.getSuggestAddrInfo();
			} else if (routeMethod.equals("CAR"))
			{
				suggestAddrInfo = ((DrivingRouteResult) result)
						.getSuggestAddrInfo();
			} else if (routeMethod.equals("FOOT"))
			{
				suggestAddrInfo = ((WalkingRouteResult) result)
						.getSuggestAddrInfo();
			}

			if (!poiInfoList.isEmpty())
			{
				poiInfoList.clear();
			}

			// 字数短的更容易产生歧义,flag != AMBIGUOUS_START 表示未进行起点歧义判断，下次直接判断终点歧义
			// 如果flag= AMBIGUOUS_NA ，说明未进行歧义判断 TODO
			if (flag == isAmbiguous.AMBIGUOUS_NA)
			{
				flag = (routeStart.length() <= routeEnd.length()) ? isAmbiguous.AMBIGUOUS_START
						: isAmbiguous.AMBIGUOUS_END;
			}

			if (flag == isAmbiguous.AMBIGUOUS_START
					&& suggestAddrInfo.getSuggestStartNode() != null)
			{
				ambiguousTextView.setText("您要选择的起点是：");
				fillList();
			} else if (flag == isAmbiguous.AMBIGUOUS_END
					&& suggestAddrInfo.getSuggestEndNode() != null)
			{
				ambiguousTextView.setText("您要选择的终点是：");
				fillList();
			}

			view_Ambiguous.setVisibility(View.VISIBLE);
			listViewAdapter.notifyDataSetChanged();
		} else if (result.error == SearchResult.ERRORNO.ST_EN_TOO_NEAR)
		{
			PublicClass.showToast(context, "起终点太近");
			include_loading.setVisibility(View.INVISIBLE);
		} else if (result == null
				|| result.error != SearchResult.ERRORNO.NO_ERROR)
		{
			PublicClass.showToast(context, "抱歉，未找到结果……");
			include_loading.setVisibility(View.INVISIBLE);
		}
	}

	private void fillList()
	{
		if (flag == isAmbiguous.AMBIGUOUS_START)
		{
			// 填充起点歧义列表
			poiInfoList = suggestAddrInfo.getSuggestStartNode();
		} else if (flag == isAmbiguous.AMBIGUOUS_END)
		{
			// 填充终点歧义列表
			poiInfoList = suggestAddrInfo.getSuggestEndNode();
		}

		int size = poiInfoList.size();

		System.out.println("歧义列表总个数" + size);

		// 将没有地址信息的 item 删除
		List<PoiInfo> remove = new ArrayList<PoiInfo>();

		for (PoiInfo item : poiInfoList)
		{
			if (item.address.isEmpty())
			{
				remove.add(item);
			}
		}

		poiInfoList.removeAll(remove);

		System.out.println("歧义列表筛选之后个数" + poiInfoList.size());

		if (poiInfoList.isEmpty())
		{
			if (flag == isAmbiguous.AMBIGUOUS_START)
			{
				PublicClass.showToast(context, "起点选择错误，请重新选择");
			} else if (flag == isAmbiguous.AMBIGUOUS_END)
			{
				PublicClass.showToast(context, "终点选择错误，请重新选择");
			}
			RoutePlanMapActivity.this.finish();
		}
	}

	/**
	 * 地理编码结果监听器
	 *
	 * **/
	private class MyGeoCoderResultListener implements
			OnGetGeoCoderResultListener
	{
		@Override
		public void onGetGeoCodeResult(GeoCodeResult result)
		{
			if (result.error != SearchResult.ERRORNO.NO_ERROR)
			{
				include_loading.setVisibility(View.INVISIBLE);
				PublicClass.showToast(context, "路径规划出错……");
				return;
			}

			if (flag == isAmbiguous.AMBIGUOUS_START)
			{
				latLngStart = result.getLocation();
				flag = isAmbiguous.AMBIGUOUS_END;
			} else if (flag == isAmbiguous.AMBIGUOUS_END)
			{
				latLngEnd = result.getLocation();
				flag = isAmbiguous.AMBIGUOUS_START;
			}
			RouteSearch(routeMethod);
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result)
		{

		}
	}

	// 地图点击事件监听器
	private class MyMapClickListener implements OnMapClickListener
	{
		@Override
		public void onMapClick(LatLng arg0)
		{
			if (PublicClass.nodeMarker != null)
			{
				PublicClass.nodeMarker.remove();
			}

			if (group_node.getVisibility() != View.GONE)
			{
				group_node.setVisibility(View.GONE);
			}
		}

		@Override
		public boolean onMapPoiClick(MapPoi arg0)
		{
			return false;
		}
	}

	// button 点击事件监听器
	private class MyClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
				// 路线规划相关
			case R.id.imageView_route_back:
				RoutePlanMapActivity.this.finish();
				break;

			case R.id.imageView_route_bus:
				imageView_route_bus
						.setImageResource(R.drawable.route_bus_pressed);
				imageView_route_car
						.setImageResource(R.drawable.route_car_normal);
				imageView_route_foot
						.setImageResource(R.drawable.route_foot_normal);

				if (btn_route_search.getVisibility() != View.VISIBLE)
				{
					btn_route_search.setVisibility(View.VISIBLE);
				}

				if (!routeMethod.equals("BUS"))
				{
					routeMethod = "BUS";
					RouteSearch("BUS");
				}
				break;

			case R.id.imageView_route_car:
				imageView_route_bus
						.setImageResource(R.drawable.route_bus_normal);
				imageView_route_car
						.setImageResource(R.drawable.route_car_pressed);
				imageView_route_foot
						.setImageResource(R.drawable.route_foot_normal);

				if (btn_route_search.getVisibility() != View.INVISIBLE)
				{
					btn_route_search.setVisibility(View.INVISIBLE);
				}

				if (!routeMethod.equals("CAR"))
				{
					routeMethod = "CAR";
					RouteSearch("CAR");
				}
				break;

			case R.id.imageView_route_foot:
				imageView_route_bus
						.setImageResource(R.drawable.route_bus_normal);
				imageView_route_car
						.setImageResource(R.drawable.route_car_normal);
				imageView_route_foot
						.setImageResource(R.drawable.route_foot_pressed);

				if (btn_route_search.getVisibility() != View.INVISIBLE)
				{
					btn_route_search.setVisibility(View.INVISIBLE);
				}

				if (!routeMethod.equals("FOOT"))
				{
					routeMethod = "FOOT";
					RouteSearch("FOOT");
				}
				break;

				// 地图 定位 控件
				case R.id.btn_location:
					// TODO
					if (isFirstLoc)
					{
						// 注册 定位 服务
					mLocClient = new LocationClient(RoutePlanMapActivity.this);
					option = new LocationClientOption();
					myOrientationListener = new MyOrientationListener(context);

					firstLoc = new PublicFirstLocClass(context);
					firstLoc.initView(include_loading, textView_loading,
							btn_location);
					firstLoc.initMap(mBaiduMap);
					firstLoc.initLoc(mLocClient, option);
					firstLoc.initOrientation(myOrientationListener);

					firstLoc.startLoc();

					isFirstLoc = false;
				} else
				{
					publicLocClass.changeMode();
				}

				break;

				// 放大控件
				case R.id.btn_zoomIn:
					publicZoomClass.zoom(1);
					break;

				// 缩小控件
				case R.id.btn_zoomOut:
					publicZoomClass.zoom(2);
					break;

				// 上一个节点
				case R.id.pre:
					publicNodeclickClass.NodeClick(v);
					break;

				// 下一个节点
				case R.id.next:
					publicNodeclickClass.NodeClick(v);
					break;

				// 方案按钮
				case R.id.btn_route_search:

					if (buslineFlag)
					{
						buslineListViewAdapter.notifyDataSetChanged();
						// 显示公交线路列表
						view_Busline.setVisibility(View.VISIBLE);
					}

				break;

			default:
				break;
			}
		}
	}

	// listview item 选中填充 起始点或者终点
	private class MyItemClickListener implements OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id)
		{
			geoCodeOption = new GeoCodeOption();
			geoCodeOption.city("青岛市");
			geoCodeOption.address(poiInfoList.get(position).address);

			view_Ambiguous.setVisibility(View.INVISIBLE);
			include_loading.setVisibility(View.VISIBLE);
			textView_loading.setText("正在进行路径规划,请稍候……");

			geoCoder.geocode(geoCodeOption);
		}
	}

	// listview item 选中选择相应公交路线
	private class MyBuslineItemClickListener implements OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id)
		{
			PublicClass.nodeIndex = -1;
			route = null;
			mBaiduMap.clear();
			// 隐藏公交列表
			view_Busline.setVisibility(View.INVISIBLE);

			route = transitRouteList.get(position);

			publicNodeclickClass = new PublicNodeclickClass(route, mBaiduMap,
					detail_textView);

			// TODO
			MyTransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaiduMap);
			overlay.initOverlay(group_node, route, detail_textView);
			mBaiduMap.setOnMarkerClickListener(overlay);

			overlay.setData((TransitRouteLine) route);
			overlay.addToMap();
			overlay.zoomToSpan();
		}
	}

	/**
	 * 自定义歧义列表适配器
	 *
	 * **/
	private class MyAdapter extends BaseAdapter
	{
		private LayoutInflater mInflater;

		private MyAdapter(Context context)
		{
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount()
		{
			return poiInfoList.size();
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
				convertView = mInflater
						.inflate(
								R.layout.layout_routeplan_ambiguous_listview_item,
								null);

				myHolder = new MyHolder();

				myHolder.textView_routeplan_ambiguous_items_name = (TextView) convertView
						.findViewById(R.id.textView_routeplan_ambiguous_items_name);
				myHolder.textView_routeplan_ambiguous_items_address = (TextView) convertView
						.findViewById(R.id.textView_routeplan_ambiguous_items_address);

				convertView.setTag(myHolder);
			} else
			{
				myHolder = (MyHolder) convertView.getTag();
			}

			PoiInfo poiInfo = poiInfoList.get(position);

			myHolder.textView_routeplan_ambiguous_items_name
					.setText(poiInfo.name);
			myHolder.textView_routeplan_ambiguous_items_address
					.setText(poiInfo.address);

			return convertView;
		}
	}

	/**
	 * 自定义公交详细信息适配器
	 *
	 **/
	public class MyBuslineListViewAdapter extends BaseAdapter
	{
		private LayoutInflater mInflater;

		public MyBuslineListViewAdapter(Context context)
		{
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount()
		{
			return myBuslineDetailList.size();
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
			MyBuslineHolder myBuslineHolder;
			if (convertView == null)
			{
				convertView = mInflater.inflate(
						R.layout.layout_routeplan_busline_listview_item, null);

				myBuslineHolder = new MyBuslineHolder();

				myBuslineHolder.textView_routeplan_busline_items_name = (TextView) convertView
						.findViewById(R.id.textView_routeplan_busline_items_name);
				myBuslineHolder.textView_routeplan_busline_items_time = (TextView) convertView
						.findViewById(R.id.textView_routeplan_busline_items_time);
				myBuslineHolder.textView_routeplan_busline_items_passStationNum = (TextView) convertView
						.findViewById(R.id.textView_routeplan_busline_items_passStationNum);
				myBuslineHolder.textView_routeplan_busline_items_walkDistance = (TextView) convertView
						.findViewById(R.id.textView_routeplan_busline_items_walkDistance);

				convertView.setTag(myBuslineHolder);
			} else
			{
				myBuslineHolder = (MyBuslineHolder) convertView.getTag();
			}

			MyBuslineDetail myBuslineDetail = myBuslineDetailList.get(position);
			String busName = myBuslineDetail.busName;
			String busTime = myBuslineDetail.busTime;
			String busPassStationNum = myBuslineDetail.busPassStationNum;
			String busWalkDistance = myBuslineDetail.busWalkDistance;

			myBuslineHolder.textView_routeplan_busline_items_name
					.setText(busName);
			myBuslineHolder.textView_routeplan_busline_items_time
					.setText(busTime);
			myBuslineHolder.textView_routeplan_busline_items_passStationNum
					.setText(busPassStationNum);
			myBuslineHolder.textView_routeplan_busline_items_walkDistance
					.setText(busWalkDistance);

			return convertView;
		}
	}

	// layout_routeplan_ambiguous_item.xml 构造器
	public class MyHolder
	{
		public TextView textView_routeplan_ambiguous_items_name;
		public TextView textView_routeplan_ambiguous_items_address;
	}

	// layout_routeplan_busline_item.xml 构造器
	public class MyBuslineHolder
	{
		public TextView textView_routeplan_busline_items_name;
		public TextView textView_routeplan_busline_items_time;
		public TextView textView_routeplan_busline_items_passStationNum;
		public TextView textView_routeplan_busline_items_walkDistance;
	}

	public class MyBuslineDetail
	{
		private String busName;
		private String busTime;
		private String busPassStationNum;
		private String busWalkDistance;

		public MyBuslineDetail(String busName, String busTime,
				String busPassStationNum, String busWalkDistance)
		{
			this.busName = busName;
			this.busTime = busTime;
			this.busPassStationNum = busPassStationNum;
			this.busWalkDistance = busWalkDistance;
		}
	}

	// 多线程，用于处理耗时数据操作

	private class MyThread implements Runnable
	{
		@Override
		public void run()
		{
			// 表示一个条路线中所有的路段
			List<TransitRouteLine.TransitStep> transitStep;

			if (myBuslineDetailList.size() != 0)
			{
				myBuslineDetailList.clear();
			}

			// item 表示多条方案中的一整条路线
			for (TransitRouteLine item : transitRouteList)
			{
				// 初始化参数，用于存储 name , time , passStationNum , walkDistance;
				String name = "";
				int time = 0;
				int passStationNum = 0;
				int walkDistance = 0;

				transitStep = new ArrayList<TransitRouteLine.TransitStep>();

				transitStep = item.getAllStep();

				for (int i = 0; i < transitStep.size(); i++)
				{
					// 得到当前这段路段
					TransitStep step = transitStep.get(i);

					// 得到当前这段路段的类型
					TransitRouteStepType stepType = step.getStepType();

					// 如果是步行路线，累加步行距离
					if (stepType == TransitRouteStepType.WAKLING)
					{
						// 累加步行距离
						walkDistance += step.getDistance();
						// 累加步行时间
						time += step.getDuration();
					} else if (stepType == TransitRouteStepType.BUSLINE)
					{
						// 得到交通工具信息
						VehicleInfo vehicleInfo = step.getVehicleInfo();
						// 累加公交换乘信息
						name = (name.equals("")) ? (vehicleInfo.getTitle())
								: (name + "→" + vehicleInfo.getTitle());
						// 累加公交乘坐时间
						time += step.getDuration();
						// 累加经过站点信息
						passStationNum += vehicleInfo.getPassStationNum();
					}
				}

				// 初始化MyBuslineDetail对象
				MyBuslineDetail myBuslineDetail = new MyBuslineDetail(name,
						TimeConvert(time), "共" + passStationNum + "站", "步行"
						+ walkDistance + "米");
				// 将MyBuslineDetail对象添加到集合中
				myBuslineDetailList.add(myBuslineDetail);
				buslineFlag = true;
			}

			for (MyBuslineDetail item : myBuslineDetailList)
			{
				System.out.println("---------------------------");
				System.out.println(item.busName);
				System.out.println(item.busTime);
				System.out.println(item.busPassStationNum);
				System.out.println(item.busWalkDistance);
			}
		}
	}

	/**
	 * 时间格式转换
	 *
	 * @param time
	 * @return
	 */
	private String TimeConvert(int time)
	{
		int hour = time / 3600;
		int minute = (time % 3600) / 60;
		int second = time % 60;

		String string1 = (hour == 0) ? "" : hour + "小时";
		String string2 = (minute == 0) ? "" : minute + "分钟";
		String string3 = (second == 0) ? "" : second + "秒";

		return "Լ" + string1 + string2 + string3;
	}

	private enum isAmbiguous
	{
		AMBIGUOUS_NA, AMBIGUOUS_START, AMBIGUOUS_END;
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		System.out.println("onDestroy()");

		mMapView.onDestroy();
	}

	@Override
	protected void onRestart()
	{
		super.onRestart();
		// 开启定位
		mBaiduMap.setMyLocationEnabled(true);

		if (mLocClient != null)
		{
			if (!mLocClient.isStarted())
			{
				mLocClient.start();
				System.out.println("mLocClient 已经开启");
			}
		}

		if (myOrientationListener != null)
		{
			// 开启方向传感器
			myOrientationListener.start();
			System.out.println("开启方向传感器");
		}

		System.out.println("onRestart()");
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		mBaiduMap.setMyLocationEnabled(false);

		if (mLocClient != null)
		{
			if (mLocClient.isStarted())
			{
				mLocClient.stop();
				System.out.println("mLocClient 已经关闭");
			}
		}

		if (myOrientationListener != null)
		{
			// 停止方向传感器
			myOrientationListener.stop();
			System.out.println("停止方向传感器");
		}

		System.out.println("onStop()");
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		mMapView.onResume();
		System.out.println("onResume()");
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		mMapView.onPause();
		System.out.println("onPause()");
	}

}
