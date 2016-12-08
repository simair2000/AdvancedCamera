package com.humanwares.camera.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.humanwares.camera.CameraApp;
import com.humanwares.camera.R;

public class ActivitySendMail extends Activity implements OnClickListener {

	public static Intent getIntent(Context context) {
		Intent i = new Intent(context, ActivitySendMail.class);
		return i;
	}

	private EditText editMessage;
	private EditText editMailAddr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_mail);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		findViewById(R.id.btnBack).setOnClickListener(this);
		findViewById(R.id.btnSend).setOnClickListener(this);
		editMessage = (EditText) findViewById(R.id.editMessage);
		editMailAddr = (EditText) findViewById(R.id.editMailAddr);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btnBack:
			finish();
			break;
		case R.id.btnSend:
			validAndSendEmail(CameraApp.email);
			break;
		}
	}

	private void showDlg(String message, final boolean finish) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss(); // 닫기
				if(finish) {
					ActivitySendMail.this.finish();
				}
			}
		});
		alert.setMessage(message);
		alert.show();
	}

	private void validAndSendEmail(String mailTo) {
		String message = editMessage.getText().toString();
		String addr = editMailAddr.getText().toString();
		if (TextUtils.isEmpty(message) || TextUtils.isEmpty(addr)) {
			showDlg("내용을 입력해 주세요", false);
			return;
		}

		String subject = "문의 사항";

		Intent email = new Intent(Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_EMAIL, new String[] { mailTo });
		email.putExtra(Intent.EXTRA_SUBJECT, subject);
		email.putExtra(Intent.EXTRA_TEXT, message + "\n\n답변받을 메일 주소 : " + addr);

		// need this to prompts email client only
		email.setType("message/rfc822");

		startActivityForResult(Intent.createChooser(email, "메일 보내기.."), 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		showDlg("문의하기가 완료되었습니다.", true);
	}
}
