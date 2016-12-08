package com.humanwares.camera.View;

import java.util.ArrayList;
import java.util.List;

import com.humanwares.camera.CameraApp;
import com.humanwares.camera.CameraException;
import com.humanwares.camera.R;
import com.humanwares.camera.Activity.MainActivity;
import com.humanwares.camera.Adapter.SettingAdapter;
import com.humanwares.camera.Adapter.SettingDetailAdapter;
import com.humanwares.camera.Adapter.SettingAdapter.SettingadapterListener;
import com.humanwares.camera.DB.UserData;
import com.humanwares.camera.Model.SettingmenuItem;
import com.humanwares.camera.View.CameraView.PictureResolution;
import com.humanwares.camera.utils.Utils;

import android.content.Context;
import android.hardware.Camera.Area;
import android.hardware.Camera.Size;
import android.os.Environment;
import android.provider.CalendarContract.Instances;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Setting extends RelativeLayout {

	private Context mContext;
	private ListView lvSetting;
	private ListView lvSettingDetail;
	private SettingAdapter adapter;
	private ImageButton btnClose;
	private CameraApp app;
	private RelativeLayout rlSetting;
	private RelativeLayout rlSettingDetail;
	private TextView tvDetailTitle;
	private ImageButton btnDetailClose;
	private OnSettingMenu settinglistener;
	public static Setting instance;
	
	
	public interface OnSettingMenu {
		public void onClose();
		public void onFlashSetting();
		public void onGridSetting();
		public void onModeSetting();
		public void onWBSetting();
		public void onSwitchCameraSetting();
		public void onGyroCameraSetting();
		public void onISOSetting();
		public void onHDRSetting();
		public void onShutter(String mode);
	}


	public Setting(Context context, OnSettingMenu l) {
		super(context);
		mContext = context;
		instance = this;
		app = (CameraApp)mContext.getApplicationContext();
		settinglistener = l;
		initUI();

	}
	
	public static void hideSetting() {
		if(instance != null) {
			instance.setVisibility(GONE);
			instance.rlSetting.setVisibility(View.VISIBLE);
			instance.rlSettingDetail.setVisibility(View.GONE);
			instance.initSetting();
		}
	}

	public void initUI() {
		String infService = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li;
		li = (LayoutInflater) getContext().getSystemService(infService);
		li.inflate(R.layout.view_setting, this, true);
		lvSetting = (ListView) findViewById(R.id.setting_list);
		lvSettingDetail = (ListView) findViewById(R.id.setting_detail_list);
		rlSetting = (RelativeLayout) findViewById(R.id.setting_view);
		rlSettingDetail = (RelativeLayout) findViewById(R.id.setting_detail_view);
		tvDetailTitle = (TextView) findViewById(R.id.setting_detail_title);
		rlSetting.setVisibility(View.VISIBLE);
		rlSettingDetail.setVisibility(View.GONE);
		btnDetailClose = (ImageButton) findViewById(R.id.setting_detail_close);
		btnDetailClose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if ( tvDetailTitle.getText().toString().equals(mContext.getString(R.string.setting_flash)) ) {
					settinglistener.onFlashSetting();
				} else if (tvDetailTitle.getText().toString().equals(mContext.getString(R.string.setting_grid))) {
					settinglistener.onGridSetting();
				} else if ( tvDetailTitle.getText().toString().equals(mContext.getString(R.string.setting_mode)) ) {
					settinglistener.onModeSetting();
				} else if ( tvDetailTitle.getText().toString().equals(mContext.getString(R.string.setting_wb)) ) {
					settinglistener.onWBSetting();
				} else if ( tvDetailTitle.getText().toString().equals(mContext.getString(R.string.setting_switch)) ) {
					settinglistener.onSwitchCameraSetting();
				} else if ( tvDetailTitle.getText().toString().equals(mContext.getString(R.string.setting_gyro)) ) {
					settinglistener.onGyroCameraSetting();
				}  else if ( tvDetailTitle.getText().toString().equals(mContext.getString(R.string.setting_hdr)) ) {
					settinglistener.onHDRSetting();
				}
				rlSetting.setVisibility(View.VISIBLE);
				rlSettingDetail.setVisibility(View.GONE);
				initSetting();
			}
		});
		btnClose = (ImageButton) findViewById(R.id.setting_close);
		btnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setVisibility(View.GONE);
			}
		});
		initSetting();
	}

	// 초기 설정 
	public void initSetting() {
		ArrayList<SettingmenuItem> items = new ArrayList<SettingmenuItem>();
		initWB(items);
		//initBR(items);
		initSwitchCamera(items);
		try {
			if(MainActivity.getCameraView() != null && MainActivity.getCameraView().isSupportMeteringMode()) {
				initMetering(items);
			}
		} catch (CameraException e) {
			e.printStackTrace();
		}
		initFlash(items);
		//initTimer(items);
		initGrid(items);
		initShutter(items);
		//initZoom(items);
		//initCam(items);
		initPhoto(items);
		initLocation(items);
		//initLang(items);
		initMode(items);
		initSave(items);
		initGyro(items);
		initIOS(items);
		initHDR(items);
		initSound(items);
		initConfirm(items);
		//initTheme(items);
		//initShare(items);
		initApp(items);
		adapter = new SettingAdapter(mContext, items, listener);
		lvSetting.setAdapter(adapter);
	}

	// 화이트 밸런스 설정값
	private void initWB( ArrayList<SettingmenuItem> items ) {
		SettingmenuItem item = new SettingmenuItem();
		item.setStrMenuName(mContext.getString(R.string.setting_wb));
		item.setnResID(R.drawable.ic_launcher);
		
		if (app.getUserData().strWB.equals(mContext.getString(R.string.setting_common_auto))) {
			item.setStrDefault(mContext.getString(R.string.setting_common_auto));
		} else if ( app.getUserData().strWB.equals(mContext.getString(R.string.setting_wb_cloudydaylight)) ) {
			item.setStrDefault(mContext.getString(R.string.setting_wb_cloudydaylight));
		} else if ( app.getUserData().strWB.equals(mContext.getString(R.string.setting_wb_daylight)) ) {
			item.setStrDefault(mContext.getString(R.string.setting_wb_daylight));
		} else if ( app.getUserData().strWB.equals(mContext.getString(R.string.setting_wb_fluorescent)) ) {
			item.setStrDefault(mContext.getString(R.string.setting_wb_fluorescent));
		} else if ( app.getUserData().strWB.equals(mContext.getString(R.string.setting_wb_incandescent)) ) {
			item.setStrDefault(mContext.getString(R.string.setting_wb_incandescent));
		}
		items.add(item);
	}

	// 밝기
	private void initBR (ArrayList<SettingmenuItem> items) {
		SettingmenuItem item = new SettingmenuItem();
		item.setnResID(R.drawable.ic_launcher);
		item.setStrMenuName(mContext.getString(R.string.setting_br));
		item.setStrDefault("0");
		items.add(item);
	}


	// 준후면카메라
	private void initSwitchCamera (ArrayList<SettingmenuItem> items) {
		SettingmenuItem item = new SettingmenuItem();
		item.setnResID(R.drawable.ic_launcher);
		item.setStrMenuName(mContext.getString(R.string.setting_switch));
		if ( app.getUserData().strSwitchCamera.equals(mContext.getString(R.string.setting_switch_f)) ) {
			item.setStrDefault(mContext.getString(R.string.setting_switch_f));
		} else {
			item.setStrDefault(mContext.getString(R.string.setting_switch_r));
		}
		
		items.add(item);
	}

	// 측광
	private void initMetering (ArrayList<SettingmenuItem> items) {
		SettingmenuItem item = new SettingmenuItem();
		item.setnResID(R.drawable.ic_launcher);
		item.setStrMenuName(mContext.getString(R.string.setting_metering));
		item.setStrDefault(app.getUserData().getStrMetering());
		items.add(item);
	}

	// flash
	private void initFlash (ArrayList<SettingmenuItem> items) {
		SettingmenuItem item = new SettingmenuItem();
		item.setnResID(R.drawable.ic_launcher);
		Log.e("", " app.getUserData().strFlashMode = "+  app.getUserData().strFlashMode);
		if ( app.getUserData().strFlashMode.equals(mContext.getString(R.string.setting_common_auto)) ) {
			item.setStrMenuName(mContext.getString(R.string.setting_flash));
			item.setStrDefault(mContext.getString(R.string.setting_common_auto));
			items.add(item);	
		} else if (app.getUserData().strFlashMode.equals(mContext.getString(R.string.setting_common_on))) {
			item.setStrMenuName(mContext.getString(R.string.setting_flash));
			item.setStrDefault(mContext.getString(R.string.setting_common_on));
			items.add(item);
		} else {
			item.setStrMenuName(mContext.getString(R.string.setting_flash));
			item.setStrDefault(mContext.getString(R.string.setting_common_off));
			items.add(item);
		}

	}

	// 타이머
	private void initTimer (ArrayList<SettingmenuItem> items) {
		SettingmenuItem item = new SettingmenuItem();
		
		item.setnResID(R.drawable.ic_launcher);
		item.setStrMenuName(mContext.getString(R.string.setting_timer));
		item.setStrDefault(mContext.getString(R.string.setting_common_off));
		items.add(item);
	}

	// 그리드
	private void initGrid (ArrayList<SettingmenuItem> items) {
		SettingmenuItem item = new SettingmenuItem();
		if ( app.getUserData().getGridMode().equals(mContext.getString(R.string.setting_common_off)) ) {
			item.setnResID(R.drawable.ic_launcher);
			item.setStrMenuName(mContext.getString(R.string.setting_grid));
			item.setStrDefault(mContext.getString(R.string.setting_common_off));
			items.add(item);
		} else {
			item.setnResID(R.drawable.ic_launcher);
			item.setStrMenuName(mContext.getString(R.string.setting_grid));
			item.setStrDefault(mContext.getString(R.string.setting_common_on));
			items.add(item);
		}
		
	}

	// 셔터이동
	private void initShutter (ArrayList<SettingmenuItem> items) {
		SettingmenuItem item = new SettingmenuItem();
		item.setnResID(R.drawable.ic_launcher);
		item.setStrMenuName(mContext.getString(R.string.setting_shutter));
		item.setStrDefault("");
		items.add(item);
	}

	// 줌
	private void initZoom (ArrayList<SettingmenuItem> items) {
		SettingmenuItem item = new SettingmenuItem();
		item.setnResID(R.drawable.ic_launcher);
		item.setStrMenuName(mContext.getString(R.string.setting_zoom));
		item.setStrDefault("1x");
		items.add(item);
	}

	// 동영상
	private void initCam (ArrayList<SettingmenuItem> items) {
		SettingmenuItem item = new SettingmenuItem();
		item.setnResID(R.drawable.ic_launcher);
		item.setStrMenuName(mContext.getString(R.string.setting_cam));
		item.setStrDefault("1x");
		items.add(item);
	}

	// 사진
	private void initPhoto (ArrayList<SettingmenuItem> items) {
		SettingmenuItem item = new SettingmenuItem();
		item.setnResID(R.drawable.ic_launcher);
		item.setStrMenuName(mContext.getString(R.string.setting_photo));
		item.setStrDefault(CameraView.instance.getCurrentPictureSize().title);
		items.add(item);
	}

	// 위치
	private void initLocation (ArrayList<SettingmenuItem> items) {
		SettingmenuItem item = new SettingmenuItem();
		item.setnResID(R.drawable.ic_launcher);
		item.setStrMenuName(mContext.getString(R.string.setting_loc));
		if (app.getUserData().strLocation.equals(mContext.getString(R.string.setting_common_off))) {
			item.setStrDefault(mContext.getString(R.string.setting_common_off));
		} else {
			item.setStrDefault(mContext.getString(R.string.setting_common_on));
		}
	
		items.add(item);
	}

	// 언어
	private void initLang (ArrayList<SettingmenuItem> items) {
		SettingmenuItem item = new SettingmenuItem();
		item.setnResID(R.drawable.ic_launcher);
		item.setStrMenuName(mContext.getString(R.string.setting_lang));
		item.setStrDefault("한국어");
		items.add(item);
	}

	// 모드
	private void initMode (ArrayList<SettingmenuItem> items) {
		SettingmenuItem item = new SettingmenuItem();
		if (app.getUserData().strCameraMode.equals(mContext.getString(R.string.setting_common_auto))) {
			item.setStrDefault(mContext.getString(R.string.setting_common_auto));
		} else if ( app.getUserData().strCameraMode.equals(mContext.getString(R.string.setting_mode_landscape)) ) {
			item.setStrDefault(mContext.getString(R.string.setting_mode_landscape));
		} else if ( app.getUserData().strCameraMode.equals(mContext.getString(R.string.setting_mode_portrait)) ) {
			item.setStrDefault(mContext.getString(R.string.setting_mode_portrait));
		} else if ( app.getUserData().strCameraMode.equals(mContext.getString(R.string.setting_mode_sports)) ) {
			item.setStrDefault(mContext.getString(R.string.setting_mode_sports));
		} else if ( app.getUserData().strCameraMode.equals(mContext.getString(R.string.setting_mode_night)) ) {
			item.setStrDefault(mContext.getString(R.string.setting_mode_night));
		} else if ( app.getUserData().strCameraMode.equals(mContext.getString(R.string.setting_mode_beauty)) ) {
			item.setStrDefault(mContext.getString(R.string.setting_mode_beauty));
		} else if ( app.getUserData().strCameraMode.equals(mContext.getString(R.string.setting_mode_food)) ) {
			item.setStrDefault(mContext.getString(R.string.setting_mode_food));
		} 
		item.setnResID(R.drawable.ic_launcher);
		item.setStrMenuName(mContext.getString(R.string.setting_mode));
		
		items.add(item);
	}

	// 저장위치
	private void initSave (ArrayList<SettingmenuItem> items) {
		SettingmenuItem item = new SettingmenuItem();
		item.setnResID(R.drawable.ic_launcher);
		item.setStrMenuName(mContext.getString(R.string.setting_save));
		item.setStrDefault(app.getUserData().getStrSave());
		items.add(item);
	}

	// 자이로
	private void initGyro (ArrayList<SettingmenuItem> items) {
		SettingmenuItem item = new SettingmenuItem();
		item.setnResID(R.drawable.ic_launcher);
		item.setStrMenuName(mContext.getString(R.string.setting_gyro));
		if ( app.getUserData().strGyro.equals(R.string.setting_common_off) ) {
			item.setStrDefault(mContext.getString(R.string.setting_common_off));
		} else {
			item.setStrDefault(mContext.getString(R.string.setting_common_on));
		}
		
		items.add(item);
	}

	// ISO
	private void initIOS (ArrayList<SettingmenuItem> items) {
		SettingmenuItem item = new SettingmenuItem();
		item.setnResID(R.drawable.ic_launcher);
		item.setStrMenuName(mContext.getString(R.string.setting_iso));
		
		if ( app.getUserData().strISO.equalsIgnoreCase(mContext.getString(R.string.setting_common_auto)) ) {
			item.setStrDefault(mContext.getString(R.string.setting_common_auto));
		} else {
			item.setStrDefault(app.getUserData().strISO);
		}
		items.add(item);
	}

	// HDR
	private void initHDR (ArrayList<SettingmenuItem> items) {
		SettingmenuItem item = new SettingmenuItem();
		item.setnResID(R.drawable.ic_launcher);
		item.setStrMenuName(mContext.getString(R.string.setting_hdr));
		if ( app.getUserData().strHDR.equals(mContext.getString(R.string.setting_common_off)) ) {
			item.setStrDefault(mContext.getString(R.string.setting_common_off));
		} else {
			item.setStrDefault(mContext.getString(R.string.setting_common_on));
		}
		items.add(item);
	}

	// 셔터음
	private void initSound (ArrayList<SettingmenuItem> items) {
		SettingmenuItem item = new SettingmenuItem();
		item.setnResID(R.drawable.ic_launcher);
		item.setStrMenuName(mContext.getString(R.string.setting_sound));
		if ( app.getUserData().strSound.equals(mContext.getString(R.string.setting_common_off)) ) {
			item.setStrDefault(mContext.getString(R.string.setting_common_off));
		} else {
			item.setStrDefault(mContext.getString(R.string.setting_common_on));
		}
	
		items.add(item);
	}

	// 촬영 후 확인
	private void initConfirm (ArrayList<SettingmenuItem> items) {
		SettingmenuItem item = new SettingmenuItem();
		item.setnResID(R.drawable.ic_launcher);
		if ( app.getUserData().strConfirm.equals(mContext.getString(R.string.setting_common_off)) ) {
			item.setStrDefault(mContext.getString(R.string.setting_common_off));
		} else {
			item.setStrDefault(mContext.getString(R.string.setting_common_on));
		}
		item.setStrMenuName(mContext.getString(R.string.setting_confirm));
	
		items.add(item);
	}

	// 공유
	private void initShare (ArrayList<SettingmenuItem> items) {
		SettingmenuItem item = new SettingmenuItem();
		item.setnResID(R.drawable.ic_launcher);
		item.setStrMenuName(mContext.getString(R.string.setting_share));
		item.setStrDefault("");
		items.add(item);
	}

	// 테마
	private void initTheme (ArrayList<SettingmenuItem> items) {
		SettingmenuItem item = new SettingmenuItem();
		item.setnResID(R.drawable.ic_launcher);
		item.setStrMenuName(mContext.getString(R.string.setting_theme));
		item.setStrDefault(mContext.getString(R.string.setting_theme_d));
		items.add(item);
	}

	private void initApp(ArrayList<SettingmenuItem> items) {
		SettingmenuItem item = new SettingmenuItem();
		item.setnResID(R.drawable.ic_launcher);
		item.setStrMenuName(mContext.getString(R.string.setting_app));
		item.setStrDefault("");
		items.add(item);
	}
	
	// 설정에 선택시
	private SettingadapterListener listener = new SettingadapterListener() {

		@Override
		public void onSelectSetting(String title) {
			rlSetting.setVisibility(View.GONE);
			rlSettingDetail.setVisibility(View.VISIBLE);
			showDetail(title);
		}
	};


	// 메뉴에 맞게 상세 호출
	private void showDetail(String title) {
		tvDetailTitle.setText(title);
		if ( title.equals(mContext.getString(R.string.setting_flash)) ) {
			showDetailFlash();
		} else if ( title.equals(mContext.getString(R.string.setting_grid)) ) {
			showDetailGrid();
		} else if (  title.equals(mContext.getString(R.string.setting_mode)) ) {
			showDetailMode();
		} else if (  title.equals(mContext.getString(R.string.setting_wb)) ) {
			showWBDetail();
		} else if ( title.equals(mContext.getString(R.string.setting_switch)) ) {
			showSwitchCamera();
		} else if ( title.equals(mContext.getString(R.string.setting_gyro)) ) {
			showGyro();
		} else if ( title.equals(mContext.getString(R.string.setting_iso)) ) {
			if ( app.getUserData().strISO.equals(mContext.getString(R.string.no_hdr)) ) {
				tvDetailTitle.setText(mContext.getString(R.string.setting_title));
				rlSetting.setVisibility(View.VISIBLE);
				rlSettingDetail.setVisibility(View.GONE);
				Toast toast = Toast.makeText(mContext, mContext.getString(R.string.no_hdr), Toast.LENGTH_LONG);
				toast.show();
			} else {
				showISO();
			}
		} else if (  title.equals(mContext.getString(R.string.setting_hdr)) ) {
			if ( app.getUserData().strHDR.equals(mContext.getString(R.string.no_hdr)) ) {
				tvDetailTitle.setText(mContext.getString(R.string.setting_title));
				rlSetting.setVisibility(View.VISIBLE);
				rlSettingDetail.setVisibility(View.GONE);
				Toast toast = Toast.makeText(mContext, mContext.getString(R.string.no_hdr), Toast.LENGTH_LONG);
				toast.show();
			} else {
				showHDR();
			}
		} else if ( title.equals(mContext.getString(R.string.setting_confirm)) ) {
			showConfirm();
		} else if ( title.equals(mContext.getString(R.string.setting_sound)) ) {
			showSound();
		} else if (title.equals(mContext.getString(R.string.setting_loc))) {
			showLocation();
		} else if ( title.equals(mContext.getString(R.string.setting_shutter)) ) {
//			showShutter();
			settinglistener.onShutter("land");
			setVisibility(View.GONE);
			rlSettingDetail.setVisibility(View.GONE);
			
		} else if(title.equals(mContext.getString(R.string.setting_metering))) {
			showMetering();
		} else if(title.equals(mContext.getString(R.string.setting_save))) {
			showSave();
		} else if(title.equals(mContext.getString(R.string.setting_app))) {
			showApp();
		} else if(title.equals(mContext.getString(R.string.setting_photo))) {
			showPicture();
		}
	}
	
	private void showPicture() {
		// TODO Auto-generated method stub
		ArrayList<SettingmenuItem> items = new ArrayList<SettingmenuItem>();
		
		List<PictureResolution> list = CameraView.instance.getSupportedPictureSizes();
		
		for(PictureResolution size : list) {
			SettingmenuItem v = new SettingmenuItem();
			v.setStrMenuName(size.title);
			v.setnResID(-1);
			v.setData(size);
			items.add(v);
		}
		
		SettingDetailAdapter adapter = new SettingDetailAdapter(mContext, items,mContext.getString(R.string.setting_photo) );
		lvSettingDetail.setAdapter(adapter);
	}

	private void showApp() {
		// 앱정보
		ArrayList<SettingmenuItem> items = new ArrayList<SettingmenuItem>();
		
		SettingmenuItem v = new SettingmenuItem();
		v.setStrMenuName(mContext.getString(R.string.setting_app_ask));
		v.setnResID(R.drawable.ic_launcher);
		items.add(v);
		
		SettingmenuItem v1 = new SettingmenuItem();
		v1.setStrMenuName(mContext.getString(R.string.setting_app_guide));
		v1.setnResID(R.drawable.ic_launcher);
		items.add(v1);
		
		SettingmenuItem v2 = new SettingmenuItem();
		v2.setStrMenuName(mContext.getString(R.string.setting_app_sw_info));
		v2.setnResID(R.drawable.ic_launcher);
		items.add(v2);
		
		SettingmenuItem v3 = new SettingmenuItem();
		v3.setStrMenuName(mContext.getString(R.string.setting_app_google_play));
		v3.setnResID(R.drawable.ic_launcher);
		items.add(v3);
		
		SettingDetailAdapter adapter = new SettingDetailAdapter(mContext, items,mContext.getString(R.string.setting_app) );
		lvSettingDetail.setAdapter(adapter);
	}

	// 저장위치
	private void showSave() {
		ArrayList<SettingmenuItem> items = new ArrayList<SettingmenuItem>();
		
		SettingmenuItem v = new SettingmenuItem();
		v.setStrMenuName(mContext.getString(R.string.setting_save_phone));
		v.setnResID(R.drawable.ic_launcher);
		items.add(v);
		
		if(Utils.isSDCardExist()) {
			SettingmenuItem v1 = new SettingmenuItem();
			v1.setStrMenuName(mContext.getString(R.string.setting_save_external));
			v1.setnResID(R.drawable.ic_launcher);
			items.add(v1);
		}
		
		SettingDetailAdapter adapter = new SettingDetailAdapter(mContext, items,mContext.getString(R.string.setting_save) );
		lvSettingDetail.setAdapter(adapter);
	}

	// 측광
	private void showMetering() {
		// TODO Auto-generated method stub
		ArrayList<SettingmenuItem> items = new ArrayList<SettingmenuItem>();
		
		SettingmenuItem v = new SettingmenuItem();
		v.setStrMenuName(mContext.getString(R.string.setting_metering_c));
		v.setnResID(R.drawable.ic_launcher);
		items.add(v);
		
		SettingmenuItem v1 = new SettingmenuItem();
		v1.setStrMenuName(mContext.getString(R.string.setting_metering_m));
		v1.setnResID(R.drawable.ic_launcher);
		items.add(v1);
		
		SettingmenuItem v2 = new SettingmenuItem();
		v2.setStrMenuName(mContext.getString(R.string.setting_metering_s));
		v2.setnResID(R.drawable.ic_launcher);
		items.add(v2);
		
		SettingDetailAdapter adapter = new SettingDetailAdapter(mContext, items,mContext.getString(R.string.setting_metering) );
		lvSettingDetail.setAdapter(adapter);
	}

	// 셔터
	private void showShutter() {
		ArrayList<SettingmenuItem> items = new ArrayList<SettingmenuItem>();
		SettingmenuItem v = new SettingmenuItem();
		v.setStrMenuName(mContext.getString(R.string.setting_shutter_l));
		v.setnResID(R.drawable.ic_launcher);
		items.add(v);
		SettingmenuItem v1 = new SettingmenuItem();
		v1.setStrMenuName(mContext.getString(R.string.setting_shutter_r));
		v1.setnResID(R.drawable.ic_launcher);
		items.add(v1);
		SettingDetailAdapter adapter = new SettingDetailAdapter(mContext, items,mContext.getString(R.string.setting_shutter) );
		lvSettingDetail.setAdapter(adapter);
	}
	
	
	// 로케이
	private void showLocation() {
		ArrayList<SettingmenuItem> items = new ArrayList<SettingmenuItem>();
		SettingmenuItem v = new SettingmenuItem();
		v.setStrMenuName(mContext.getString(R.string.setting_common_off));
		v.setnResID(R.drawable.ic_launcher);
		items.add(v);
		SettingmenuItem v1 = new SettingmenuItem();
		v1.setStrMenuName(mContext.getString(R.string.setting_common_on));
		v1.setnResID(R.drawable.ic_launcher);
		items.add(v1);
		SettingDetailAdapter adapter = new SettingDetailAdapter(mContext, items,mContext.getString(R.string.setting_loc) );
		lvSettingDetail.setAdapter(adapter);
	}
	
	
	// 셔터음
	private void showSound() {
		ArrayList<SettingmenuItem> items = new ArrayList<SettingmenuItem>();
		SettingmenuItem v = new SettingmenuItem();
		v.setStrMenuName(mContext.getString(R.string.setting_common_off));
		v.setnResID(R.drawable.ic_launcher);
		items.add(v);
		SettingmenuItem v1 = new SettingmenuItem();
		v1.setStrMenuName(mContext.getString(R.string.setting_common_on));
		v1.setnResID(R.drawable.ic_launcher);
		items.add(v1);
		SettingDetailAdapter adapter = new SettingDetailAdapter(mContext, items,mContext.getString(R.string.setting_sound) );
		lvSettingDetail.setAdapter(adapter);
	}
	// confirm
	private void showConfirm() {
		ArrayList<SettingmenuItem> items = new ArrayList<SettingmenuItem>();
		SettingmenuItem v = new SettingmenuItem();
		v.setStrMenuName(mContext.getString(R.string.setting_common_off));
		v.setnResID(R.drawable.ic_launcher);
		items.add(v);
		SettingmenuItem v1 = new SettingmenuItem();
		v1.setStrMenuName(mContext.getString(R.string.setting_common_on));
		v1.setnResID(R.drawable.ic_launcher);
		items.add(v1);
		SettingDetailAdapter adapter = new SettingDetailAdapter(mContext, items,mContext.getString(R.string.setting_confirm) );
		lvSettingDetail.setAdapter(adapter);
	}
	
	// HDR
	private void showHDR() {
		ArrayList<SettingmenuItem> items = new ArrayList<SettingmenuItem>();
		SettingmenuItem v = new SettingmenuItem();
		v.setStrMenuName(mContext.getString(R.string.setting_common_off));
		v.setnResID(R.drawable.ic_launcher);
		items.add(v);
		SettingmenuItem v1 = new SettingmenuItem();
		v1.setStrMenuName(mContext.getString(R.string.setting_common_on));
		v1.setnResID(R.drawable.ic_launcher);
		items.add(v1);
		SettingDetailAdapter adapter = new SettingDetailAdapter(mContext, items,mContext.getString(R.string.setting_hdr) );
		lvSettingDetail.setAdapter(adapter);
	}
	
	// iso
	private void showISO() {
		ArrayList<SettingmenuItem> items = new ArrayList<SettingmenuItem>();
		
		for ( int i =0; i< app.getUserData().arrISO.length; i++ ) {
			SettingmenuItem v = new SettingmenuItem();
			v.setStrMenuName(app.getUserData().arrISO[i]);
			v.setnResID(R.drawable.ic_launcher);
			items.add(v);
		}
	
	
		SettingDetailAdapter adapter = new SettingDetailAdapter(mContext, items,mContext.getString(R.string.setting_iso) );
		lvSettingDetail.setAdapter(adapter);
	}
	
	// 자이로 
	private void showGyro() {
		ArrayList<SettingmenuItem> items = new ArrayList<SettingmenuItem>();
		SettingmenuItem v = new SettingmenuItem();
		v.setStrMenuName(mContext.getString(R.string.setting_common_off));
		v.setnResID(R.drawable.ic_launcher);
		items.add(v);
		SettingmenuItem v1 = new SettingmenuItem();
		v1.setStrMenuName(mContext.getString(R.string.setting_common_on));
		v1.setnResID(R.drawable.ic_launcher);
		items.add(v1);
		SettingDetailAdapter adapter = new SettingDetailAdapter(mContext, items,mContext.getString(R.string.setting_gyro) );
		lvSettingDetail.setAdapter(adapter);
	}
	
	// 카메라 전후면
	private void showSwitchCamera() {
		ArrayList<SettingmenuItem> items = new ArrayList<SettingmenuItem>();
		SettingmenuItem v = new SettingmenuItem();
		v.setStrMenuName(mContext.getString(R.string.setting_switch_f));
		v.setnResID(R.drawable.ic_launcher);
		items.add(v);
		SettingmenuItem v1 = new SettingmenuItem();
		v1.setStrMenuName(mContext.getString(R.string.setting_switch_r));
		v1.setnResID(R.drawable.ic_launcher);
		items.add(v1);
		SettingDetailAdapter adapter = new SettingDetailAdapter(mContext, items,mContext.getString(R.string.setting_switch) );
		lvSettingDetail.setAdapter(adapter);
	}
	
	// WB 상세 호출
	private void showWBDetail() {
		ArrayList<SettingmenuItem> items = new ArrayList<SettingmenuItem>();
		SettingmenuItem v = new SettingmenuItem();
		v.setStrMenuName(mContext.getString(R.string.setting_common_auto));
		v.setnResID(R.drawable.ic_launcher);
		items.add(v);
		SettingmenuItem v1 = new SettingmenuItem();
		v1.setStrMenuName(mContext.getString(R.string.setting_wb_daylight));
		v1.setnResID(R.drawable.ic_launcher);
		items.add(v1);
		SettingmenuItem v2 = new SettingmenuItem();
		v2.setStrMenuName(mContext.getString(R.string.setting_wb_cloudydaylight));
		v2.setnResID(R.drawable.ic_launcher);
		items.add(v2);
		SettingmenuItem v3 = new SettingmenuItem();
		v3.setStrMenuName(mContext.getString(R.string.setting_wb_incandescent));
		v3.setnResID(R.drawable.ic_launcher);
		items.add(v3);
		SettingmenuItem v4 = new SettingmenuItem();
		v4.setStrMenuName(mContext.getString(R.string.setting_wb_fluorescent));
		v4.setnResID(R.drawable.ic_launcher);
		items.add(v4);
		SettingDetailAdapter adapter = new SettingDetailAdapter(mContext, items,mContext.getString(R.string.setting_wb) );
		lvSettingDetail.setAdapter(adapter);
	}
	
	
	// 모드 상세 호출
	private void showDetailMode() {
		ArrayList<SettingmenuItem> items = new ArrayList<SettingmenuItem>();
		SettingmenuItem v = new SettingmenuItem();
		v.setStrMenuName(mContext.getString(R.string.setting_common_auto));
		v.setnResID(R.drawable.ic_launcher);
		items.add(v);
		SettingmenuItem v1 = new SettingmenuItem();
		v1.setStrMenuName(mContext.getString(R.string.setting_mode_landscape));
		v1.setnResID(R.drawable.ic_launcher);
		items.add(v1);
		SettingmenuItem v2 = new SettingmenuItem();
		v2.setStrMenuName(mContext.getString(R.string.setting_mode_portrait));
		v2.setnResID(R.drawable.ic_launcher);
		items.add(v2);
		SettingmenuItem v3 = new SettingmenuItem();
		v3.setStrMenuName(mContext.getString(R.string.setting_mode_sports));
		v3.setnResID(R.drawable.ic_launcher);
		items.add(v3);
		SettingmenuItem v4 = new SettingmenuItem();
		v4.setStrMenuName(mContext.getString(R.string.setting_mode_night));
		v4.setnResID(R.drawable.ic_launcher);
		items.add(v4);
		SettingmenuItem v5 = new SettingmenuItem();
		v5.setStrMenuName(mContext.getString(R.string.setting_mode_beauty));
		v5.setnResID(R.drawable.ic_launcher);
		items.add(v5);
		SettingmenuItem v6 = new SettingmenuItem();
		v6.setStrMenuName(mContext.getString(R.string.setting_mode_food));
		v6.setnResID(R.drawable.ic_launcher);
		items.add(v6);
		SettingDetailAdapter adapter = new SettingDetailAdapter(mContext, items,mContext.getString(R.string.setting_mode) );
		lvSettingDetail.setAdapter(adapter);
	}
	// 그리드 상세 호출
	private void showDetailGrid() {
		ArrayList<SettingmenuItem> items = new ArrayList<SettingmenuItem>();
		SettingmenuItem v = new SettingmenuItem();
		v.setStrMenuName(mContext.getString(R.string.setting_common_off));
		v.setnResID(R.drawable.ic_launcher);
		items.add(v);
		SettingmenuItem v1 = new SettingmenuItem();
		v1.setStrMenuName(mContext.getString(R.string.setting_common_on));
		v1.setnResID(R.drawable.ic_launcher);
		items.add(v1);
	
		SettingDetailAdapter adapter = new SettingDetailAdapter(mContext, items,mContext.getString(R.string.setting_grid) );
		lvSettingDetail.setAdapter(adapter);
	}
	
	
	// 플래쉬 상세 호출
	private void showDetailFlash() {
		ArrayList<SettingmenuItem> items = new ArrayList<SettingmenuItem>();
		SettingmenuItem v = new SettingmenuItem();
		v.setStrMenuName(mContext.getString(R.string.setting_common_auto));
		v.setnResID(R.drawable.ic_launcher);
		items.add(v);
		SettingmenuItem v1 = new SettingmenuItem();
		v1.setStrMenuName(mContext.getString(R.string.setting_common_off));
		v1.setnResID(R.drawable.ic_launcher);
		items.add(v1);
		SettingmenuItem v2 = new SettingmenuItem();
		v2.setStrMenuName(mContext.getString(R.string.setting_common_on));
		v2.setnResID(R.drawable.ic_launcher);
		items.add(v2);
		SettingDetailAdapter adapter = new SettingDetailAdapter(mContext, items,mContext.getString(R.string.setting_flash) );
		lvSettingDetail.setAdapter(adapter);
	}
	



}
