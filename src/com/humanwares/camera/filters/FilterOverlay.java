package com.humanwares.camera.filters;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.humanwares.camera.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class FilterOverlay extends BitmapTransformation {
	
	

	private Bitmap overlay;
	private Context context;

	public FilterOverlay(BitmapPool bitmapPool) {
		super(bitmapPool);
		// TODO Auto-generated constructor stub
	}

	public FilterOverlay(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	
	public FilterOverlay setOverlay(Bitmap overlay) {
		this.overlay = overlay;
		return this;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return getClass().getName();
	}

	@Override
	protected Bitmap transform(BitmapPool arg0, Bitmap arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		Bitmap result = arg0.get(arg2, arg3, Bitmap.Config.ARGB_8888);
		if(result == null) {
			result = Bitmap.createBitmap(arg2, arg3, Bitmap.Config.ARGB_8888);
		}
		final Canvas canvas = new Canvas(result);
		final Paint paint = new Paint();
		canvas.drawBitmap(arg1, 0, 0, paint);
		Glide.with(context).load(R.drawable.ic_launcher).asBitmap().transform(new BlurTransformation(context)).into(new SimpleTarget<Bitmap>() {

			@Override
			public void onResourceReady(Bitmap arg0, GlideAnimation<? super Bitmap> arg1) {
				// TODO Auto-generated method stub
				
				canvas.drawBitmap(arg0, 0, 0, paint);
			}
		});
		return result;
	}

}
