package com.whw.pubclass;

import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.route.DrivingRouteLine.DrivingStep;

public class MyDrivingRouteOverlay extends DrivingRouteOverlay
{
	private View group_node;
	@SuppressWarnings("rawtypes")
	private RouteLine route;
	private TextView detail_textView;

	private BaiduMap mBaiduMap;

	public MyDrivingRouteOverlay(BaiduMap mBaiduMap)
	{
		super(mBaiduMap);
		this.mBaiduMap = mBaiduMap;
	}

	@SuppressWarnings("rawtypes")
	public void initOverlay(View group_node, RouteLine route,
							TextView detail_textView)
	{
		this.group_node = group_node;
		this.route = route;
		this.detail_textView = detail_textView;
	}

	@Override
	public boolean onRouteNodeClick(int arg0)
	{
		DrivingStep step = (DrivingStep) route.getAllStep().get(arg0);
		String str = step.getInstructions();

		LatLng latLng = ((DrivingStep) route.getAllStep().get(arg0))
				.getEntrace().getLocation();

		// �ƶ��ڵ�������
		MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.animateMapStatus(update, 500);

		if (group_node.getVisibility() == View.GONE)
		{
			group_node.setVisibility(View.VISIBLE);
		}
		detail_textView.setText(str);

		PublicClass.nodeIndex = arg0;

		PublicClass.setNodeMarker(latLng, mBaiduMap);

		return true;
	}
}
