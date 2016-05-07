package com.whw.pubclass;

import android.content.Context;
import android.widget.ImageView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.whw.vehiclenet.R;

/**
 * 自定义类，用于控制地图缩放
 * <p>
 * 构造函数参数：
 * <p>
 *
 *  zoomin
 *            地图放大按钮
 *  zoomout
 *            地图缩小按钮
 *  mBaiduMap
 *            BaiduMap对象
 *  context
 *            当前上下文，用于加载自定义弹窗布局
 *
 */
public class PublicZoomClass
{

	private static final float MAX_ZOOM_LEVEL = 19.0f;
	private static final float MIN_ZOOM_LEVEL = 12.0f;

	private ImageView zoomin;
	private ImageView zoomout;
	private BaiduMap mBaiduMap;
	private Context context;

	public PublicZoomClass(ImageView zoomin, ImageView zoomout,
						   BaiduMap mBaiduMap, Context context)
	{
		this.zoomin = zoomin;
		this.zoomout = zoomout;
		this.mBaiduMap = mBaiduMap;
		this.context = context;
	}

	public void zoom(int flag)
	{
		float currentLevel = mBaiduMap.getMapStatus().zoom;
		System.out.println("0-----currentLevel:" + currentLevel);

		if (flag == 1)
		{
			if (!zoomout.isClickable())
			{
				zoomout.setClickable(true);
				zoomout.setImageResource(R.drawable.zoom_out);
			}
			if (currentLevel < MAX_ZOOM_LEVEL)
			{
				MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
						.zoomIn();
				mBaiduMap.setMapStatus(mMapStatusUpdate);
			} else if (currentLevel == MAX_ZOOM_LEVEL)
			{
				zoomin.setClickable(false);
				zoomin.setImageResource(R.drawable.zoom_in_dis);
				PublicClass.showToast(context, "已缩放到最大级别！");

			}
		} else if (flag == 2)
		{
			if (!zoomin.isClickable())
			{
				zoomin.setClickable(true);
				zoomin.setImageResource(R.drawable.zoom_in);
			}
			if (currentLevel > MIN_ZOOM_LEVEL)
			{
				MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
						.zoomOut();
				mBaiduMap.setMapStatus(mMapStatusUpdate);
			} else if (currentLevel == MIN_ZOOM_LEVEL)
			{
				zoomout.setClickable(false);
				zoomout.setImageResource(R.drawable.zoom_out_dis);
				PublicClass.showToast(context, "已缩放到最小级别！");
			}
		}
	}
}
