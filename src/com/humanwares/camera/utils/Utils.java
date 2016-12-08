package com.humanwares.camera.utils;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;

public class Utils {

	public static void goGoogleMarket(Context context) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse("market://search?q=" + context.getPackageName()));
		context.startActivity(i);
	}
	
	public static byte[] bitmapToByteArray(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		return byteArray;
	}
	
	public static boolean isSDCardExist() {
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
}
