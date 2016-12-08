package com.humanwares.camera;

import com.bumptech.glide.load.Transformation;
import com.humanwares.camera.DB.UserData;

import android.app.Application;
import android.util.Log;

public class CameraApp extends Application {
	
	public Boolean isDebug = true;			// 디버깅 모드 
	private UserData mUserData;
	private Transformation[] filters;
	private static CameraApp app;
	
	// 구글 계정 로그인 할 e-mail과 패스워드 : 이 값이 설정되어야 마켓에 등록한 앱의 버전 정보를 가지고 올수 있다.
	public static final String email = "test@gmail.com";
	public static final String passwd = "passwd";
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		if ( isDebug )Log.e("", "CameraApp onCreate");
		super.onCreate();
		this.app = this;
	}
	
	public static CameraApp getApp() {
		return app;
	}
	
	public UserData  getUserData() {
		if ( mUserData == null ) mUserData = new UserData(this);
		return mUserData;
	}
	
	public void setFilters(Transformation...filter) {
		this.filters = filter;
	}
	
	public Transformation[] getFilters() {
		return filters;
	}
	
	public void clearFilters() {
		filters = null;
	}
}
