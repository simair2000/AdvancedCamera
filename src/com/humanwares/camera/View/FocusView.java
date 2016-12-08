package com.humanwares.camera.View;

import android.R.bool;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera.Face;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

public class FocusView extends View {

	boolean haveFace;
	Paint drawingPaint;
	public int nSuccess = -1;	// 포커싱 성공
	
	boolean haveTouch;
	Rect touchArea;
	Face[] detectedFaces;
	int nRectSize = 90;
	Context mContext;

	public FocusView(Context context) {
		super(context);
		haveFace = false;
		drawingPaint = new Paint();
		drawingPaint.setColor(Color.GREEN);
		drawingPaint.setStyle(Paint.Style.STROKE); 
		drawingPaint.setStrokeWidth(2);
		haveTouch = false;
		mContext = context;
	}


	public void setFace(Face[] face) {
		detectedFaces = face;
	}


	public void setHaveFace(boolean h){
		haveFace = h;
	}

	public void setHaveTouch(boolean t, Rect tArea){
		haveTouch = t;
		touchArea = tArea;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		if ( nSuccess == 0 ) {
			drawingPaint.setColor(Color.GREEN);
		} else if ( nSuccess == 1 ) {
			drawingPaint.setColor(Color.RED);
		} else {
			drawingPaint.setColor(Color.WHITE);
		}
		if(haveTouch){
	
			canvas.drawRect(
					touchArea.left - nRectSize, touchArea.top -nRectSize, touchArea.right + nRectSize, touchArea.bottom + nRectSize,  
					drawingPaint);
		} 
	}

	



}
