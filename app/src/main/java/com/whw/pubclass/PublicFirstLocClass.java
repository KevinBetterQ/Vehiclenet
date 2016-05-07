package com.whw.pubclass;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.whw.pubclass.MyOrientationListener.OnOrientationListener;
import com.whw.vehiclenet.R;

/**
 * �Զ��嶨λ��
 * 
 * @author LVGUANNAN
 * 
 *         <p>
 *         ʹ��˵����
 *         <p>
 *         1������PublicFirstLocClass���󣬹��캯������Context
 *         <p>
 *         2��ʹ�� initView() ������ʼ�� View �ؼ�
 *         <p>
 *         3��ʹ�� initMap() ������ʼ�� Map
 *         <p>
 *         4��ʹ�� initLoc() ������ʼ�� ��λ���
 *         <p>
 *         5��ʹ�� initOrientation() ������ʼ�� ���򴫸���
 *         <p>
 *         6������ startLoc() ��������ʼ��λ
 * 
 */
public class PublicFirstLocClass
{
	// �����ģ����캯���д���
	private Context context;

	// View���
	private View include_loading;
	private TextView textView_loading;
	private ImageView btn_location;

	// ��ͼ���
	private BaiduMap mBaiduMap;

	// ��λ���
	private LocationClient mLocClient;
	private LocationClientOption option;

	// ���򴫸������
	private MyOrientationListener myOrientationListener;

	// ������ mXDirection
	private float mXDirection;

	// �����ص��ӿڣ���ʼ��ʱʹ��
	private IRoutePlan routePlan;
	// ��־���������ж��Ƿ���Ҫ�ڶ�λ����·���滮�������Ҫ��������ʵ�ֽӿ��е���Ӧ����
	private boolean shouldRoutePlan = true;

	// �����е�˽���ֶΣ����⴫Ҳ�������ⲿ��ֵ
	private boolean isFirstLoc = true;
	private MyLocationData locationData;

	/**
	 * ���캯��
	 * 
	 * @param context
	 */
	public PublicFirstLocClass(Context context)
	{
		this.context = context;
	}

	/**
	 * ��ʼ�� View �ؼ�
	 * 
	 * @param include_loading
	 * @param textView_loading
	 * @param btn_location
	 */
	public void initView(View include_loading, TextView textView_loading,
						 ImageView btn_location)
	{
		this.include_loading = include_loading;
		this.textView_loading = textView_loading;
		this.btn_location = btn_location;
	}

	/**
	 * ��ʼ�� ��ͼ
	 * 
	 * @param mBaiduMap
	 */
	public void initMap(BaiduMap mBaiduMap)
	{
		this.mBaiduMap = mBaiduMap;
	}

	/**
	 * ��ʼ�� ��λ���
	 * 
	 * @param mLocClient
	 * @param option
	 */
	public void initLoc(LocationClient mLocClient, LocationClientOption option)
	{
		this.mLocClient = mLocClient;
		this.option = option;
		mLocClient.registerLocationListener(new MyLocationListener());
	}

	/**
	 * ��ʼ�� ���򴫸���
	 *
	 *  myOrientationListene
	 */
	public void initOrientation(MyOrientationListener myOrientationListener)
	{
		this.myOrientationListener = myOrientationListener;
	}

	public void startLoc()
	{
		include_loading.setVisibility(View.VISIBLE);
		textView_loading.setText("���ڶ�λ����λ��,���Ժ򡭡�");

		// ���� ��λģʽ ��ʼΪ ��ͨģʽ
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				LocationMode.NORMAL, true, null));

		if (PublicClass.isNetReachable(context)
				&& PublicClass.isGPSOpen(context))
		{
			option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
			System.out.println("Hight_Accuracy");
		} // ���ö�λģʽ
		else if (PublicClass.isNetReachable(context))
		{
			option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
			System.out.println("Battery_Saving");
		} else if (PublicClass.isGPSOpen(context))
		{
			option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
			System.out.println("Device_Sensors");
		} else
		{
			PublicClass.showToast(context, "�����GPS��Ϊ�������޷���λ��");
		}

		option.setCoorType("bd09ll"); // ���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
		option.setScanSpan(5 * 1000); // ���÷���λ����ļ��ʱ��Ϊ5000ms
		mLocClient.setLocOption(option);

		// ���� ��λ ����
		mLocClient.start();

		// �������򴫸���
		InitOritationListener();
		myOrientationListener.start();
	}

	/**
	 * ��ʼ�����򴫸���
	 * 
	 * **/
	private void InitOritationListener()
	{
		myOrientationListener
				.setOnOrientationListener(new OnOrientationListener()
				{
					@Override
					public void onOrientationChanged(float x)
					{
						mXDirection = x;
					}
				});
	}

	/**
	 * ��÷��򴫸��� ����ֵ
	 * 
	 * @return
	 */
	public float getmXDirection()
	{
		return mXDirection;
	}

	private class MyLocationListener implements BDLocationListener
	{
		@Override
		public void onReceiveLocation(BDLocation location)
		{
			if (location == null)
			{
				System.out.println("location == null");
				return;
			}

			LatLng myLocation = new LatLng(location.getLatitude(),
					location.getLongitude());

			// TODO
			if (PublicClass.isNetReachable(context)
					&& PublicClass.isGPSOpen(context))
			{
				option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
				System.out.println("Hight_Accuracy");
			} // ���ö�λģʽ
			else if (PublicClass.isNetReachable(context))
			{
				option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
				System.out.println("Battery_Saving");
			} else if (PublicClass.isGPSOpen(context))
			{
				option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
				System.out.println("Device_Sensors");
			}

			mLocClient.setLocOption(option);

			// ��ӡ��λ����codeֵ
			System.out.println("error code:" + location.getLocType());
			System.out.println("------------------------------------");

			if (location.getLocType() == 68)
			{
				PublicClass.showToast(context, "��ʱ�޷���ȡ����λ�ã��������磡");
				include_loading.setVisibility(View.INVISIBLE);
				return;
			}

			locationData = new MyLocationData.Builder()
					.accuracy(location.getRadius())//
					.direction(mXDirection)//
					.latitude(location.getLatitude())//
					.longitude(location.getLongitude())//
					.build();

			// ������λͼ��
			mBaiduMap.setMyLocationData(locationData);

			if (isFirstLoc)
			{
				// ���ζ�λ��ת����λ��
				LatLng latLng = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory
						.newLatLngZoom(latLng, 17);
				mBaiduMap.setMapStatus(mapStatusUpdate);

				// ���� ��λ �ؼ�
				btn_location.setImageResource(R.drawable.loc_location_suc);

				include_loading.setVisibility(View.INVISIBLE);
				isFirstLoc = false;
			}

			if (routePlan != null && shouldRoutePlan)
			{
				routePlan.startRoutePlan(myLocation);
				shouldRoutePlan = false;
			}
		}
	}

	public void initRoutePlan(IRoutePlan routePlan)
	{
		this.routePlan = routePlan;
	}

	/**
	 * �ص��ӿڣ������λ�� ��Ҫ ·���滮�������ʵ�ֽӿ��еķ���
	 * 
	 */
	public interface IRoutePlan
	{
		public void startRoutePlan(LatLng currentLocation);
	}
}
