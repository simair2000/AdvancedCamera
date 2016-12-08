package com.humanwares.camera.Activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import jp.wasabeef.glide.transformations.ColorFilterTransformation;

import org.apache.commons.io.FileUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.humanwares.camera.CameraApp;
import com.humanwares.camera.R;
import com.humanwares.camera.View.CameraView;
import com.humanwares.camera.View.CameraView.OnCameraListener;
import com.humanwares.camera.View.FocusView;
import com.humanwares.camera.View.Setting;
import com.humanwares.camera.View.Setting.OnSettingMenu;
import com.humanwares.camera.View.ViewChanger;



public class MainActivity extends Activity implements OnClickListener {

	private static final String TAG = MainActivity.class.getSimpleName();
	private CameraView viewCamera;
	private TextView tvNum;
	private OrientationEventListener mOrientationListener;
	private CameraApp app;
	private ImageButton BtnOK, btnSwitch, btnFlash, btnMode, btnFinish, btnSetting, btnHDR; 
	private ImageView imgGallery, imgGrid;
	private SensorManager smManager;
	private Sensor sAccelerometer;
	private Sensor sGyro;
	private Sensor sMagnetic;
	private FocusView viewFocus;
	private Handler hd;
	private Setting rlSetting;
	private ImageView imgGyro, imgGyroBG;
	private SoundPool sound_pool;
	private int sound_beep;
	private RelativeLayout rlZoom;
	private ProgressBar pbZoom;
	private RelativeLayout rlBrightness, rlShutter;
	private ProgressBar pbBrightness;
	private Button btnShutterOK, btnShutterNO;
	private ImageView imgFilter;
	private View layoutMovie;
	private View layoutCamera;
	private ViewChanger viewChanger;
	private TextView textTime;
	private View btnRecordHD;
	public boolean isHDRecord;
	private View btnPauseResume;
	public static MainActivity instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e(TAG, "onCreate");
		instance = this;
		setContentView(R.layout.activity_main);
		initUI();
	}

	public static CameraView getCameraView() {
		if(instance != null) {
			return instance.viewCamera;
		}
		return null;
	}

	// UI 초기화
	private void initUI() {
		app = (CameraApp) getApplication();
		initSensor();
		initFocus();
		// 사운드
		sound_pool = new SoundPool( 5, AudioManager.STREAM_MUSIC, 0 );
		sound_beep = sound_pool.load(this, R.raw.beef, 1 );

		layoutMovie = findViewById(R.id.layoutMovie);
		layoutCamera = findViewById(R.id.layoutCamera);
		
		viewChanger = new ViewChanger(layoutCamera, layoutMovie);
		viewChanger.setCurrentView(layoutCamera);

		viewCamera = (CameraView) findViewById(R.id.pre);
		viewCamera.setCameraListener(cameraListener);
		BtnOK = (ImageButton) findViewById(R.id.btn_camera_capture);
		btnSwitch = (ImageButton) findViewById(R.id.btn_camera_change);
		btnFlash = (ImageButton) findViewById(R.id.btn_camera_flash);
		btnMode = (ImageButton) findViewById(R.id.btn_camera_mode);
		btnSetting = (ImageButton) findViewById(R.id.btn_camera_setting);
		btnFinish = (ImageButton) findViewById(R.id.btn_finish);
		btnHDR = (ImageButton) findViewById(R.id.btn_camera_hdr);
		imgGrid = (ImageView) findViewById(R.id.img_grid);
		imgGyro = (ImageView) findViewById(R.id.img_gyro);
		imgGyroBG = (ImageView) findViewById(R.id.img_gyro_bg);
		btnHDR.setOnClickListener(buttonListener);
		BtnOK.setOnClickListener(buttonListener);
		BtnOK.setOnLongClickListener(buttonLongListener);
		btnSwitch.setOnClickListener(buttonListener);
		btnFlash.setOnClickListener(buttonListener);
		btnMode.setOnClickListener(buttonListener);
		btnSetting.setOnClickListener(buttonListener);
		btnFinish.setOnClickListener(buttonListener);
		imgGallery = (ImageView) findViewById(R.id.iv_gallery);
		imgFilter = (ImageView)findViewById(R.id.imgFilter);
		btnRecordHD = findViewById(R.id.btn_record_hd);
		btnPauseResume = findViewById(R.id.btn_pause_resume);
		//viewFocus.setOnTouchListener(moveListener);
		
		imgGallery.setOnClickListener(buttonListener);
		
		findViewById(R.id.btn_camera_movie).setOnClickListener(this);
		findViewById(R.id.btn_camera_photo).setOnClickListener(this);
		
		findViewById(R.id.btn_record).setOnClickListener(this);
		findViewById(R.id.btn_camera_change1).setOnClickListener(this);
		findViewById(R.id.btn_finish1).setOnClickListener(this);
		
		findViewById(R.id.btn_record_hd).setOnClickListener(this);
		findViewById(R.id.btn_pause_resume).setOnClickListener(this);
		
		isHDRecord = true;
		
		textTime = (TextView)findViewById(R.id.textTime);


		initZoom();
//		getLastfile();
		initGrid();
		initBrightness();
		initShutter();
	}

	private void initShutter() {
		if ( app.getUserData().getStrOriX().equals("") ) {
			app.getUserData().setStrOriX("" + BtnOK.getX());
			app.getUserData().setStrOriY("" + BtnOK.getY());
		}
		Handler hd = new Handler();
		hd.postDelayed(new Runnable() {

			@Override
			public void run() {
				if ( !app.getUserData().getStrMoveX().equals("") ) {
				
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT); //WRAP_CONTENT param can be FILL_PARENT
					params.leftMargin = (int) Float.parseFloat(app.getUserData().getStrMoveX());
					params.topMargin = (int) Float.parseFloat(app.getUserData().getStrMoveY());
					BtnOK.setLayoutParams(params);
					
				}  
			}
		}, 500);		


		viewCamera.isMove = false;
		rlShutter = (RelativeLayout) findViewById(R.id.rl_shutter);
		rlShutter.setVisibility(View.GONE);
		btnShutterOK = (Button) findViewById(R.id.ok);
		btnShutterNO = (Button) findViewById(R.id.no);
		btnShutterNO.setVisibility(View.GONE);
		btnShutterOK.setOnClickListener(buttonListener);
		btnShutterNO.setOnClickListener(buttonListener);
	}

	private void initBrightness() {
		rlBrightness = (RelativeLayout) findViewById(R.id.rl_brightness);
		pbBrightness = (ProgressBar) findViewById(R.id.pb_brightness);
		pbBrightness.setMax(5);
		pbBrightness.setProgress(3);
		rlBrightness.setVisibility(View.GONE);
	}

	private void initZoom() {
		rlZoom = (RelativeLayout) findViewById(R.id.rl_zoom);
		pbZoom = (ProgressBar) findViewById(R.id.pb_zoom);
		pbZoom.setMax(10);
		pbZoom.setProgress(0);
		rlZoom.setVisibility(View.GONE);
	}


	// 화이트 밸런스 설정
	private void initWB() {
		if ( app.getUserData().strWB.equals(getString(R.string.setting_common_auto)) ) {
			Parameters p = viewCamera.mCamera.getParameters();
			p.setWhiteBalance(Parameters.WHITE_BALANCE_AUTO);
			viewCamera.mCamera.setParameters(p);
		} else if (app.getUserData().strWB.equals(getString(R.string.setting_wb_daylight))) {
			Parameters p = viewCamera.mCamera.getParameters();
			p.setWhiteBalance(Parameters.WHITE_BALANCE_DAYLIGHT);
			viewCamera.mCamera.setParameters(p);
		} else if (app.getUserData().strWB.equals(getString(R.string.setting_wb_cloudydaylight))) {
			Parameters p = viewCamera.mCamera.getParameters();
			p.setWhiteBalance(Parameters.WHITE_BALANCE_CLOUDY_DAYLIGHT);
			viewCamera.mCamera.setParameters(p);
		} else if (app.getUserData().strWB.equals(getString(R.string.setting_wb_fluorescent))) {
			Parameters p = viewCamera.mCamera.getParameters();
			p.setWhiteBalance(Parameters.WHITE_BALANCE_FLUORESCENT);
			viewCamera.mCamera.setParameters(p);
		} else if (app.getUserData().strWB.equals(getString(R.string.setting_wb_incandescent))) {
			Parameters p = viewCamera.mCamera.getParameters();
			p.setWhiteBalance(Parameters.WHITE_BALANCE_INCANDESCENT);
			viewCamera.mCamera.setParameters(p);
		}
	}

	// 그리드 설정
	private void initGrid() {
		if ( app.getUserData().getGridMode().equals(getString(R.string.setting_common_off)) ) {
			imgGrid.setVisibility(View.GONE);
		} else {
			imgGrid.setVisibility(View.VISIBLE);
		}
	}

	// 카메라 모드 설정
	private void initCameaMode() {
		Log.e("", "app.getUserData().strCameraMode =" + app.getUserData().strCameraMode);
		Parameters p = viewCamera.mCamera.getParameters();
		app.clearFilters();
		if ( app.getUserData().strCameraMode.equals(getString(R.string.setting_common_auto)) ) {
//			Parameters p = viewCamera.mCamera.getParameters();
//			p.setSceneMode(Parameters.SCENE_MODE_AUTO);
//			viewCamera.mCamera.setParameters(p);
//			viewCamera.mCamera.startPreview();
			imgFilter.setImageDrawable(null);
			btnMode.setBackgroundResource(R.drawable.icon_lm05_01);
		} else if ( app.getUserData().strCameraMode.equals(getString(R.string.setting_mode_landscape)) ) {
//			Parameters p = viewCamera.mCamera.getParameters();
//			p.setSceneMode(Parameters.SCENE_MODE_LANDSCAPE);
//			viewCamera.mCamera.setParameters(p);
//			viewCamera.mCamera.startPreview();
			imgFilter.setImageDrawable(null);
			btnMode.setBackgroundResource(R.drawable.icon_lm05_02);
		} else if ( app.getUserData().strCameraMode.equals(getString(R.string.setting_mode_portrait)) ) {
//			Parameters p = viewCamera.mCamera.getParameters();
//			p.setSceneMode(Parameters.SCENE_MODE_PORTRAIT);
//			viewCamera.mCamera.setParameters(p);
//			viewCamera.mCamera.startPreview();
			imgFilter.setImageDrawable(null);
			btnMode.setBackgroundResource(R.drawable.icon_lm05_03);
		} else if ( app.getUserData().strCameraMode.equals(getString(R.string.setting_mode_sports)) ) {
//			Parameters p = viewCamera.mCamera.getParameters();
//			p.setSceneMode(Parameters.SCENE_MODE_SPORTS);
//			viewCamera.mCamera.setParameters(p);
//			viewCamera.mCamera.startPreview();
			imgFilter.setImageDrawable(null);
			btnMode.setBackgroundResource(R.drawable.icon_lm05_04);
		} else if ( app.getUserData().strCameraMode.equals(getString(R.string.setting_mode_night)) ) {
//			Parameters p = viewCamera.mCamera.getParameters();
//			p.setSceneMode(Parameters.SCENE_MODE_NIGHT);
//			viewCamera.mCamera.setParameters(p);
//			viewCamera.mCamera.startPreview();
			imgFilter.setImageDrawable(null);
			btnMode.setBackgroundResource(R.drawable.icon_lm05_05);
		} else if ( app.getUserData().strCameraMode.equals(getString(R.string.setting_mode_beauty)) ) {
//			Parameters p = viewCamera.mCamera.getParameters();
//			p.setSceneMode(Parameters.SCENE_MODE_NIGHT);
//			viewCamera.mCamera.setParameters(p);
//			viewCamera.mCamera.startPreview();
//			ToonFilterTransformation toon = new ToonFilterTransformation(this);
			app.setFilters(new ColorFilterTransformation(this, Color.argb(50, 0, 0, 255)), new ColorFilterTransformation(this, Color.alpha(50)));
//			app.setFilters(toon);
			Glide.with(this).load(R.drawable.img_filter).asBitmap().transform(app.getFilters()).into(imgFilter);
			btnMode.setBackgroundResource(R.drawable.icon_lm05_06);
		} else if ( app.getUserData().strCameraMode.equals(getString(R.string.setting_mode_food)) ) {
//			Parameters p = viewCamera.mCamera.getParameters();
//			p.setSceneMode(Parameters.SCENE_MODE_NIGHT);
//			viewCamera.mCamera.setParameters(p);
//			viewCamera.mCamera.startPreview();
			app.setFilters(new ColorFilterTransformation(this, Color.argb(50, 255, 0, 0)), new ColorFilterTransformation(this, Color.alpha(50)));
			Glide.with(this).load(R.drawable.img_filter).asBitmap().transform(app.getFilters()).into(imgFilter);
			btnMode.setBackgroundResource(R.drawable.icon_lm05_07);
		}
	}


	private void initFocus() {
		viewFocus = new FocusView(this);
		LayoutParams layoutParamsDrawing = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		this.addContentView(viewFocus, layoutParamsDrawing);
	}

	//  마지막에 저장된 사진 가져옴
	private void getLastfile () {
		File pathToFiles = null;
		if(app.getUserData().getStrSave().equals(getString(R.string.setting_save_external))) {
			pathToFiles = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "//EQCamera");
		} else {
			pathToFiles = getFilesDir();
		}
		if (pathToFiles.exists()) {

			try {
				String[] videoFileList = pathToFiles.list();
				if(videoFileList != null && videoFileList.length > 0) {
					String lastFile = pathToFiles.getPath() +"/"+ videoFileList[videoFileList.length -1];
					Glide.with(this).load(new File(lastFile)).asBitmap().thumbnail(0.5f).into(imgGallery);
				} else {
					imgGallery.setImageDrawable(null);
				}

//				BitmapFactory.Options options = new BitmapFactory.Options();
//				options.inSampleSize = 4;
//				Bitmap src = BitmapFactory.decodeFile( lastFile, options );
//				Bitmap resized = Bitmap.createScaledBitmap( src, 100, 100, true );
//				imgGallery.setImageBitmap(resized);
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

	}


	// 초기 설정 가져오기
	//	- FLASH_MODE_AUTO : Flash will be fired automaticall when required.
	//	- FLASH_MODE_OFF   : Flash will not be fired.
	//	- FLASH_MODE_ON     : Flash will always be fired during snapshot.
	//	- FLASH_MODE_RED_EYE : Flash will be fired in red-eye reduction mode.
	//	- FLASH_MODE_TORCH : Constant emission of light during preview, auto-focus and snapshot.
	private void initFlashSetting() {

		if ( app.getUserData().strFlashMode.equals(getString(R.string.setting_common_auto)) ) {
			viewCamera.flashAuto();
			btnFlash.setBackgroundResource(R.drawable.icon_lm02_03);
		} else if ( app.getUserData().strFlashMode.equals(getString(R.string.setting_common_on)) ) {
			viewCamera.flashOn();
			btnFlash.setBackgroundResource(R.drawable.icon_lm02_01);
		} else {
			viewCamera.flashOff();
			btnFlash.setBackgroundResource(R.drawable.icon_lm02_02);
		}
	}

	// iso 설정
	private void initISO() {
		Parameters p = viewCamera.mCamera.getParameters();
		String supportedValues = p.get("iso-values");
		Log.e("", "newVAlue + "  + supportedValues);
		if (supportedValues == null) {
			app.getUserData().strISO = getString(R.string.no_hdr);
		} else {
			app.getUserData().strISO = getString(R.string.setting_common_auto); 
			app.getUserData().arrISO = supportedValues.split(",");  
			Parameters params = viewCamera.mCamera.getParameters();
			// "choice" reflects the user's choice (ie. menu selection)
			for ( int i = 0; i < app.getUserData().arrISO.length; i++ ) {
				if ( app.getUserData().strISO.equals(app.getUserData().arrISO[i]) ) {
					params.set("iso", app.getUserData().arrISO[i]);  
					app.getUserData().strISO = app.getUserData().arrISO[i];
				} else if ( (app.getUserData().arrISO[i]).equals("auto") ) {
					app.getUserData().arrISO[i] = getString(R.string.setting_common_auto);
				}
			}
			if ( app.getUserData().strISO.equals(getString(R.string.setting_common_auto)) ) {
				params.set("iso", "auto");  
				app.getUserData().strISO = app.getUserData().arrISO[0];
			}


			viewCamera.mCamera.setParameters(params);
		}
	}

	// HDR 
	private void initHDRSetting() {

		boolean isHDR = false;
		Parameters p = viewCamera.mCamera.getParameters();
		List<String>temp =  p.getSupportedSceneModes();
		for ( int i =0; i < temp.size(); i++ ) {
			Log.e("", "temp.get(i) = " + temp.get(i));
			if (temp.get(i).equals(Parameters.SCENE_MODE_HDR)) {
				isHDR = true;
			}else {
				isHDR = false;
			} 
		} 
		if ( isHDR == true ) {
			if ( app.getUserData().strHDR.equals(getString(R.string.setting_common_off)) ) {
				btnHDR.setBackgroundResource(R.drawable.icon_lm04_02);
				p.setSceneMode(Parameters.SCENE_MODE_AUTO);
				viewCamera.mCamera.setParameters(p);
				viewCamera.isHDR = false;

			} else {
				btnHDR.setBackgroundResource(R.drawable.icon_lm04_01);

				p.setSceneMode(Parameters.SCENE_MODE_AUTO);
				viewCamera.mCamera.setParameters(p);
				viewCamera.isHDR = true;
			}


		} else {
			btnHDR.setBackgroundResource(R.drawable.icon_lm04_02);
			app.getUserData().strHDR = getString(R.string.no_hdr);
		}

	}



	// 센서  초기화
	private void initSensor() {
		smManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);



		if( smManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null ) {
			sAccelerometer = smManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			smManager.registerListener(accelerometerListener, sAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		} else {
			Log.e("", "Error Accelerometer");
		}
		if( smManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null ) {

			sMagnetic = smManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		}else {
			Log.e("", "Error Magnetic");
		}
		if( smManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null ) {
			sGyro = smManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
			smManager.registerListener(accelerometerListener, sGyro, SensorManager.SENSOR_DELAY_NORMAL);
		} else {
			Log.e("", "Error TYPE_GYROSCOPE");
		}
		mOrientationListener = new OrientationEventListener(this,SensorManager.SENSOR_DELAY_NORMAL) {
			@Override
			public void onOrientationChanged(int orientation) {

				imgGyro.setRotation(orientation);
				imgGyro.setScaleType(ScaleType.FIT_XY);
				//Log.e("", "onOrientationChanged = " + orientation);
				int diff = Math.abs(orientation -viewCamera.current_orientation);
				if( diff > 180 )
					diff = 360 - diff;
				// only change orientation when sufficiently changed
				if( diff > 60 ) {
					orientation = (orientation + 45) / 90 * 90;
					orientation = orientation % 360;

					viewCamera.current_orientation = orientation;
					//Log.e("", "viewCamera.current_orientation = " + viewCamera.current_orientation);
				}
			}
		};
		mOrientationListener.enable();;
	}

	int wb = 0;
	boolean isPlus = false;


	private OnLongClickListener buttonLongListener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			viewCamera.isStillMode = true;

			BtnOK.setOnTouchListener(moveListener);
			BtnOK.setOnClickListener(null);
			return true;
		}
	};


	// 포커스 가운데로 초기화 
	private void resetFocus() {
		if(viewChanger.getCurrentView().equals(layoutMovie)) {
			return;
		}
		if ( hd == null ) {
			hd = new Handler();
		}
		hd.postDelayed(new Runnable() {

			@Override
			public void run() {


				//				Rect rect = new Rect(100, 100, 100, 100);
				//				Rect targetFocusRect = new Rect(rect.left * 2000/viewFocus.getWidth() - 1000, rect.top * 2000/viewFocus.getHeight() - 1000, rect.right * 2000/viewFocus.getWidth() - 1000,rect.bottom * 2000/viewFocus.getHeight() - 1000);
				//				List<Camera.Area> focusList = new ArrayList<Camera.Area>();
				//				Camera.Area focusArea = new Camera.Area(rect, 1000);
				//				focusList.add(focusArea);
				//				Parameters para = viewCamera.mCamera.getParameters();
				//				para.setFocusAreas(focusList);
				//				//para.setMeteringAreas(focusList);
				//				viewCamera.mCamera.setParameters(para);
				//				viewCamera.mCamera.autoFocus(myAutoFocusCallback);
				//				viewFocus.setHaveTouch(true, rect);
				//				viewFocus.invalidate();

				//Rect newRect = new Rect(100,100,100, 100);
				//Rect newRect = new Rect(screenWidth, screenHeight, 0, 0);

				viewFocus.nSuccess = -1;
				Display display = getWindowManager().getDefaultDisplay(); 
				int screenWidth = display.getWidth() /2;
				int screenHeight = display.getHeight() /2;
				Rect newRect = new Rect(screenWidth - 45, screenHeight - 45, screenWidth - 45, screenHeight - 45);
				Camera.Parameters params = viewCamera.mCamera.getParameters();
				Camera.Area focusArea = new Camera.Area(newRect, 1000);
				List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
				focusAreas.add(focusArea);
				params.setFocusAreas(focusAreas);
				try {
					viewCamera.mCamera.autoFocus(myAutoFocusCallback);
				} catch (Exception e) {
					e.printStackTrace();
				}
				viewFocus.setHaveTouch(true, newRect);
				viewFocus.invalidate();



			}
		}, 500);		


	}

	private OnClickListener buttonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.btn_camera_capture) {

				viewCamera.capture(false);
				resetFocus();
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						getLastfile();
					}
				}, 1000);
				
			} else if ( v.getId() == R.id.ok ) {
				app.getUserData().setStrMoveX("" + BtnOK.getX());
				app.getUserData().setStrMoveY("" + BtnOK.getY());
				viewCamera.isMove = false;
				BtnOK.setOnTouchListener(null);
				BtnOK.setOnClickListener(buttonListener);
				rlShutter.setVisibility(View.GONE);
			} else if (v.getId() == R.id.no ) {
				initShutter();
				rlShutter.setVisibility(View.GONE);
				viewCamera.isMove = false;
				BtnOK.setOnTouchListener(null);
				BtnOK.setOnClickListener(buttonListener);
			} else if ( v.getId() == R.id.btn_camera_change ) {
				if ( app.getUserData().strSwitchCamera.equals(getString(R.string.setting_switch_f)) ) {
					app.getUserData().strSwitchCamera = getString(R.string.setting_switch_r);
					viewCamera.switchCamera(app.getUserData().strSwitchCamera);
				} else {
					app.getUserData().strSwitchCamera = getString(R.string.setting_switch_f);
					viewCamera.switchCamera(app.getUserData().strSwitchCamera);
				}

			} else if ( v.getId() == R.id.btn_camera_flash ) {

				if ( app.getUserData().strFlashMode.equals(getString(R.string.setting_common_auto)) ) {
					app.getUserData().strFlashMode = getString(R.string.setting_common_off);

				} else if ( app.getUserData().strFlashMode.equals(getString(R.string.setting_common_off)) ) {
					app.getUserData().strFlashMode = getString(R.string.setting_common_on);

				} else {
					app.getUserData().strFlashMode = getString(R.string.setting_common_auto);

				}
				Log.e("", "app.getUserData().strFlashMode = " + app.getUserData().strFlashMode);
				initFlashSetting();


			} else if ( v.getId() == R.id.iv_gallery ) {
				Intent i = new Intent(MainActivity.this, PreViewActivity.class);
				startActivity(i);
			} else if ( v.getId() == R.id.btn_finish ) {
				pressFinish();
			} else if ( v.getId() == R.id.btn_camera_setting ) {
				//				if ( viewCamera.isMove == false ) {
				//					viewCamera.isMove = true;
				//					Toast toast = Toast.makeText(MainActivity.this, "셔터위치를 변경하세요.", Toast.LENGTH_LONG);
				//					toast.setGravity(Gravity.CENTER, 0, 0);
				//					toast.show();
				//					BtnOK.setClickable(true);
				//					BtnOK.setOnTouchListener(moveListener);
				//					BtnOK.setOnClickListener(null);
				//				} else {
				//					Toast toast = Toast.makeText(MainActivity.this, "셔터위치 변경이 완료되었습니다.", Toast.LENGTH_LONG);
				//					toast.setGravity(Gravity.CENTER, 0, 0);
				//					toast.show();
				//					viewCamera.isMove = false;
				//					BtnOK.setOnTouchListener(null);
				//					BtnOK.setOnClickListener(buttonListener);
				//
				//				}

				if ( rlSetting == null ) {
					rlSetting = new Setting(MainActivity.this, settingListener);	
					LayoutParams layoutParamsDrawing = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
					addContentView(rlSetting, layoutParamsDrawing);
					//					
				}
				rlSetting.setVisibility(View.VISIBLE);
				rlSetting.initSetting();
			} else if ( v.getId() == R.id.btn_camera_hdr  ) {
				boolean useHDR = false;
				Parameters p = viewCamera.mCamera.getParameters();
				List<String>temp =  p.getSupportedSceneModes();
				for ( int i =0; i < temp.size(); i++ ) {
					Log.e("", "temp.get(i) = " + temp.get(i));
					if (temp.get(i).equals(Parameters.SCENE_MODE_HDR)) {
						useHDR = true;
					}else {
						useHDR = false;
					} 
				} 
				if ( useHDR == true ) {
					if ( viewCamera.isHDR == false ) {
						p.setSceneMode(Parameters.SCENE_MODE_HDR);
						viewCamera.mCamera.setParameters(p);
						viewCamera.isHDR = true;
						btnHDR.setBackgroundResource(R.drawable.icon_lm04_01);
					} else {
						p.setSceneMode(Parameters.SCENE_MODE_AUTO);
						viewCamera.mCamera.setParameters(p);
						viewCamera.isHDR = false;
						btnHDR.setBackgroundResource(R.drawable.icon_lm04_02);
					}

				} else {
					Toast toast = Toast.makeText(MainActivity.this, getString(R.string.no_hdr), Toast.LENGTH_LONG);
					toast.show();
				}	
			}



			//			else if ( v.getId() == R.id.btn_camera_grid ) {
			//
			//				if ( viewCamera.isGrid == false ) {
			//					viewCamera.isGrid = true;
			//					viewCamera.invalidate();
			//				} else {
			//					viewCamera.isGrid = false;
			//					viewCamera.invalidate();
			//				}
			//
			//			} else if ( v.getId() == R.id.btn_camera_wb ) {
			//				Parameters p = viewCamera.mCamera.getParameters();
			//				List< String > whitelist = p.getSupportedWhiteBalance ();
			//				Toast toast = Toast.makeText(MainActivity.this, whitelist.get(wb), Toast.LENGTH_LONG);
			//				toast.setGravity(Gravity.CENTER, 0, 0);
			//				toast.show();
			//				p.setWhiteBalance(whitelist.get(wb));
			//				viewCamera.mCamera.setParameters(p);
			//				if (wb < (whitelist.size() -1) ) {
			//					wb++;
			//				} else {
			//					wb = 0;
			//				}
			//
			//			} else if ( v.getId() == R.id.btn_camera_change ) {
			//				if ( viewCamera.isMove == false ) {
			//					viewCamera.isMove = true;
			//					btnMove.setText("변경완료");
			//					BtnOK.setClickable(true);
			//					BtnOK.setOnTouchListener(moveListener);
			//					BtnOK.setOnClickListener(null);
			//				} else {
			//					viewCamera.isMove = false;
			//					btnMove.setText("셔터위치 변경");
			//					BtnOK.setOnTouchListener(null);
			//					BtnOK.setOnClickListener(buttonListener);
			//
			//				}
			//			} 
			else if ( v.getId() == R.id.btn_camera_mode ) {
				if ( app.getUserData().strCameraMode.equals(getString(R.string.setting_common_auto)) ) {
					app.getUserData().strCameraMode = getString(R.string.setting_mode_landscape);
				} else if ( app.getUserData().strCameraMode.equals(getString(R.string.setting_mode_landscape)) ) {
					app.getUserData().strCameraMode = getString(R.string.setting_mode_portrait);
				} else if ( app.getUserData().strCameraMode.equals(getString(R.string.setting_mode_portrait)) ) {
					app.getUserData().strCameraMode = getString(R.string.setting_mode_sports);
				} else if ( app.getUserData().strCameraMode.equals(getString(R.string.setting_mode_sports)) ) {
					app.getUserData().strCameraMode = getString(R.string.setting_mode_night);
				} else if ( app.getUserData().strCameraMode.equals(getString(R.string.setting_mode_night)) ) {
					app.getUserData().strCameraMode = getString(R.string.setting_mode_beauty);
				} else if ( app.getUserData().strCameraMode.equals(getString(R.string.setting_mode_beauty)) ) {
					app.getUserData().strCameraMode = getString(R.string.setting_mode_food);
				} else {
					app.getUserData().strCameraMode = getString(R.string.setting_common_auto);
				}
				initCameaMode();

				//				Parameters p = viewCamera.mCamera.getParameters();
				//				List<String>temp =  p.getSupportedColorEffects();
				//				p.setColorEffect(temp.get(viewCamera.nColorFilter));
				//				viewCamera.mCamera.setParameters(p);
				//				Toast toast = Toast.makeText(MainActivity.this, "" + temp.get(viewCamera.nColorFilter), Toast.LENGTH_LONG);
				//				toast.setGravity(Gravity.CENTER, 0, 0);
				//				toast.show();
				//				if ( temp != null && temp.size() > 0 ) {
				//					if ( temp.size() -1 > viewCamera.nColorFilter ) {
				//						viewCamera.nColorFilter++;
				//					} else {
				//						viewCamera.nColorFilter = 0;
				//					}
				//					Log.e("", "viewCamera.nColorFilter  = " + viewCamera.nColorFilter + " // " + temp.size());
				//
				//				}

			}
			//			else if ( v.getId() == R.id.btn_camera_brightness ) {
			//				Parameters p = viewCamera.mCamera.getParameters();
			//				int minBrightness =  viewCamera.mCamera.getParameters().getMinExposureCompensation();
			//				int maxBrightness =  viewCamera.mCamera.getParameters().getMaxExposureCompensation();
			//				int nowBrightness = viewCamera.mCamera.getParameters().getExposureCompensation();
			//				Log.e("", "minBrightness = " + minBrightness + "maxBrightness =" + maxBrightness + "nowBrightness =" +   nowBrightness);
			//				if ( nowBrightness < maxBrightness && isPlus == false ) {
			//					nowBrightness = nowBrightness +1;
			//					Log.e("", "nowBrightness = "+ nowBrightness);
			//				} else if ( nowBrightness >  minBrightness) {
			//					nowBrightness = nowBrightness -1;
			//					isPlus = true;
			//				} else {
			//					isPlus = false;
			//					nowBrightness = nowBrightness +1;
			//				}
			//				p.setExposureCompensation(nowBrightness);
			//				viewCamera.mCamera.setParameters(p);
			//				Toast toast = Toast.makeText(MainActivity.this, ""+nowBrightness, Toast.LENGTH_LONG);
			//				toast.setGravity(Gravity.CENTER, 0, 0);
			//				toast.show();
			//			} else if ( v.getId() == R.id.btn_camera_metering  || v.getId() == R.id.btn_setting) {
			//				Toast toast = Toast.makeText(MainActivity.this, "준비중...", Toast.LENGTH_LONG);
			//				toast.setGravity(Gravity.CENTER, 0, 0);
			//				toast.show();
			//			} else if ( v.getId() == R.id.btn_camera_resolution ) {
			//				Parameters p = viewCamera.mCamera.getParameters();
			//				List<Size> sizes = p.getSupportedPictureSizes();
			//
			//				Size mSize;
			//				for (Size size : sizes) {
			//					Log.i("", "Available resolution: "+size.width+" "+size.height + "//");
			//					mSize = size;
			//				}
			//
			//				p.setPictureSize(sizes.get(0).width, sizes.get(0).height);
			//				viewCamera.mCamera.setParameters( p);
			//			} else if ( v.getId() == R.id.btn_camera_hdr  ) {
			//				boolean isHDR = false;
			//				Parameters p = viewCamera.mCamera.getParameters();
			//				List<String>temp =  p.getSupportedSceneModes();
			//				for ( int i =0; i < temp.size(); i++ ) {
			//					Log.e("", "temp.get(i) = " + temp.get(i));
			//					if (temp.get(i).equals(Parameters.SCENE_MODE_HDR)) {
			//						isHDR = true;
			//					}else {
			//						isHDR = false;
			//					} 
			//				} 
			//				if ( isHDR == true ) {
			//					p.setSceneMode(Parameters.SCENE_MODE_HDR);
			//					viewCamera.mCamera.setParameters(p);
			//					Toast toast = Toast.makeText(MainActivity.this, "SCENE_MODE_HDR", Toast.LENGTH_LONG);
			//					toast.setGravity(Gravity.CENTER, 0, 0);
			//					toast.show();
			//				} else {
			//					Toast toast = Toast.makeText(MainActivity.this, getString(R.string.no_hdr), Toast.LENGTH_LONG);
			//					toast.setGravity(Gravity.CENTER, 0, 0);
			//					toast.show();
			//				}	
			//			} else if ( v.getId()  == R.id.btn_camera_gps ) {
			//
			//			} else if ( v.getId() == R.id.btn_camera_filter ) {
			//				Parameters p = viewCamera.mCamera.getParameters();
			//				List<String>temp =  p.getSupportedColorEffects();
			//				p.setColorEffect(temp.get(viewCamera.nColorFilter));
			//				viewCamera.mCamera.setParameters(p);
			//				Toast toast = Toast.makeText(MainActivity.this, "" + temp.get(viewCamera.nColorFilter), Toast.LENGTH_LONG);
			//				toast.setGravity(Gravity.CENTER, 0, 0);
			//				toast.show();
			//
			//				if ( temp.size() -1 > viewCamera.nColorFilter ) {
			//					viewCamera.nColorFilter++;
			//				} else {
			//					viewCamera.nColorFilter = 0;
			//				}
			//				Log.e("", "viewCamera.nColorFilter  = " + viewCamera.nColorFilter + " // " + temp.size());
			//
			//			}
		}
	}; 

	int a = 0;
	private OnTouchListener moveListener = new OnTouchListener() {

		float offset_x, offset_y;
		@Override
		public boolean onTouch( View v,  MotionEvent event) {

			// TODO Auto-generated method stub
			final RelativeLayout.LayoutParams par=(RelativeLayout.LayoutParams)v.getLayoutParams();
			Log.e("", "" + event.getAction());
			if ( event.getAction() ==  MotionEvent.ACTION_MOVE) {
				if ( viewCamera.isMove == true ) {
					v.setX(event.getRawX() - offset_x);
					v.setY(event.getRawY() - offset_y);
				}
				if ( viewCamera.isStillMode == true ) {
					if ( a == 0 ) {
						viewCamera.capture(false);
						a =1;
					}

					//					tvNum.setText(viewCamera.nStillCount + "/" + 20);

				}

			} else if (event.getAction() ==  MotionEvent.ACTION_UP) {
				if ( viewCamera.isMove == true ) {
					v.setLayoutParams(par);
					return true;
				}

				viewCamera.isStillMode = false;
				BtnOK.setOnClickListener(buttonListener);
				BtnOK.setOnTouchListener(null);
				Log.e("", "MotionEvent.ACTION_UP viewCamera.isStillMode = " + viewCamera.isStillMode);
				a = 0;
				return false;


			} else if ( event.getAction() ==  MotionEvent.ACTION_DOWN ) {
				if ( viewCamera.isMove == true ) {
					offset_x = event.getX(); 
					offset_y = event.getY();
				}
				return true;
			} 
			return false;

		}
	};

	private void pressFinish() {
		Intent i = new Intent(MainActivity.this, ADActivity.class);
		startActivity(i);
		finish();
	}



	// 카메라 리스너
	private OnCameraListener cameraListener = new OnCameraListener() {


		private DecimalFormat decimalFormat = new DecimalFormat("#0.0");
		@Override
		public void onCapture() {
			// TODO Auto-generated method stub
			getLastfile();
			if ( app.getUserData().strConfirm.equals(getString(R.string.setting_common_on)) ) {
				Intent i = new Intent(MainActivity.this, PreViewActivity.class);
				startActivity(i);
			}
		}

		@Override
		public void onTouchFocus(Rect rect) {
			viewFocus.nSuccess = -1;
			Rect targetFocusRect = new Rect(rect.left * 2000/viewFocus.getWidth() - 1000, rect.top * 2000/viewFocus.getHeight() - 1000, rect.right * 2000/viewFocus.getWidth() - 1000,rect.bottom * 2000/viewFocus.getHeight() - 1000);

			List<Camera.Area> focusList = new ArrayList<Camera.Area>();
			Camera.Area focusArea = new Camera.Area(targetFocusRect, 1000);
			focusList.add(focusArea);
			Parameters para = viewCamera.mCamera.getParameters();
			para.setFocusAreas(focusList);
			para.setMeteringAreas(focusList);
			viewCamera.mCamera.setParameters(para);
			viewCamera.mCamera.autoFocus(myAutoFocusCallback);
			viewFocus.setHaveTouch(true, rect);
			viewFocus.invalidate();
		}

		@Override
		public void onCameraStart() {
			initFlashSetting();
			initHDRSetting();
			resetFocus();
			initISO();
		}

		@Override
		public void onAccelerometer(double lv, double x, double y) {
			// TODO Auto-generated method stub
			String number_string = decimalFormat.format(lv);
			number_string = number_string.replaceAll( "^-(?=0(.0*)?$)", "");
			double test = Double.valueOf(number_string);
			String string =  " 각도 : " + number_string + (char)0x00B0;
			if ( Math.abs(lv) <= 1.0f ) {
				imgGyro.setImageResource(R.drawable.test3);
			} else {
				imgGyro.setImageResource(R.drawable.test2);
			}
			//Log.e("", "string = " + string);
			//			TextView a = (TextView) findViewById(R.id.test1);
			//			a.setText(string);
			//			if ( Math.abs(lv) <= 1.0f ) {
			//				a.setTextColor(Color.GREEN);
			//			} else {
			//				a.setTextColor(Color.WHITE);
			//			}

			//			if ( preX != -20 && preY != -20    ) {
			//				double fixX = ((preX * -1) + 1) - (x * -1);
			//				//Log.e("", "x = " +y);
			//				if ( fixX > 1.5 ) {
			//					resetFocus();
			//				}
			//			}


		}

		@Override
		public void onZoom(int v) {
			rlBrightness.setVisibility(View.GONE);
			rlZoom.setVisibility(View.VISIBLE);
			pbZoom.setProgress(v);

		}

		@Override
		public void onBrightness(int v) {
			// TODO Auto-generated method stub
			rlZoom.setVisibility(View.GONE);
			rlBrightness.setVisibility(View.VISIBLE);
			if (v < 0 ) {
				pbBrightness.setProgress(v +2);
			} else {
				pbBrightness.setProgress(v +3);
			}

		}

		@Override
		public void onTouchUp() {
			Log.e("", "TouchUp");
			removeZoomrightness();

		}
	};

	private void removeZoomrightness() {
		if ( rlZoom.getVisibility() == View.VISIBLE ) {
			Handler hd = new Handler();
			hd.postDelayed(new Runnable() {

				@Override
				public void run() {
					rlZoom.setVisibility(View.GONE);  
				}
			}, 1000);	
		}
		if ( rlBrightness.getVisibility() == View.VISIBLE ) {
			Handler hd = new Handler();
			hd.postDelayed(new Runnable() {

				@Override
				public void run() {
					rlBrightness.setVisibility(View.GONE);  
				}
			}, 1000);	
		}
	}


	// 포커스 리스너
	AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback(){

		@Override
		public void onAutoFocus(boolean arg0, Camera arg1) {
			// TODO Auto-generated method stub
			Log.e("", "arg0 = "+ arg0);
			if (arg0){
				sound_pool.play( sound_beep, 1f, 1f, 0, 0, 1f );
				viewFocus.nSuccess = 0;
				arg1.cancelAutoFocus();
				viewFocus.invalidate();
			} else {

				viewFocus.nSuccess = 1;
				viewFocus.invalidate();
			}
			viewCamera.isRun = false;
			float focusDistances[] = new float[3];
			arg1.getParameters().getFocusDistances(focusDistances);



		}};


		@Override
		public void onBackPressed() {
			//super.onBackPressed();
			pressFinish();
		}
		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			viewCamera.releaseCamera();
			super.onDestroy();
		}



		private SensorEventListener accelerometerListener = new SensorEventListener() {
			int preX, preY, preZ;
			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}

			@Override
			public void onSensorChanged(SensorEvent event) {
				Sensor sensor = event.sensor;
				if ( sensor.getType() == Sensor.TYPE_GYROSCOPE ) {
					int gyroX = Math.round(event.values[0] * 1000);
					int    gyroY = Math.round(event.values[1] * 1000);
					int    gyroZ = Math.round(event.values[2] * 1000);

					int fixX =  preX * (-1)  - gyroX *(-1)  ;
					int ad = -500;
					fixX = Math.abs(fixX);

					if (  fixX  > 1000 ) {
						if ( viewCamera.isRun == false ) {
							viewCamera.isRun = true;
							resetFocus();
						}
					}
					preX = gyroX;
					preY = preY;
					preZ = preZ;
				} else {
					viewCamera.onAccelerometerSensorChanged(event);
				}

			}
		};

		@Override
		protected void onPause() {
			super.onPause();
			smManager.unregisterListener(accelerometerListener);
			mOrientationListener.disable();

		}

		@Override
		protected void onResume() {
			super.onResume();
			smManager.registerListener(accelerometerListener, sAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
			smManager.registerListener(accelerometerListener, sGyro, SensorManager.SENSOR_DELAY_NORMAL);
			mOrientationListener.enable();
			getLastfile();
		}


		// 설정에서 전달받은 값
		private OnSettingMenu settingListener = new OnSettingMenu() {

			@Override
			public void onFlashSetting() {
				// TODO Auto-generated method stub
				initFlashSetting();
			}

			@Override
			public void onClose() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGridSetting() {
				initGrid();

			}

			@Override
			public void onModeSetting() {
				// TODO Auto-generated method stub
				initCameaMode();
			}

			@Override
			public void onWBSetting() {
				// TODO Auto-generated method stub
				initWB();
			}

			@Override
			public void onSwitchCameraSetting() {
				viewCamera.switchCamera(app.getUserData().strSwitchCamera);
			}

			@Override
			public void onGyroCameraSetting() {
				Log.e("", "app.getUserData().strGyro = " + app.getUserData().strGyro);
				if (app.getUserData().strGyro.equals(getString(R.string.setting_common_on)) ) {
					imgGyro.setVisibility(View.VISIBLE);
					imgGyroBG.setVisibility(View.VISIBLE);
				} else {
					imgGyro.setVisibility(View.GONE);
					imgGyroBG.setVisibility(View.GONE);
				}

			}

			@Override
			public void onISOSetting() {
				initISO();

			}

			@Override
			public void onHDRSetting() {
				initHDRSetting();

			}

			@Override
			public void onShutter(String mode) {
				viewCamera.isMove = true;
				rlShutter.setVisibility(View.VISIBLE);
				BtnOK.setClickable(true);
				BtnOK.setOnTouchListener(moveListener);
				BtnOK.setOnClickListener(null);

			}
		};

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId()) {
			case R.id.btn_pause_resume:
				if(viewCamera.isRecording) {
					if(viewCamera.isPaused) {
						btnPauseResume.setBackgroundResource(R.drawable.icon_sr01);
						viewCamera.resumeRecord();
					} else {
						btnPauseResume.setBackgroundResource(R.drawable.icon_sr02);
						viewCamera.pauseRecord();
					}
				} else {
					if(viewCamera.isRecording == false) {
						btnPauseResume.setBackgroundResource(R.drawable.icon_sr01);
					}
					viewCamera.startRecorder(textTime);
				}
				break;
			case R.id.btn_record_hd:
				if(isHDRecord) {
					btnRecordHD.setBackgroundResource(R.drawable.icon_lm04_02);
				} else {
					btnRecordHD.setBackgroundResource(R.drawable.icon_lm04_01);
				}
				isHDRecord = !isHDRecord;
				break;
			case R.id.btn_record:
				if(viewCamera.isRecording == false) {
					btnPauseResume.setBackgroundResource(R.drawable.icon_sr01);
				}
				viewCamera.startRecorder(textTime);
				break;
			case R.id.btn_finish1:
				pressFinish();
				break;
			case R.id.btn_camera_change1:
				if ( app.getUserData().strSwitchCamera.equals(getString(R.string.setting_switch_f)) ) {
					app.getUserData().strSwitchCamera = getString(R.string.setting_switch_r);
					viewCamera.switchCamera(app.getUserData().strSwitchCamera);
				} else {
					app.getUserData().strSwitchCamera = getString(R.string.setting_switch_f);
					viewCamera.switchCamera(app.getUserData().strSwitchCamera);
				}
				break;
			case R.id.btn_camera_movie:
				viewChanger.setCurrentView(layoutMovie);
				viewCamera.changeMode(false);
				break;
			case R.id.btn_camera_photo:
				viewChanger.setCurrentView(layoutCamera);
				viewCamera.changeMode(true);
				break;
			}
		}
}
