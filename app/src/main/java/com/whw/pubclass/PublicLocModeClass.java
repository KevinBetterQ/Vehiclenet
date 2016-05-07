package com.whw.pubclass;

import android.widget.ImageButton;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.whw.vehiclenet.R;

/**
 * �Զ����࣬�����л���λģʽ
 * <p>
 * ���캯��������
 * <p>
 * 
 * //@param mapStatus
 *            ��ͼ״̬����
 * //@param mBaiduMap
 *            BaiduMap����
 * //@param btn_location
 *            ��λ��ť�������л���ͼ��λģʽ��
 * //@param mCurrentMode
 *            ��ǰ��λģʽ
 * 
 */
public class PublicLocModeClass
{

	private MapStatus mapStatus;
	private BaiduMap mBaiduMap;
	private ImageButton btn_location;
	private LocationMode mCurrentMode;

	public PublicLocModeClass(MapStatus mapStatus, BaiduMap mBaiduMap,
							  ImageButton btn_location, LocationMode mCurrentMode)
	{
		this.mapStatus = mapStatus;
		this.mBaiduMap = mBaiduMap;
		this.btn_location = btn_location;
		this.mCurrentMode = mCurrentMode;
	}

	/**
	 * ��λ ��ť����λģʽ�л�
	 * 
	 * **/
	public void changeMode()
	{
		switch (mCurrentMode)
		{
		case NORMAL:
			btn_location.setImageResource(R.drawable.loc_follow);

			mCurrentMode = LocationMode.FOLLOWING;
			mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
					mCurrentMode, true, null));
			break;

		case FOLLOWING:
			btn_location.setImageResource(R.drawable.loc_compass);

			mCurrentMode = LocationMode.COMPASS;
			mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
					mCurrentMode, true, null));
			break;

		case COMPASS:
			// ȡ����ͼ ������
			mapStatus = new MapStatus.Builder().overlook(0).build();
			MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory
					.newMapStatus(mapStatus);
			mBaiduMap.animateMapStatus(mapStatusUpdate);

			btn_location.setImageResource(R.drawable.loc_follow);

			mCurrentMode = LocationMode.FOLLOWING;
			mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
					mCurrentMode, true, null));
			break;

		default:
			break;
		}
	}
}
