package com.humanwares.camera.utils;

import java.io.Serializable;

import com.humanwares.camera.CameraApp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PrefUtil {

	public enum PrefType {
		PREF_GLOBAL,
		;
	}
	
	public static class Keys {
		public static final String IS_SHOWN_GUIDE = "IS_SHOWN_GUIDE";
	}

	private static PrefUtil instance = null;
	private static SharedPreferences pref;
	private static Editor editor;

	public static PrefUtil getInstance(PrefType type) {
		if (instance == null) {
			instance = new PrefUtil(type, CameraApp.getApp());
		}
		return instance;
	}

	public PrefUtil(PrefType type, Context context) {
		pref = context.getSharedPreferences(CameraApp.getApp().getPackageName() + "." + type.toString(), Context.MODE_PRIVATE);
		if (pref != null) {
			editor = pref.edit();
		}
	}
	
	public Serializable getPreference(Serializable key, Class<?> type, Serializable def) {
		if (type == Integer.class) {
			return getInt((String) key, (Integer) def);
		} else if (type == String.class) {
			return getString((String) key, (String) def);
		} else if (type == Boolean.class) {
			return getBoolean((String) key, (Boolean) def);
		} else if (type == Float.class) {
			return getFloat((String) key, (Float) def);
		} else if (type == Long.class) {
			return getLong((String) key, (Long) def);
		} else {
			return null;
		}
	}

	public void putPreference(Class<?> type, Serializable key, Serializable value) {
		if (type == Integer.class) {
			putValue((String) key, (Integer) value);
		} else if (type == String.class) {
			putValue((String) key, (String) value);
		} else if (type == Boolean.class) {
			putValue((String) key, (Boolean) value);
		} else if (type == Float.class) {
			putValue((String) key, (Float) value);
		} else if (type == Long.class) {
			putValue((String) key, (Long) value);
		}
	}

	public void putValue(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	public void putValue(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}

	public void putValue(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	public void putValue(String key, float value) {
		editor.putFloat(key, value);
		editor.commit();
	}

	public void putValue(String key, long value) {
		editor.putLong(key, value);
		editor.commit();
	}

	public Boolean getBoolean(String key, boolean defValue) {
		return pref.getBoolean(key, defValue);
	}

	public Float getFloat(String key, float defValue) {
		return pref.getFloat(key, defValue);
	}

	public Integer getInt(String key, int defValue) {
		return pref.getInt(key, defValue);
	}

	public Long getLong(String key, long defValue) {
		return pref.getLong(key, defValue);
	}

	public String getString(String key, String defValue) {
		if (pref != null) {
			return pref.getString(key, defValue);
		} else {
			return defValue;
		}
	}
}
