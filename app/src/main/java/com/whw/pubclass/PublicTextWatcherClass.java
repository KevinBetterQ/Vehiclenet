package com.whw.pubclass;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

/**
 * 自定义textWacther类
 * <p>
 * 使用说明:
 * <p>
 * 1、调用构造函数传入Context对象
 * <p>
 * 2、调用initTextWatcher()函数
 * <p>
 * 3、调用startTextWatcher()函数
 *
 */
public class PublicTextWatcherClass
{
	private Context context;

	private View view_loading;
	private TextView textView_loading;
	private ListView netSearchListView;

	private SuggestionSearch suggestionSearch;

	public PublicTextWatcherClass(Context context)
	{
		this.context = context;
	}

	public void initTextWatcher(View view_loading, TextView textView_loading,
								ListView netSearchListView, SuggestionSearch suggestionSearch)
	{
		this.view_loading = view_loading;
		this.textView_loading = textView_loading;
		this.netSearchListView = netSearchListView;
		this.suggestionSearch = suggestionSearch;
	}

	public void startTextWatcher(EditText editText)
	{
		editText.addTextChangedListener(new MyTextWatcher());
	}

	private class MyTextWatcher implements TextWatcher
	{
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
								  int count)
		{
			if (s.length() == 0)
			{
				view_loading.setVisibility(View.INVISIBLE);
				return;
			}

			if (!PublicClass.shouldSearch)
			{
				view_loading.setVisibility(View.INVISIBLE);
				PublicClass.shouldSearch = true;
				return;
			}

			if (!PublicClass.isNetReachable(context))
			{
				PublicClass.showToast(context, "获取在线结果失败，请检查网络");
				return;
			}

			view_loading.setVisibility(View.VISIBLE);
			if (!netSearchListView.isShown())
			{
				textView_loading.setText("正在请求在线查询……");
			} else
			{
				textView_loading.setText("");
			}

			// 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
			suggestionSearch.requestSuggestion((new SuggestionSearchOption())
					.keyword(s.toString()).city("青岛市"));

			System.out.println("关键字：" + s.toString() + ";城市：青岛");
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
									  int after)
		{

		}

		@Override
		public void afterTextChanged(Editable s)
		{

		}
	}
}
