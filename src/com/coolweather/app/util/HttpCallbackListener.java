package com.coolweather.app.util;
/**
 * ����:�����߳��е��ø��ӿڷ�������ȡ���ַ���Ϣ����.
 * @author Administrator
 *
 */
public interface HttpCallbackListener {
	public void onFinish(String response);
	public void onError(Exception e);
}
