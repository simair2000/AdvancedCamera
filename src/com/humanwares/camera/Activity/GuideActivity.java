package com.humanwares.camera.Activity;

import com.humanwares.camera.R;
import com.humanwares.camera.View.ViewChanger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class GuideActivity extends Activity implements OnClickListener {
	
	public static Intent getIntent(Context context) {
		Intent i = new Intent(context, GuideActivity.class);
		return i;
	}

	private ViewChanger viewChanger;
	private View page1;
	private View page2;
	private View page3;
	private View page4;
	private View page5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		initView();
	}

	private void initView() {
		page1 = findViewById(R.id.page1);
		page2 = findViewById(R.id.page2);
		page3 = findViewById(R.id.page3);
		page4 = findViewById(R.id.page4);
		page5 = findViewById(R.id.page5);
		
		viewChanger = new ViewChanger(page1, page2, page3, page4, page5);
		viewChanger.setCurrentView(page1);
		
		findViewById(R.id.btnClose).setOnClickListener(this);
		findViewById(R.id.btnNext1).setOnClickListener(this);
		findViewById(R.id.btnNext2).setOnClickListener(this);
		findViewById(R.id.btnNext3).setOnClickListener(this);
		findViewById(R.id.btnNext4).setOnClickListener(this);
		findViewById(R.id.btnPrev2).setOnClickListener(this);
		findViewById(R.id.btnPrev3).setOnClickListener(this);
		findViewById(R.id.btnPrev4).setOnClickListener(this);
		findViewById(R.id.btnPrev5).setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()) {
		case R.id.btnClose:
			finish();
			break;
		case R.id.btnNext1:
			viewChanger.setCurrentView(page2);
			break;
		case R.id.btnNext2:
			viewChanger.setCurrentView(page3);
			break;
		case R.id.btnNext3:
			viewChanger.setCurrentView(page4);
			break;
		case R.id.btnNext4:
			viewChanger.setCurrentView(page5);
			break;
		case R.id.btnPrev2:
			viewChanger.setCurrentView(page1);
			break;
		case R.id.btnPrev3:
			viewChanger.setCurrentView(page2);
			break;
		case R.id.btnPrev4:
			viewChanger.setCurrentView(page3);
			break;
		case R.id.btnPrev5:
			viewChanger.setCurrentView(page4);
			break;
		}
	}
}
