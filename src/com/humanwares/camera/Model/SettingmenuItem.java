package com.humanwares.camera.Model;

public class SettingmenuItem {

	private int nResID;
	private String strMenuName;
	private String strDefault;
	private Object data;
	
	public int getnResID() {
		return nResID;
	}
	public void setnResID(int nResID) {
		this.nResID = nResID;
	}
	public String getStrMenuName() {
		return strMenuName;
	}
	public void setStrMenuName(String strMenuName) {
		this.strMenuName = strMenuName;
	}
	public String getStrDefault() {
		return strDefault;
	}
	public void setStrDefault(String strDefault) {
		this.strDefault = strDefault;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
}
