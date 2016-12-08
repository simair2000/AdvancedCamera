package com.humanwares.camera.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.gc.android.market.api.MarketSession;
import com.gc.android.market.api.MarketSession.Callback;
import com.gc.android.market.api.model.Market;
import com.gc.android.market.api.model.Market.App;
import com.gc.android.market.api.model.Market.AppsRequest;
import com.gc.android.market.api.model.Market.AppsResponse;
import com.gc.android.market.api.model.Market.ResponseContext;
import com.humanwares.camera.CameraApp;
import com.humanwares.camera.R;
import com.humanwares.camera.utils.Utils;

public class ActivitySWInfo extends Activity implements OnClickListener {
	
	public static Intent getIntent(Context context) {
		Intent i = new Intent(context, ActivitySWInfo.class);
		return i;
	}

	private TextView textCurrent;
	private TextView textLatest;
	private PackageInfo packageInfo;
	private MarketSession session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sw_info);
		try {
			packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initView();
	}

	private void initView() {
		textCurrent = (TextView)findViewById(R.id.textCurrent);
		textLatest = (TextView)findViewById(R.id.textLatest);
		
		textCurrent.setText("v" + packageInfo.versionName);
		findViewById(R.id.btnUpdate).setOnClickListener(this);
		
//		new MarketVersionChecker().execute();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.btnUpdate:
			finish();
			Utils.goGoogleMarket(this);
			break;
		}
	}
	
	
	private class MarketVersionChecker extends AsyncTask<Void, Void, Void> {

		protected String marketVersion;

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				session = new MarketSession();
				session.login(CameraApp.email, CameraApp.passwd);
				session.getContext().setAndroidId(Secure.getString(getContentResolver(), Secure.ANDROID_ID));
				AppsRequest appsReq = AppsRequest.newBuilder()
						.setQuery(getPackageName())
						.setStartIndex(0)
						.setEntriesCount(10)
						.setWithExtendedInfo(true)
						.build();
				session.append(appsReq, new Callback<Market.AppsResponse>() {
					
					@Override
					public void onResult(ResponseContext arg0, AppsResponse arg1) {
						// TODO Auto-generated method stub
						for(int i = 0; i < arg1.getAppCount(); i++) {
							App app = arg1.getApp(i);
							if(app.getPackageName().equals(getPackageName())) {
								marketVersion = app.getVersion();
								publishProgress();
							}
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				session.flush();
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			textLatest.setText("v" + marketVersion);
			super.onProgressUpdate(values);
		}
	}
}
