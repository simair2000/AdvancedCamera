package com.humanwares.camera;

public class CameraException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int CODE_NONE = 100;
	public static final int CODE_NOT_READY = CODE_NONE + 1;
	
	public enum Errors {
		ERROR_NONE(CODE_NONE, "에러 없음"),
		ERROR_NOT_READY(CODE_NOT_READY, "카메라가 준비중입니다.");
		;
		
		public int code;
		public String message;
		
		private Errors(int code, String message) {
			// TODO Auto-generated constructor stub
			this.code = code;
			this.message = message;
		}
	}

	public Errors error;
	
	public CameraException(Errors e) {
		// TODO Auto-generated constructor stub
		this.error = e;
	}
}
