package com.whw.pubclass;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.widget.Toast;


public class PublicClass
{
	/**
	 * 公共类维护常量 节点索引 字段
	 */
	public static int nodeIndex = -1;

	/**
	 * 读取当前SD卡状态
	 *
	 * **/
	public static final String SDCARD_STATE = Environment
			.getExternalStorageState();

	/**
	 * 得到SD卡根目录
	 *
	 * **/
	public static final String ROOT_PATH = Environment
			.getExternalStorageDirectory().toString();

	// TODO 添加变量

	/**
	 * 显示Toast
	 *
	 * **/
	public static void showToast(Context context, String str)
	{
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 判断移动网络是否连接
	 *
	 * @param context
	 *            当前上下文
	 * @return boolean
	 *
	 * **/
	public static boolean isNetworkReachable(Context context)
	{
		ConnectivityManager mManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo current = mManager.getActiveNetworkInfo();
		if (current == null)
		{
			return false;
		}

		return (current.getState() == NetworkInfo.State.CONNECTED);
	}

	/**
	 * 判断WIFI网络是否连接
	 *
	 * @param context
	 *            当前上下文
	 * @return boolean
	 *
	 * **/
	public static boolean isWIFIReachable(Context context)
	{
		ConnectivityManager mManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo current = mManager.getActiveNetworkInfo();
		if (current == null)
		{
			return false;
		}

		return (current.getType() == ConnectivityManager.TYPE_WIFI);
	}

	/**
	 * 判断网络是否连接
	 *
	 * @param context
	 *            当前上下文
	 * @return boolean
	 *
	 * **/
	public static boolean isNetReachable(Context context)
	{
		ConnectivityManager mManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo current = mManager.getActiveNetworkInfo();
		if (current == null)
		{
			return false;
		}

		return (current.getState() == NetworkInfo.State.CONNECTED)
				|| (current.getType() == ConnectivityManager.TYPE_WIFI);
	}

	/**
	 * 判断GPS是否开启
	 *
	 * @param context
	 *            当前上下文
	 * @return boolean
	 *
	 * **/
	public static boolean isGPSOpen(Context context)
	{
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	/**
	 * 根据输入的字符串，返回路径名；
	 * <p>
	 * 如：GetPathName("/SmartSchool/CharmKD")，返回：
	 * "/mnt/sdcard/SmartSchool/CharmKD"
	 *
	 * @param path
	 *            文件夹名称
	 * @return String
	 *
	 * **/
	public static String getPathName(String path)
	{
		return ROOT_PATH + path;
	}

	/**
	 * 根据输入的字符串，返回路径名；
	 * <p>
	 * 如：GetFileName("/SmartSchool/CharmKD", "一号楼",
	 * "png")，返回："/mnt/sdcard/SmartSchool/CharmKD/一号楼.png"
	 *
	 * @param path
	 *            文件夹名称
	 * @param fileName
	 *            文件名称
	 * @param format
	 *            文件格式
	 *
	 *
	 * @return String
	 *
	 * **/

}
