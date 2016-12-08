package com.humanwares.camera.DB;

import com.humanwares.camera.R;
import com.humanwares.camera.Activity.MainActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

// 사용자 디바이스 정보 저장
public class UserData {
	
	private Context mContext;
	private String DBNAME = "eqcamera";
	
	public String strFlashMode;				// 카메라 FLASH
	public String strCameraMode;			// 카메라 모드
	public String strWB;					// 화이트밸런스
	public String strSwitchCamera;			// 카메라 전후방
	public String strGyro;					// 자이로
	public String strISO;					// ISO
	public String strHDR;					// HDR
	public int nZoom;						// zoom
	public int nBrightness;					// 밝기
	public String[] arrISO;					// ISO
	public String strConfirm;				// 촬영 후 확인
	public String strSound;					// 촬영음
	public String strLocation;				// 위치
	public String strOriX;					// 오리지날 셔터위치 X
	public String strOriY;					// 오리지날 셔터위치 Y
	public String strMoveX;					// 이동한 X
	public String strMoveY;					// 이동한 Y
	public String strMetering;				// 측광 모드
	public String strSave;					// 저장위치
	
	public UserData( Context c ) {
		// TODO Auto-generated constructor stub
		mContext = c;
		strFlashMode = mContext.getString(R.string.setting_common_auto);	
		strCameraMode = mContext.getString(R.string.setting_common_auto);
		strWB = mContext.getString(R.string.setting_common_auto);
		strSwitchCamera = mContext.getString(R.string.setting_switch_r);
		strGyro = mContext.getString(R.string.setting_common_off);
		strISO = mContext.getString(R.string.setting_common_auto);
		strHDR = mContext.getString(R.string.setting_common_off);
		strConfirm = mContext.getString(R.string.setting_common_off);
		strSound  = mContext.getString(R.string.setting_common_off);
		strLocation = mContext.getString(R.string.setting_common_off);
		strMetering = getStrMetering();
		nZoom = 0;
		nBrightness = 0;
	}
	
	public String getStrSound() {
		if(TextUtils.isEmpty(strSound)) {
			strSound = mContext.getString(R.string.setting_common_on);
		}
		return strSave;
	}

	public void setStrSound(String strSound) {
		this.strSound = strSound;
		SharedPreferences pref = mContext.getSharedPreferences(DBNAME, mContext.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("sound", strSound);
		editor.commit();
	}
	
	public String getStrSave() {
		if(TextUtils.isEmpty(strSave)) {
			strSave = mContext.getString(R.string.setting_save_phone);
		}
		return strSave;
	}

	public void setStrSave(String strSave) {
		this.strSave = strSave;
		SharedPreferences pref = mContext.getSharedPreferences(DBNAME, mContext.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("save", strSave);
		editor.commit();
	}

	public String getStrMetering() {
		if(TextUtils.isEmpty(strMetering)) {
			strMetering = mContext.getString(R.string.setting_metering_c);
		}
		return strMetering;
	}

	public void setStrMetering(String strMetering) {
		this.strMetering = strMetering;
		MainActivity.getCameraView().setMeteringMode(strMetering);
		SharedPreferences pref = mContext.getSharedPreferences(DBNAME, mContext.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("metering", strMetering);
		editor.commit();
	}

	public String getStrOriX() {
		String temp;
		SharedPreferences prefs = mContext.getSharedPreferences(DBNAME, mContext.MODE_PRIVATE);
		temp = prefs.getString("orix", "");
		if ( temp.equals("") ) temp = ""; 
		return temp; 
	}

	public void setStrOriX(String strOriX) {
		SharedPreferences pref = mContext.getSharedPreferences(DBNAME, mContext.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("orix", strOriX);
		editor.commit();
	}


	public String getStrOriY() {
		String temp;
		SharedPreferences prefs = mContext.getSharedPreferences(DBNAME, mContext.MODE_PRIVATE);
		temp = prefs.getString("oriy", "");
		if ( temp.equals("") ) temp = ""; 
		return temp; 
	}


	public void setStrOriY(String strOriY) {
		SharedPreferences pref = mContext.getSharedPreferences(DBNAME, mContext.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("oriy", strOriY);
		editor.commit();
	}


	public String getStrMoveX() {
		String temp;
		SharedPreferences prefs = mContext.getSharedPreferences(DBNAME, mContext.MODE_PRIVATE);
		temp = prefs.getString("movex", "");
		if ( temp.equals("") ) temp = ""; 
		return temp; 
	}


	public void setStrMoveX(String strMoveX) {
		SharedPreferences pref = mContext.getSharedPreferences(DBNAME, mContext.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("movex", strMoveX);
		editor.commit();
	}


	public String getStrMoveY() {
		String temp;
		SharedPreferences prefs = mContext.getSharedPreferences(DBNAME, mContext.MODE_PRIVATE);
		temp = prefs.getString("movey", "");
		if ( temp.equals("") ) temp = ""; 
		return temp; 
	}


	public void setStrMoveY(String strMoveY) {
		SharedPreferences pref = mContext.getSharedPreferences(DBNAME, mContext.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("movey", strMoveY);
		editor.commit();
	}



	
	
	public  void setGridMode( String mode ) {
		SharedPreferences pref = mContext.getSharedPreferences(DBNAME, mContext.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("grid", mode);
		editor.commit();
	}
	
	public  String getGridMode() {
		String temp;
		SharedPreferences prefs = mContext.getSharedPreferences(DBNAME, mContext.MODE_PRIVATE);
		temp = prefs.getString("grid", "");
		if ( temp.equals("") ) temp = mContext.getString(R.string.setting_common_off); 
		return temp; 
	}
	
	public  void setGPSMode( String mode ) {
		SharedPreferences pref = mContext.getSharedPreferences(DBNAME, mContext.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("gps", mode);
		editor.commit();
	}
	
	public  String getGPSMode() {
		String temp;
		SharedPreferences prefs = mContext.getSharedPreferences(DBNAME, mContext.MODE_PRIVATE);
		temp = prefs.getString("gps", "");
		if ( temp.equals("") ) temp = mContext.getString(R.string.setting_common_off); 
		return temp; 
	}
	
	public  void setHDRMode( String mode ) {
		SharedPreferences pref = mContext.getSharedPreferences(DBNAME, mContext.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("hdr", mode);
		editor.commit();
	}
	
	public  String getHDRMode() {
		String temp;
		SharedPreferences prefs = mContext.getSharedPreferences(DBNAME, mContext.MODE_PRIVATE);
		temp = prefs.getString("hdr", "");
		if ( temp.equals("") ) temp =  mContext.getString(R.string.setting_common_off); 
		return temp; 
	}
	
	
}
