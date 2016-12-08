package com.humanwares.camera.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.hardware.SensorEvent;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.humanwares.camera.CameraApp;
import com.humanwares.camera.CameraException;
import com.humanwares.camera.CameraException.Errors;
import com.humanwares.camera.R;
import com.humanwares.camera.Activity.MainActivity;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

	public interface OnCameraListener {
		public void onCapture(); // 사진 눌렀을

		public void onTouchFocus(Rect rect); // 카메라뷰 터치시

		public void onCameraStart(); // 카메라 동작 시작

		public void onAccelerometer(double lv, double x, double y); // 가속도계

		public void onZoom(int v); // 줌설정

		public void onBrightness(int v); // 밝기 설정

		public void onTouchUp();
	}

	private static final String TAG = CameraView.class.getSimpleName();

	private static final int RECORDING_TIME = 60000 * 5; // 동영상 최대 촬영 시간 5분

	public Camera mCamera;
	private SurfaceHolder mHolder;
	private PictureCallback rawCallback;
	private ShutterCallback shutterCallback;
	private PictureCallback jpegCallback;

	private Context mContext;
	private boolean isPreviewRunning = false;
	private boolean cameraFront = false; // 전면 카메라 여부
	public int currentZoomLevel = 0, maxZoomLevel = 4; // 줌설정
	public boolean isGrid = false; // 그리드 표시
	public boolean isMove = false; // 셔터 이동
	public boolean isMode = false; // 모드
	public boolean isFlash = false; // flash Mode
	public int nBrightness = 0; // 밝기
	public int nColorFilter = 0; // 컬러 모드
	public int nStillCount = 0; // 연사 갯수
	public boolean isStillMode = false; // 연사 모드
	public int current_orientation = 0; // 현재 회전상태
	public boolean isHDR = false;
	public boolean isRun = false; // 포커싱 동작중

	private OnCameraListener listener;
	private CameraApp app;

	public static CameraView instance;

	private Timer timer;
	private MediaRecorder recorder = null;
	public boolean isRecording;
	private File videoFile;
	private ArrayList<File> videoFiles = new ArrayList<File>();
	protected int elapsedTime;
	private TextView timeView;

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			int seconds = (int) (elapsedTime) % 60;
			int minutes = (int) ((elapsedTime / (60)) % 60);
			// int hours = (int) ((elapsedTime / (60*60)) % 24);
			timeView.setText(String.format("%02d:%02d", minutes, seconds)); // this
																			// is
																			// the
																			// textview
		}
	};

	public boolean isPaused;

	public void startTimer() {
		timer = new Timer();
		elapsedTime = 0;
		TimerTask timerTask = new TimerTask() {
			public void run() {
				elapsedTime += 1;
				mHandler.obtainMessage(1).sendToTarget();
			}
		};
		timer.scheduleAtFixedRate(timerTask, 0, 1000);
	};

	public void stopTimer() {
		timer.cancel();
		elapsedTime = 0;
		timeView.setText("00:00");
	}

	public void pauseTimer() {
		timer.cancel();
	}

	public void resumeTimer() {
		timer = new Timer();
		TimerTask timerTask = new TimerTask() {
			public void run() {
				elapsedTime += 1;
				mHandler.obtainMessage(1).sendToTarget();
			}
		};
		timer.scheduleAtFixedRate(timerTask, 0, 1000);
	}

	private void mergeMp4Files() {
		if (videoFiles.size() > 1) {
			
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File(sdCard.getAbsolutePath() + "/EQCamera");
			try {
				Movie[] inMovies = new Movie[videoFiles.size()];
				for(int i = 0; i < videoFiles.size(); i++) {
					File file = videoFiles.get(i);
					inMovies[i] = MovieCreator.build(file.getAbsolutePath());
				}
				
				List<Track> videoTracks = new LinkedList<Track>();
				List<Track> audioTracks = new LinkedList<Track>();
				for (Movie m : inMovies) {
					for (Track t : m.getTracks()) {
						if (t.getHandler().equals("soun")) {
							audioTracks.add(t);
						}
						if (t.getHandler().equals("vide")) {
							videoTracks.add(t);
						}
					}
				}
				Movie result = new Movie();
				if (audioTracks.size() > 0) {
					result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
				}
				if (videoTracks.size() > 0) {
					result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
				}
				
				Container out = new DefaultMp4Builder().build(result);
				FileOutputStream fos = new FileOutputStream(new File(dir, "out.mp4"));
				FileChannel fc = fos.getChannel();
				out.writeContainer(fc);
				fc.close();
				fos.close();
				
			} catch (Exception e) {
				Toast.makeText(getContext(), "" + e, Toast.LENGTH_LONG).show();
			} finally {
				File first = videoFiles.get(0);
				String name = first.getName();
				for (File file : videoFiles) {
					FileUtils.deleteQuietly(file);
				}
				File out = new File(dir, "out.mp4");
				out.renameTo(new File(dir, name));
			}
		}
		videoFiles.clear();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	public void startRecorder(TextView timeView) {
		this.timeView = timeView;
		if (isRecording) {
			stopRecord();
			return;
		}
		recordVideo();
		isRecording = true;
		startTimer();
	}

	private void recordVideo() {
		try {
			FileOutputStream outStream = null;
			String fileName = String.format("%d.mp4", System.currentTimeMillis());
			videoFile = null;
			if (app.getUserData().getStrSave().equals(mContext.getString(R.string.setting_save_external))) {
				// Write to SD Card
				File sdCard = Environment.getExternalStorageDirectory();
				File dir = new File(sdCard.getAbsolutePath() + "/EQCamera");
				dir.mkdirs();
				videoFile = new File(dir, fileName);
			} else {
				videoFile = new File(mContext.getFilesDir(), fileName);
			}
			videoFiles.add(videoFile);
			mCamera.stopPreview();
			mCamera.unlock();
			recorder.setCamera(mCamera);

			// Step 2: Set sources
			recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
			recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

			// Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
			if (MainActivity.instance.isHDRecord) {
				recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
			} else {
				recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
			}

			// Step 4: Set output file
			recorder.setOutputFile(videoFile.getAbsolutePath());
			// Step 5: Set the preview output
			recorder.setPreviewDisplay(mHolder.getSurface());
			// Step 6: Prepare configured MediaRecorder
			recorder.setMaxDuration(5 * 60 * 1000); // 최대 5분
			recorder.setOnInfoListener(new OnInfoListener() {

				@Override
				public void onInfo(MediaRecorder mr, int what, int extra) {
					if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {

						mCamera.stopPreview();
						releaseMediaRecorder();

						/*
						 * initiate media scan and put the new things into the
						 * path array to make the scanner aware of the location
						 * and the files you want to see
						 */MediaScannerConnection.scanFile(MainActivity.instance, new String[] { videoFile.getPath() }, null, null);

					}

				}
			});
			recorder.prepare();
			recorder.start();
		} catch (Exception e) {
			Log.e("Error Stating CuXtom Camera", e.getMessage());
		}
	}

	public void stopRecord() {
		if (isPaused == false) {
			recorder.stop();
		}
		isPaused = true;
		isRecording = false;
		stopTimer();
		mergeMp4Files();
	}

	public void pauseRecord() {
		isPaused = true;
		recorder.stop();
		pauseTimer();
	}

	public void resumeRecord() {
		isPaused = false;
		recordVideo();
		resumeTimer();
	}

	private void releaseMediaRecorder() {
		if (recorder != null) {
			recorder.reset(); // clear recorder configuration
			recorder.release(); // release the recorder object
			recorder = null;
		}
		isRecording = false;
	}

	public CameraView(Context c, AttributeSet att) {
		// TODO Auto-generated constructor stub
		super(c, att);
		mContext = c;
		app = (CameraApp) mContext.getApplicationContext();
		instance = this;
		recorder = new MediaRecorder();
		this.setWillNotDraw(false);
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		rawCallback = new PictureCallback() {
			public void onPictureTaken(byte[] data, Camera camera) {
				Log.e("Log", "onPictureTaken - raw");
			}
		};

		shutterCallback = new ShutterCallback() {
			public void onShutter() {
				Log.i("Log", "onShutter'd");
			}
		};

		jpegCallback = new PictureCallback() {
			public void onPictureTaken(byte[] data, Camera camera) {
				SaveImageTask a = new SaveImageTask();
				a.temp = listener;
				a.execute(data);

				mCamera.startPreview();
				try {

					if (isStillMode == true) {
						nStillCount++;
						Log.e("", "viewCamera.nStillCount = " + nStillCount);
						if (nStillCount < 20) {
							mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
						} else {
							nStillCount = 0;
							isStillMode = false;
						}
					}

				} catch (Exception e) {
					// TODO: handle exception
				}

				Log.e("Log", "onPictureTaken - jpeg");

			}
		};
	}

	public void setCameraListener(OnCameraListener l) {
		listener = l;
	}

	@Override
	protected void onDraw(Canvas canvas) {

		if (isGrid) {
			// Find Screen size first
			DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
			int screenWidth = metrics.widthPixels;
			int screenHeight = metrics.heightPixels;
			Paint paint = new Paint();
			// Set paint options
			paint.setAntiAlias(true);
			paint.setStrokeWidth(3);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.argb(255, 255, 255, 255));

			canvas.drawLine((screenWidth / 3) * 2, 0, (screenWidth / 3) * 2, screenHeight, paint);
			canvas.drawLine((screenWidth / 3), 0, (screenWidth / 3), screenHeight, paint);
			canvas.drawLine(0, (screenHeight / 3) * 2, screenWidth, (screenHeight / 3) * 2, paint);
			canvas.drawLine(0, (screenHeight / 3), screenWidth, (screenHeight / 3), paint);
		}
		super.onDraw(canvas);
	}

	public void changeMode(boolean isCamera) {
		if (isCamera) {
			// 카메라 모드
			try {
				mCamera.setPreviewDisplay(mHolder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mCamera.startPreview();
		} else {
			// 동영상 모드
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
		try {
			mCamera.setPreviewDisplay(holder);
			isPreviewRunning = true;
			mCamera.startPreview();
			if (listener != null)
				listener.onCameraStart();

			Parameters params = mCamera.getParameters();
			Log.d(TAG, params.flatten());
			List<Size> sizes = params.getSupportedPictureSizes();
			Size currentSize = params.getPictureSize();
			for (Size size : sizes) {
				Log.e(TAG, "resolution : " + size.width + " x " + size.height);
			}
			Log.w(TAG, "Curent : " + currentSize.width + " x " + currentSize.height);

			LayoutParams p = getLayoutParams();
			PictureResolution size = getCurrentPictureSize();
			p.width = size.width;
			p.height = size.height;
			setLayoutParams(p);
		} catch (IOException exception) {
			mCamera.release();
			mCamera = null;
		}
	}

	/**
	 * 사진 해상도 관련 enum
	 * @author simair
	 *
	 */
	public enum PictureResolution {
		HIGH_16_9("고화질 16:9", 1920, 1080),
		HIGH_4_3("고화질 4:3", 1280, 960),
		NORMAL_16_9("일반화질 16:9", 800, 480),
		NORMAL_4_3("일반화질 4:3", 640, 480),
		;
		
		public String title;
		public int width;
		public int height;
		
		private PictureResolution(String title, int width, int height) {
			// TODO Auto-generated constructor stub
			this.title = title;
			this.width = width;
			this.height = height;
		}
		
		public static List<PictureResolution> getSupportedResolution(List<Size> list) {
			ArrayList<PictureResolution> ret = new ArrayList<CameraView.PictureResolution>();
			PictureResolution[] values = PictureResolution.values();
			for(PictureResolution value : values) {
				SIZE_LOOP : for(Size size : list) {
					if(value.width == size.width && value.height == size.height) {
						ret.add(value);
						break SIZE_LOOP;
					}
				}
			}
			return ret;
		}
		
		public static PictureResolution getPictureResolution(Size size) {
			PictureResolution[] values = PictureResolution.values();
			for(PictureResolution value : values) {
				if(value.width == size.width && value.height == size.height) {
					return value;
				}
			}
			return null;
		}
	}
	
	public List<PictureResolution> getSupportedPictureSizes() {
		if (mCamera == null) {
			return null;
		}
		Parameters params = mCamera.getParameters();
		List<Size> pictureSizes = params.getSupportedPictureSizes();

		checkSupportedPictureSizeAtPreviewSize(pictureSizes);
		return PictureResolution.getSupportedResolution(pictureSizes);
	}

	private void checkSupportedPictureSizeAtPreviewSize(List<Size> pictureSizes) {
		Parameters params = mCamera.getParameters();
		List<Size> previewSizes = params.getSupportedPreviewSizes();
		Camera.Size pictureSize;
		Camera.Size previewSize;
		double pictureRatio = 0;
		double previewRatio = 0;
		final double aspectTolerance = 0.05;
		boolean isUsablePicture = false;

		for (int indexOfPicture = pictureSizes.size() - 1; indexOfPicture >= 0; --indexOfPicture) {
			pictureSize = pictureSizes.get(indexOfPicture);
			pictureRatio = (double) pictureSize.width / (double) pictureSize.height;
			isUsablePicture = false;

			for (int indexOfPreview = previewSizes.size() - 1; indexOfPreview >= 0; --indexOfPreview) {
				previewSize = previewSizes.get(indexOfPreview);

				previewRatio = (double) previewSize.width / (double) previewSize.height;

				if (Math.abs(pictureRatio - previewRatio) < aspectTolerance) {
					isUsablePicture = true;
					break;
				}
			}

			if (isUsablePicture == false) {
				pictureSizes.remove(indexOfPicture);
			}
		}
	}

	public PictureResolution getCurrentPictureSize() {
		Parameters params = mCamera.getParameters();
		Size currentSize = params.getPictureSize();
		return PictureResolution.getPictureResolution(currentSize);
	}

	public void setPictureSize(int width, int height) {
		Parameters params = mCamera.getParameters();
		params.setPictureSize(width, height);

		double aspectTolerance = 0.05;
		double pictureRatio = (double) width / (double) height;
		List<Size> previewSizes = params.getSupportedPreviewSizes();
		for (Size previewSize : previewSizes) {
			double previewRatio = (double) previewSize.width / (double) previewSize.height;
			if (Math.abs(pictureRatio - previewRatio) < aspectTolerance) {
				params.setPreviewSize(previewSize.width, previewSize.height);
				LayoutParams p = getLayoutParams();
				p.width = previewSize.width;
				p.height = previewSize.height;
				setLayoutParams(p);
				break;
			}
		}
		mCamera.setParameters(params);
	}

	public static String sizeToString(Size size) {
		return size.width + " x " + size.height;
	}

	public boolean isSupportMeteringMode() throws CameraException {
		if (mCamera == null) {
			throw new CameraException(Errors.ERROR_NOT_READY);
		}
		int max = mCamera.getParameters().getMaxNumMeteringAreas();
		if (max == 0) {
			return false;
		}
		return true;
	}

	public boolean setMeteringMode(String mode) {
		if (mCamera == null) {
			return false;
		}

		Parameters p = mCamera.getParameters();
		if (mode.equals(mContext.getString(R.string.setting_metering_c))) {
			// 중앙 : 화면 중간 부분에 60% 광량 측정
			p.set("metering", "center");
		} else if (mode.equals(mContext.getString(R.string.setting_metering_m))) {
			// 다분할 : 화면 전체를 골고루 측광
			p.set("metering", "matrix");
		} else if (mode.equals(mContext.getString(R.string.setting_metering_s))) {
			// 스팟 : 화면 중간 부분에 100% 광량 측정
			p.set("metering", "spot");
		}
		try {
			mCamera.setParameters(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	protected void setDisplayOrientation(Camera camera, int angle) {
		Method downPolymorphic;
		try {
			downPolymorphic = camera.getClass().getMethod("setDisplayOrientation", new Class[] { int.class });
			if (downPolymorphic != null)
				downPolymorphic.invoke(camera, new Object[] { angle });
		} catch (Exception e1) {
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.e("", "surfaceChanged");

		Camera.Parameters parameters = mCamera.getParameters();
		// Log.e("", "width = " + width + " // height = " + height);
		List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
		parameters.setPreviewSize(previewSizes.get(0).width, previewSizes.get(0).height);
		mCamera.setParameters(parameters);

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
		if (timer != null) {
			timer.cancel();
		}
	}

	/**
	 * 사진촬영
	 * 
	 * @param mute
	 *            - true이면 셔터 사운드 없이 사진촬영을 한다
	 */
	public void capture(boolean mute) {
		PictureResolution size = getCurrentPictureSize();
		int width = size.width;
		int height = size.height;
		if (app.getUserData().strSound.equals(mContext.getString(R.string.setting_common_off))) {
			mute = true;
		} else {
			mute = false;
		}
		if (mute) {
			final AudioManager manager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
			manager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
			mCamera.takePicture(null, rawCallback, jpegCallback);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					manager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
				}
			}, 1000);
		} else {
			mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
		}
		setPictureSize(width, height);
	}

	// 카메라 변경
	public void switchCamera(String mode) {
		int camerasNumber = Camera.getNumberOfCameras();
		if (camerasNumber > 1) {
			releaseCamera();
			if (mode.equals(mContext.getString(R.string.setting_switch_f))) {
				chooseFrontCamera();
			} else {
				chooseBackCamera();
			}
		} else {
			Toast toast = Toast.makeText(mContext, mContext.getString(R.string.no_hdr), Toast.LENGTH_LONG);
			toast.show();
		}
	}

	// 카메라 종료
	public void releaseCamera() {
		// stop and release camera
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}

	public void chooseBackCamera() {
		// if the camera preview is the front
		int cameraId = findBackFacingCamera();
		if (cameraId >= 0) {

			mCamera = Camera.open(cameraId);
			rawCallback = getPictureCallback();
			refreshCamera(mCamera);
		}

	}

	public void chooseFrontCamera() {
		int cameraId = findFrontFacingCamera();
		if (cameraId >= 0) {
			// open the backFacingCamera
			// set a picture callback
			// refresh the preview

			mCamera = Camera.open(cameraId);
			rawCallback = getPictureCallback();
			refreshCamera(mCamera);
		}
	}

	public void chooseCamera() {
		// if the camera preview is the front
		if (cameraFront) {
			int cameraId = findBackFacingCamera();
			if (cameraId >= 0) {

				mCamera = Camera.open(cameraId);
				rawCallback = getPictureCallback();
				refreshCamera(mCamera);
			}
		} else {
			int cameraId = findFrontFacingCamera();
			if (cameraId >= 0) {
				// open the backFacingCamera
				// set a picture callback
				// refresh the preview

				mCamera = Camera.open(cameraId);
				rawCallback = getPictureCallback();
				refreshCamera(mCamera);
			}
		}
	}

	private File getOutputMediaFile() {
		// make a new file directory inside the "sdcard" folder
		File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "//EQCamera");

		// if this "JCGCamera folder does not exist
		if (!mediaStorageDir.exists()) {
			// if you cannot make this folder return
			if (!mediaStorageDir.mkdirs()) {
				mediaStorageDir.mkdirs();
			}
		}

		// take the current timeStamp
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		// and make a media file:
		mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

		return mediaFile;
	}

	private PictureCallback getPictureCallback() {
		PictureCallback picture = new PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				// make a new picture file
				File pictureFile = getOutputMediaFile();

				if (pictureFile == null || data == null) {
					return;
				}
				try {
					// write the file
					FileOutputStream fos = new FileOutputStream(pictureFile);
					fos.write(data);
					fos.close();
					Toast toast = Toast.makeText(mContext, "Picture saved: " + pictureFile.getName(), Toast.LENGTH_LONG);
					toast.show();

				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				}

				// refresh camera to continue preview
				refreshCamera(mCamera);
			}
		};
		return picture;
	}

	public void refreshCamera(Camera camera) {
		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}
		// stop preview before making changes
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}
		// set preview size and make any resize, rotate or
		// reformatting changes here
		// start preview with new settings
		setCamera(camera);
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();

		} catch (Exception e) {
			Log.e(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());
		}
	}

	public void setCamera(Camera camera) {
		// method to set a camera instance
		mCamera = camera;
	}

	private int findFrontFacingCamera() {
		int cameraId = -1;
		// Search for the front facing camera
		int numberOfCameras = Camera.getNumberOfCameras();
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				cameraId = i;
				cameraFront = true;
				break;
			}
		}
		return cameraId;
	}

	private int findBackFacingCamera() {
		int cameraId = -1;
		// Search for the back facing camera
		// get the number of cameras
		int numberOfCameras = Camera.getNumberOfCameras();
		// for every camera check
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
				cameraId = i;
				cameraFront = false;
				break;
			}
		}
		return cameraId;
	}

	// 플래쉬 On
	public void flashOn() {
		Parameters p = mCamera.getParameters();
		p.setFlashMode(Parameters.FLASH_MODE_ON);
		mCamera.setParameters(p);
	}

	// 플래쉬 Auto
	public void flashAuto() {
		Parameters p = mCamera.getParameters();
		p.setFlashMode(Parameters.FLASH_MODE_AUTO);
		mCamera.setParameters(p);
	}

	// 플래쉬 Auto
	public void flashOff() {
		Parameters p = mCamera.getParameters();
		p.setFlashMode(Parameters.FLASH_MODE_OFF);
		mCamera.setParameters(p);
	}

	public static byte[] bitmapToByteArray(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		return byteArray;
	}

	public class SaveImageTask extends AsyncTask<byte[], Void, Void> {

		public OnCameraListener temp;
		private byte[] imgData;

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			if (temp != null)
				temp.onCapture();
			super.onPostExecute(result);
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(byte[]... data) {

			imgData = data[0];
			publishProgress();
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			if (app.getFilters() != null) {

				Glide.with(mContext).load(imgData).asBitmap().transform(app.getFilters()).into(new SimpleTarget<Bitmap>(1024, 600) {

					@Override
					public void onResourceReady(Bitmap arg0, GlideAnimation<? super Bitmap> arg1) {
						// TODO Auto-generated method stub
						try {
							File outFile = null;
							FileOutputStream outStream = null;
							String fileName = String.format("%d.jpg", System.currentTimeMillis());
							if (app.getUserData().getStrSave().equals(mContext.getString(R.string.setting_save_external))) {
								// Write to SD Card
								File sdCard = Environment.getExternalStorageDirectory();
								File dir = new File(sdCard.getAbsolutePath() + "/EQCamera");
								dir.mkdirs();

								outFile = new File(dir, fileName);
							} else {
								outFile = new File(mContext.getFilesDir(), fileName);
							}

							outStream = new FileOutputStream(outFile);
							outStream.write(bitmapToByteArray(arg0));
							outStream.flush();
							outStream.close();

						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
						}
					}
				});
			} else {
				Glide.with(mContext).load(imgData).asBitmap().into(new SimpleTarget<Bitmap>(1024, 600) {

					@Override
					public void onResourceReady(Bitmap arg0, GlideAnimation<? super Bitmap> arg1) {
						// TODO Auto-generated method stub
						// Write to SD Card
						try {
							File outFile = null;
							FileOutputStream outStream = null;
							String fileName = String.format("%d.jpg", System.currentTimeMillis());
							if (app.getUserData().getStrSave().equals(mContext.getString(R.string.setting_save_external))) {
								// Write to SD Card
								File sdCard = Environment.getExternalStorageDirectory();
								File dir = new File(sdCard.getAbsolutePath() + "/EQCamera");
								dir.mkdirs();

								outFile = new File(dir, fileName);
							} else {
								outFile = new File(mContext.getFilesDir(), fileName);
							}

							outStream = new FileOutputStream(outFile);
							outStream.write(bitmapToByteArray(arg0));
							outStream.flush();
							outStream.close();

						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
						}
					}
				});
			}
		}
	}

	private double level_angle = 0.0f;
	private double orig_level_angle = 0.0f;
	private final float sensor_alpha = 0.8f;
	private float[] gravity = new float[3];

	// 각도 변화
	public void onAccelerometerSensorChanged(SensorEvent event) {

		for (int i = 0; i < 3; i++) {
			this.gravity[i] = sensor_alpha * this.gravity[i] + (1.0f - sensor_alpha) * event.values[i];
		}

		double x = gravity[0];
		double y = gravity[1];
		// Log.e("", "x = " + x + "//" + y );
		this.level_angle = Math.atan2(-x, y) * 180.0 / Math.PI;
		if (this.level_angle < -0.0) {
			this.level_angle += 360.0;
		}
		this.orig_level_angle = this.level_angle;
		this.level_angle -= (float) 270;
		if (this.level_angle < -180.0) {
			this.level_angle += 360.0;
		} else if (this.level_angle > 180.0) {
			this.level_angle -= 360.0;
		}

		if (listener != null)
			listener.onAccelerometer(level_angle, x, y);
	}

	float x1, x2, y1, y2, dx, dy;
	float minisize = 0;
	String directionX = "";
	String directionY = "";
	boolean isZoom = false;
	boolean isBrightness = false;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		detector.onTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			float x = event.getX();
			float y = event.getY();
			x1 = event.getX();
			y1 = event.getY();
			float touchMajor = event.getTouchMajor();
			float touchMinor = event.getTouchMinor();
			Rect touchRect = new Rect((int) (x - touchMajor / 2), (int) (y - touchMinor / 2), (int) (x + touchMajor / 2), (int) (y + touchMinor / 2));
			Log.e("", "x = " + x);
			if (x > 150 && x < 1530) {
				if (listener != null)
					listener.onTouchFocus(touchRect);
			}
			return true;
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			isZoom = false;
			isBrightness = false;
			listener.onTouchUp();
		}

		return true;
	}

	private void brightnessUp() {
		Parameters p = mCamera.getParameters();
		int minBrightness = mCamera.getParameters().getMinExposureCompensation();
		int maxBrightness = mCamera.getParameters().getMaxExposureCompensation();
		if (maxBrightness > 2) {
			maxBrightness = 2;
		}

		Log.e("", "app.getUserData().nBrightness = " + app.getUserData().nBrightness + "//" + maxBrightness);
		if (app.getUserData().nBrightness < maxBrightness) {
			app.getUserData().nBrightness += 1;
			p.setExposureCompensation(app.getUserData().nBrightness);
			mCamera.setParameters(p);
		}
		listener.onBrightness(app.getUserData().nBrightness);
		// Log.e("", "minBrightness = " + minBrightness + "maxBrightness =" +
		// maxBrightness + "nowBrightness =" + app.getUserData().nBrightness);
	}

	private void brightnessDown() {
		Parameters p = mCamera.getParameters();
		int minBrightness = mCamera.getParameters().getMinExposureCompensation();
		int maxBrightness = mCamera.getParameters().getMaxExposureCompensation();
		if (minBrightness < -3) {
			minBrightness = -2;
		}

		Log.e("", "app.getUserData().nBrightness = " + app.getUserData().nBrightness + "//" + minBrightness);
		if (app.getUserData().nBrightness > minBrightness) {
			app.getUserData().nBrightness -= 1;
			p.setExposureCompensation(app.getUserData().nBrightness);
			mCamera.setParameters(p);
		}
		listener.onBrightness(app.getUserData().nBrightness);
		// Log.e("", "minBrightness = " + minBrightness + "maxBrightness =" +
		// maxBrightness + "nowBrightness =" + app.getUserData().nBrightness);
	}

	String maxZoom = "";

	private void zoomIn() {
		Camera.Parameters parameters;
		parameters = mCamera.getParameters();
		if (maxZoom.equals("")) {
			maxZoom = parameters.get("max-zoom");
		}
		if (app.getUserData().nZoom < 10) {
			app.getUserData().nZoom += 1;
			parameters.setZoom(app.getUserData().nZoom);
			mCamera.setParameters(parameters);
		}
		// Log.e("", "maxZoom = " + maxZoom + "/" + app.getUserData().nZoom);
		listener.onZoom(app.getUserData().nZoom);
	}

	private void zoomOut() {
		Camera.Parameters parameters;
		parameters = mCamera.getParameters();

		if (app.getUserData().nZoom > 0) {
			app.getUserData().nZoom -= 1;
			parameters.setZoom(app.getUserData().nZoom);
			mCamera.setParameters(parameters);
		}
		// Log.e("", "maxZoom out = " + maxZoom + "/" +
		// app.getUserData().nZoom);
		listener.onZoom(app.getUserData().nZoom);
	}

	PanGestureListener penlistener = new PanGestureListener();
	GestureDetector detector = new GestureDetector(mContext, penlistener);

	class PanGestureListener extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

			// if (e1.getX() < e2.getX()) {
			// Log.e("", "Left to Right swipe performed");
			// }
			//
			// if (e1.getX() > e2.getX()) {
			// Log.e("", "Right to Left swipe performed");
			// }
			//
			// if (e1.getY() < e2.getY()) {
			// Log.e("", "Up to Down swipe performed");
			// }
			//
			// if (e1.getY() > e2.getY()) {
			// Log.e("", "Down to Up swipe performed");
			// }
			//
			Log.e("", "distance  = " + distanceX);
			if (e1.getX() > e2.getX() && e1.getY() > e2.getY()) {

				if (isBrightness == false) {
					zoomIn();
					isZoom = true;
				}

				Log.e("", "zoom in");
			} else if (e1.getX() < e2.getX() && e1.getY() < e2.getY()) {

				if (isBrightness == false) {
					zoomOut();
					isZoom = true;
				}

				Log.e("", "zoom out");
			} else if (e1.getX() < e2.getX() && e1.getY() > e2.getY()) {

				if (isZoom == false) {
					brightnessUp();
					isBrightness = true;
				}

				Log.e("", "brightnessUp");
			} else if (e1.getX() > e2.getX() && e1.getY() < e2.getY()) {

				if (isZoom == false) {
					brightnessDown();
					isBrightness = true;
				}

				Log.e("", "brightnesDown");
			}

			return true;
		}

	}

}
