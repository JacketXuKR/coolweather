package com.coolweather.app.util;
/**
 * 功能:在子线程中调用给接口方法将读取的字符信息返回.
 * @author Administrator
 *
 */
public interface HttpCallbackListener {
	public void onFinish(String response);
	public void onError(Exception e);
}
