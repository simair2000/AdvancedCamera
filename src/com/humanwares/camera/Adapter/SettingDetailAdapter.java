package com.humanwares.camera.Adapter;

import java.util.List;

import com.humanwares.camera.CameraApp;
import com.humanwares.camera.R;
import com.humanwares.camera.Activity.ActivitySWInfo;
import com.humanwares.camera.Activity.ActivitySendMail;
import com.humanwares.camera.Activity.GuideActivity;
import com.humanwares.camera.DB.UserData;
import com.humanwares.camera.Model.SettingmenuItem;
import com.humanwares.camera.View.CameraView;
import com.humanwares.camera.View.CameraView.PictureResolution;
import com.humanwares.camera.View.Setting;
import com.humanwares.camera.utils.Utils;

import android.content.Context;
import android.hardware.Camera;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingDetailAdapter extends ArrayAdapter<SettingmenuItem> {
	
	private LayoutInflater mInflater;
	private Context mContext;
	private List<SettingmenuItem> arrSettingMenu;
	private String strMode;

	
	public SettingDetailAdapter(Context context, List<SettingmenuItem> list ,String mode) {
		super(context,  0, list);
		mContext = context;
		arrSettingMenu = list;
		mInflater = LayoutInflater.from(context);
		strMode = mode;
		// TODO Auto-generated constructor stub
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(com.humanwares.camera.R.layout.row_setting_detail, parent, false);
		}
		final SettingmenuItem item = arrSettingMenu.get(position);
		ImageView img_icon =  (ImageView) convertView.findViewById(R.id.img_icon);
		final TextView tv_title = (TextView) convertView.findViewById(R.id.tv_title);
		final CameraApp app =  (CameraApp) mContext.getApplicationContext();
		final UserData userData = app.getUserData();
		ImageButton btnSelect = (ImageButton) convertView.findViewById(R.id.btn_select);
		img_icon.setImageResource(item.getnResID());
		tv_title.setText(item.getStrMenuName());
	
		convertView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if ( strMode.equals(mContext.getString(R.string.setting_flash)) ) {
					userData.strFlashMode = tv_title.getText().toString();
				} else if ( strMode.equals(mContext.getString(R.string.setting_grid)) ) {
					userData.setGridMode(tv_title.getText().toString());
				} else if (strMode.equals(mContext.getString(R.string.setting_mode))) {
					userData.strCameraMode = tv_title.getText().toString();
				} else if (strMode.equals(mContext.getString(R.string.setting_wb))) {
					userData.strWB = tv_title.getText().toString();
				} else if (strMode.equals(mContext.getString(R.string.setting_switch))) {
					userData.strSwitchCamera = tv_title.getText().toString();
				} else if (strMode.equals(mContext.getString(R.string.setting_gyro))) {
					userData.strGyro = tv_title.getText().toString();
				} else if ( strMode.equals(mContext.getString(R.string.setting_iso)) ) {
					userData.strISO = tv_title.getText().toString();
				} else if (strMode.equals(mContext.getString(R.string.setting_hdr))) {
					userData.strHDR = tv_title.getText().toString();
				} else if ( strMode.equals(mContext.getString(R.string.setting_confirm)) ) {
					userData.strConfirm = tv_title.getText().toString();
				} else if ( strMode.equals(mContext.getString(R.string.setting_sound)) ) {
					userData.strSound = tv_title.getText().toString();
				} else if ( strMode.equals(mContext.getString(R.string.setting_loc)) ) {
					userData.strLocation = tv_title.getText().toString();
				} else if ( strMode.equals(mContext.getString(R.string.setting_metering))) {
					userData.setStrMetering(tv_title.getText().toString());
				} else if ( strMode.equals(mContext.getString(R.string.setting_save))) {
					userData.setStrSave(tv_title.getText().toString());
				} else if(strMode.equals(mContext.getString(R.string.setting_app))) {
					onClickedAppMenu(tv_title);
				} else if(strMode.equals(mContext.getString(R.string.setting_photo))) {
					PictureResolution size = (PictureResolution)item.getData();
					CameraView.instance.setPictureSize(size.width, size.height);
				}
				
				notifyDataSetChanged();
			}
		});
	
		
		if ( strMode.equals(mContext.getString(R.string.setting_flash)) ) {
			if ( userData.strFlashMode.equals(tv_title.getText().toString()) ) {
				btnSelect.setBackgroundResource(R.drawable.icon01);
			} else {
				btnSelect.setBackgroundResource(R.drawable.ic_launcher);
			}
		} else if ( strMode.equals(mContext.getString(R.string.setting_grid)) ) {
			if ( userData.getGridMode().equals(tv_title.getText().toString()) ) {
				btnSelect.setBackgroundResource(R.drawable.icon01);
			} else {
				btnSelect.setBackgroundResource(R.drawable.ic_launcher);
			}
		} else if (strMode.equals(mContext.getString(R.string.setting_mode))) {
			if ( userData.strCameraMode.equals(tv_title.getText().toString()) ) {
				btnSelect.setBackgroundResource(R.drawable.icon01);
			} else {
				btnSelect.setBackgroundResource(R.drawable.ic_launcher);
			}
		} else if (strMode.equals(mContext.getString(R.string.setting_wb))) {
			if ( userData.strWB.equals(tv_title.getText().toString()) ) {
				btnSelect.setBackgroundResource(R.drawable.icon01);
			} else {
				btnSelect.setBackgroundResource(R.drawable.ic_launcher);
			}
		} else if (strMode.equals(mContext.getString(R.string.setting_switch))) {
			if ( userData.strSwitchCamera.equals(tv_title.getText().toString()) ) {
				btnSelect.setBackgroundResource(R.drawable.icon01);
			} else {
				btnSelect.setBackgroundResource(R.drawable.ic_launcher);
			}
		} else if (strMode.equals(mContext.getString(R.string.setting_gyro))) {
			if ( userData.strGyro.equals(tv_title.getText().toString()) ) {
				btnSelect.setBackgroundResource(R.drawable.icon01);
			} else {
				btnSelect.setBackgroundResource(R.drawable.ic_launcher);
			}
		} else if (strMode.equals(mContext.getString(R.string.setting_iso))) {
			
			if ( userData.strISO.equals(tv_title.getText().toString()) ) {
				btnSelect.setBackgroundResource(R.drawable.icon01);
			} else {
				btnSelect.setBackgroundResource(R.drawable.ic_launcher);
			}
		} else if (strMode.equals(mContext.getString(R.string.setting_hdr))) {
			if ( userData.strHDR.equals(tv_title.getText().toString()) ) {
				btnSelect.setBackgroundResource(R.drawable.icon01);
			} else {
				btnSelect.setBackgroundResource(R.drawable.ic_launcher);
			}
		} else if (strMode.equals(mContext.getString(R.string.setting_confirm))) {
			if ( userData.strConfirm.equals(tv_title.getText().toString()) ) {
				btnSelect.setBackgroundResource(R.drawable.icon01);
			} else {
				btnSelect.setBackgroundResource(R.drawable.ic_launcher);
			}
		} else if (strMode.equals(mContext.getString(R.string.setting_sound))) {
			if ( userData.strSound.equals(tv_title.getText().toString()) ) {
				btnSelect.setBackgroundResource(android.R.drawable.checkbox_on_background);
			} else {
				btnSelect.setBackgroundDrawable(null);
			}
		} else if (strMode.equals(mContext.getString(R.string.setting_loc))) {
			if ( userData.strLocation.equals(tv_title.getText().toString()) ) {
				btnSelect.setBackgroundResource(R.drawable.icon01);
			} else {
				btnSelect.setBackgroundResource(R.drawable.ic_launcher);
			}
		} else if (strMode.equals(mContext.getString(R.string.setting_metering))) {
			if ( userData.strMetering.equals(tv_title.getText().toString()) ) {
				btnSelect.setBackgroundResource(android.R.drawable.checkbox_on_background);
			} else {
				btnSelect.setBackgroundDrawable(null);
			}
		} else if (strMode.equals(mContext.getString(R.string.setting_save))) {
			if ( userData.strSave.equals(tv_title.getText().toString()) ) {
				btnSelect.setBackgroundResource(android.R.drawable.checkbox_on_background);
			} else {
				btnSelect.setBackgroundDrawable(null);
			}
		} else if (strMode.equals(mContext.getString(R.string.setting_photo))) {
			if ( CameraView.instance.getCurrentPictureSize().equals(tv_title.getText().toString()) ) {
				btnSelect.setBackgroundResource(android.R.drawable.checkbox_on_background);
			} else {
				btnSelect.setBackgroundDrawable(null);
			}
		}
		return convertView;
	}


	// 앱정보 > 하위 메뉴가 선택 됐을때
	protected void onClickedAppMenu(TextView tv_title) {
		Setting.hideSetting();
		String title = tv_title.getText().toString();
		if(title.equals(mContext.getString(R.string.setting_app_ask))) {
			// 문의하기
			mContext.startActivity(ActivitySendMail.getIntent(mContext));
		} else if(title.equals(mContext.getString(R.string.setting_app_guide))) {
			// 가이드
			mContext.startActivity(GuideActivity.getIntent(mContext));
		} else if(title.equals(mContext.getString(R.string.setting_app_sw_info))) {
			// 소프트웨어 정보
			mContext.startActivity(ActivitySWInfo.getIntent(mContext));
		} else if(title.equals(mContext.getString(R.string.setting_app_google_play))) {
			// 구글 플레이
			Utils.goGoogleMarket(mContext);
		}
	}
	
}
