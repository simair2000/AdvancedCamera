package com.humanwares.camera.Adapter;

import java.util.List;

import com.humanwares.camera.R;
import com.humanwares.camera.Model.SettingmenuItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingAdapter extends ArrayAdapter<SettingmenuItem> {
	
	private LayoutInflater mInflater;
	private Context mContext;
	private List<SettingmenuItem> arrSettingMenu;
	private SettingadapterListener listener;
	
	public interface SettingadapterListener {
		public void onSelectSetting(String title);
	}
	
	public SettingAdapter(Context context, List<SettingmenuItem> list, SettingadapterListener l) {
		super(context,  0, list);
		mContext = context;
		arrSettingMenu = list;
		mInflater = LayoutInflater.from(context);
		 listener = l;
		// TODO Auto-generated constructor stub
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(com.humanwares.camera.R.layout.row_setting, parent, false);
		}
		SettingmenuItem item = arrSettingMenu.get(position);
		ImageView img_icon =  (ImageView) convertView.findViewById(R.id.img_icon);
		final TextView tv_title = (TextView) convertView.findViewById(R.id.tv_title);
		TextView tv_default = (TextView) convertView.findViewById(R.id.tv_default);
		
		convertView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listener.onSelectSetting(tv_title.getText().toString());	
			}
		});
	
		img_icon.setImageResource(item.getnResID());
		tv_title.setText(item.getStrMenuName());
		tv_default.setText(item.getStrDefault());
		return convertView;
	}
	
}
