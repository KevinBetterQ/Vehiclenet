package com.whw.pubclass;

import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.whw.vehiclenet.R;

/**
 * 自定义类，用于控制节点，显示详细信息
 * <p>
 * 构造函数参数：
 * <p>
 *
 * //@param route
 *            RouteLine对象
 * //@param nodeIndex
 *            节点索引
 * //@param mBaiduMap
 *            BaiduMap对象
 * //@param group_node
 *            详细信息显示控制View
 * //@param detail_textView
 *            详细信息显示 TextView
 *
 */
public class PublicNodeclickClass
{
	@SuppressWarnings("rawtypes")
	private RouteLine route;
	private BaiduMap mBaiduMap;
	private TextView detail_textView;

	@SuppressWarnings("rawtypes")
	public PublicNodeclickClass(RouteLine route, BaiduMap mBaiduMap,
								TextView detail_textView)
	{
		this.route = route;
		this.mBaiduMap = mBaiduMap;
		this.detail_textView = detail_textView;
	}

	/**
	 * 上、下一个节点查询，infowindow 显示
	 *
	 * **/
	public void NodeClick(View v)
	{
		if (route == null || route.getAllStep() == null)
		{
			return;
		}
		if (PublicClass.nodeIndex == -1 && v.getId() == R.id.pre)
		{
			return;
		}
		// 设置节点索引
		if (v.getId() == R.id.next)
		{
			if (PublicClass.nodeIndex < route.getAllStep().size() - 1)
			{
				PublicClass.nodeIndex++;
			} else
			{
				PublicClass.nodeIndex = 0;
			}
		} else if (v.getId() == R.id.pre)
		{
			if (PublicClass.nodeIndex > 0)
			{
				PublicClass.nodeIndex--;
			} else
			{
				PublicClass.nodeIndex = route.getAllStep().size() - 1;
			}
		}

		// ��ȡ�ڽ����Ϣ
		LatLng latLng = null;
		String title = null;

		Object step = route.getAllStep().get(PublicClass.nodeIndex);

		if (step instanceof WalkingRouteLine.WalkingStep)
		{
			latLng = ((WalkingRouteLine.WalkingStep) step).getEntrace()
					.getLocation();
			title = ((WalkingRouteLine.WalkingStep) step).getInstructions();
		} else if (step instanceof DrivingRouteLine.DrivingStep)
		{
			latLng = ((DrivingRouteLine.DrivingStep) step).getEntrace()
					.getLocation();
			title = ((DrivingRouteLine.DrivingStep) step).getInstructions();
		} else if (step instanceof TransitRouteLine.TransitStep)
		{
			latLng = ((TransitRouteLine.TransitStep) step).getEntrace()
					.getLocation();
			title = ((TransitRouteLine.TransitStep) step).getInstructions();
		}

		if (latLng == null || title == null)
		{
			return;
		}

		// �ƶ��ڵ�������
		MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.animateMapStatus(update, 500);

		detail_textView.setText(title);

		PublicClass.setNodeMarker(latLng, mBaiduMap);
	}
}
