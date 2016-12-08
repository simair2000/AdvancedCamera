package com.humanwares.camera.Activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;
import jp.wasabeef.glide.transformations.gpu.BrightnessFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.ToonFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.VignetteFilterTransformation;

import org.apache.commons.io.FileUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.humanwares.camera.CameraApp;
import com.humanwares.camera.R;
import com.humanwares.camera.View.ViewChanger;
import com.humanwares.camera.utils.Utils;

public class PreViewActivity extends Activity implements OnClickListener {
	
	ImageView imgPreView;
	private String lastFile;
	private CameraApp app;
	private ViewChanger viewChanger;
	private ViewChanger viewChangerEdit;
	private View cellBtn1;
	private View cellBtn2;
	private View cellBtn3;
	private View cellSeekbar;
	private View cellFilter;
	private SeekBar seekbarWB;
	private DrawableTypeRequest<File> gDrawable;
	private Bitmap currBitmap;
	private ColorFilterTransformation sharfFilter;
	private GrayscaleTransformation grayFilter;
	private VignetteFilterTransformation vignetteFilter;
	private ToonFilterTransformation toonFilter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview);
		app = (CameraApp) getApplicationContext();
		initView();
		init();
	}

	private void initView() {
		// TODO Auto-generated method stub
		findViewById(R.id.img_pre).setOnClickListener(this);
		findViewById(R.id.btnDelete).setOnClickListener(this);
		findViewById(R.id.btnSave).setOnClickListener(this);
		findViewById(R.id.btnEdit).setOnClickListener(this);
		findViewById(R.id.btnShare).setOnClickListener(this);
		findViewById(R.id.btnDelete2).setOnClickListener(this);
		findViewById(R.id.btnWB).setOnClickListener(this);
		findViewById(R.id.btnFilter).setOnClickListener(this);
		findViewById(R.id.btnEditSave).setOnClickListener(this);
		
		findViewById(R.id.imgFilter1).setOnClickListener(this);
		findViewById(R.id.imgFilter2).setOnClickListener(this);
		findViewById(R.id.imgFilter3).setOnClickListener(this);
		findViewById(R.id.imgFilter4).setOnClickListener(this);
		findViewById(R.id.imgFilterToon).setOnClickListener(this);
		
		cellBtn1 = findViewById(R.id.cellBtn1);
		cellBtn2 = findViewById(R.id.cellBtn2);
		cellBtn3 = findViewById(R.id.cellBtn3);
		viewChanger = new ViewChanger(cellBtn1, cellBtn2, cellBtn3);
		viewChanger.setCurrentView(cellBtn2);
		
		cellSeekbar = findViewById(R.id.cellSeekbar);
		cellFilter = findViewById(R.id.cellFilter);
		viewChangerEdit = new ViewChanger(cellSeekbar, cellFilter);
		
		seekbarWB = (SeekBar)findViewById(R.id.seekbarWB);
		seekbarWB.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				int progress = seekBar.getProgress();
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				float wb = (float)(progress) * 0.1f;
				gDrawable.asBitmap().transform(new BrightnessFilterTransformation(app, wb)).into(new SimpleTarget<Bitmap>() {


					@Override
					public void onResourceReady(Bitmap arg0, GlideAnimation<? super Bitmap> arg1) {
						// TODO Auto-generated method stub
						currBitmap = arg0;
						imgPreView.setImageBitmap(arg0);
					}
				});
			}
		});
	}
	
	private class SaveTask extends AsyncTask<Bitmap, Void, Void> {

		@Override
		protected Void doInBackground(Bitmap... params) {
			// TODO Auto-generated method stub
			try {
				if(currBitmap != null) {
					FileOutputStream os = new FileOutputStream(lastFile);
					os.write(Utils.bitmapToByteArray(currBitmap));
					os.flush();
					os.close();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
		
	}

	private void init() {
		imgPreView = (ImageView) findViewById(R.id.img_pre);

		getLastfile();
	}
	
	
	private void getLastfile () {
		File pathToFiles = null;
		if(app.getUserData().getStrSave().equals(getString(R.string.setting_save_external))) {
			pathToFiles = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "//EQCamera");
		} else {
			pathToFiles = getFilesDir();
		}
		if (pathToFiles.exists()) {
			String[] videoFileList = pathToFiles.list();
			if(videoFileList != null && videoFileList.length > 0) {
				lastFile = pathToFiles.getPath() +"/"+ videoFileList[videoFileList.length -1];
				gDrawable = Glide.with(this).load(new File(lastFile));
				gDrawable.into(imgPreView);
			} else {
				// nothing to do..
			}
			
//			Display display = getWindowManager().getDefaultDisplay(); 
//			int screenWidth = display.getWidth();
//			int screenHeight = display.getHeight();
//			
//			BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inSampleSize = 1;
//			Bitmap src = BitmapFactory.decodeFile( lastFile, options );
//			Bitmap resized = Bitmap.createScaledBitmap( src, screenWidth, screenHeight, true );
//			imgPreView.setImageBitmap(resized);
		
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.imgFilter1:
			gDrawable.asBitmap().into(imgPreView);
			break;
		case R.id.imgFilter2:
			gDrawable.asBitmap().transform(sharfFilter).into(new SimpleTarget<Bitmap>() {


				@Override
				public void onResourceReady(Bitmap arg0, GlideAnimation<? super Bitmap> arg1) {
					// TODO Auto-generated method stub
					currBitmap = arg0;
					imgPreView.setImageBitmap(arg0);
				}
			});
			break;
		case R.id.imgFilter3:
			gDrawable.asBitmap().transform(grayFilter).into(new SimpleTarget<Bitmap>() {


				@Override
				public void onResourceReady(Bitmap arg0, GlideAnimation<? super Bitmap> arg1) {
					// TODO Auto-generated method stub
					currBitmap = arg0;
					imgPreView.setImageBitmap(arg0);
				}
			});
			break;
		case R.id.imgFilter4:
			gDrawable.asBitmap().transform(vignetteFilter).into(new SimpleTarget<Bitmap>() {


				@Override
				public void onResourceReady(Bitmap arg0, GlideAnimation<? super Bitmap> arg1) {
					// TODO Auto-generated method stub
					currBitmap = arg0;
					imgPreView.setImageBitmap(arg0);
				}
			});
			break;
		case R.id.imgFilterToon:
			gDrawable.asBitmap().transform(toonFilter).into(new SimpleTarget<Bitmap>() {


				@Override
				public void onResourceReady(Bitmap arg0, GlideAnimation<? super Bitmap> arg1) {
					// TODO Auto-generated method stub
					currBitmap = arg0;
					imgPreView.setImageBitmap(arg0);
				}
			});
			break;
		case R.id.btnEditSave:
			new SaveTask().execute(currBitmap);
			finish();
			break;
		case R.id.img_pre:
			finish();
			break;
		case R.id.btnDelete:
		case R.id.btnDelete2:
			if(TextUtils.isEmpty(lastFile) == false) {
				FileUtils.deleteQuietly(new File(lastFile));
			}
			finish();
			break;
		case R.id.btnSave:
			// nothing to do : 이미 저장했음 ㅡ.ㅡ;;
//			viewChanger.setCurrentView(cellBtn2);
			finish();
			break;
		case R.id.btnShare:
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("image/*");
			Uri uri = Uri.fromFile(new File(lastFile));
			shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
			startActivity(Intent.createChooser(shareIntent, "공유"));
			break;
		case R.id.btnEdit:
			viewChanger.setCurrentView(cellBtn3);
			break;
		case R.id.btnWB:
			viewChangerEdit.setCurrentView(cellSeekbar);
			break;
		case R.id.btnFilter:
			initFilters();
			viewChangerEdit.setCurrentView(cellFilter);
			break;
		}
	}

	private void initFilters() {
		// TODO Auto-generated method stub
		ImageView imgFilter1 = (ImageView)findViewById(R.id.imgFilter1);
		ImageView imgFilter2 = (ImageView)findViewById(R.id.imgFilter2);
		ImageView imgFilter3 = (ImageView)findViewById(R.id.imgFilter3);
		ImageView imgFilter4 = (ImageView)findViewById(R.id.imgFilter4);
		ImageView imgFilterToon = (ImageView)findViewById(R.id.imgFilterToon);
	
		sharfFilter = new ColorFilterTransformation(this, Color.argb(10, 20, 20, 20));
		grayFilter = new GrayscaleTransformation(this);
		vignetteFilter = new VignetteFilterTransformation(this);
		toonFilter = new ToonFilterTransformation(this);
		
		Glide.with(this).load(new File(lastFile)).thumbnail(0.1f).into(imgFilter1);
		Glide.with(this).load(new File(lastFile)).asBitmap().transform(sharfFilter).thumbnail(0.1f).into(imgFilter2);
		Glide.with(this).load(new File(lastFile)).asBitmap().transform(grayFilter).thumbnail(0.1f).into(imgFilter3);
		Glide.with(this).load(new File(lastFile)).asBitmap().transform(vignetteFilter).thumbnail(0.1f).into(imgFilter4);
		Glide.with(this).load(new File(lastFile)).asBitmap().transform(toonFilter).thumbnail(0.1f).into(imgFilterToon);
	}
	
}
