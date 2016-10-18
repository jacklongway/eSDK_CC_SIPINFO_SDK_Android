/**Copyright 2015 Huawei Technologies Co., Ltd. All rights reserved.
eSDK is licensed under the Apache License, Version 2.0 ^(the "License"^);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.huawei.cc;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import tupsdk.TupCall;
import android.app.Application;
import android.os.Environment;

import com.huawei.cc.call.CallManager;
import com.huawei.cc.common.AccountInfo;
import com.huawei.cc.common.Constants;
import com.huawei.cc.common.Constants.CALLMODE;
import com.huawei.cc.service.CCApp;
import com.huawei.cc.utils.LogUtils;
import com.huawei.cc.utils.Tools;
import com.huawei.esdk.log4Android.Log4Android;

public class MobileCC {
	private String TAG = "MobileCC";
	
	private final static String format = "yyyy-MM-dd HH:mm:ss SSS";
	
	private final static String product = "eSDK-CC-API-Android-Java";

	private static MobileCC instance;
	public synchronized static MobileCC getInstance() {
		if (null == instance) {
			instance = new MobileCC();
		}
		return instance;
	}

	private MobileCC() {
	}

	/**
	 * 初始化SDK服务
	 * @param app 该工程的Application,用于启动服务.
	 * @return
	 */
	public boolean initSDK(Application app) {
		String reqTime =  new SimpleDateFormat(format).format(new Date());
		CCApp.getInstances().initApp(app);
		CallManager callManager = CallManager.getInstance();
		
		//加载esdk日志模块的so文件
		System.loadLibrary("Log4Android");
		//设置Context
		Log4Android.setContext(CCApp.getInstances().getApplication());
		//日志保存路径
		String logPath = Environment.getExternalStorageDirectory().toString()
				+ File.separator + Constants.CC_LOG_FILE + File.separator;
		//日志初始化
		int[] logLevel = {0,0,0};
		Log4Android.getInstance().logInit(product, "", logLevel, logPath);
		int[] sizes = {10240, 10240, 10240};
        int[] nums = {10, 10, 10};
        String serverPath = "esdk-log.huawei.com:9086";
        String logUploadUrl = "/esdkom/log/upload";

        Log4Android.getInstance().setLogProperty(product, sizes, nums, serverPath, logUploadUrl);
		
		
		
		Log4Android.getInstance().setCallBackMethod();
		//172.22.9.38:9086
		Log4Android.getInstance().setSendLogStrategy(0, 2, "esdk-log.huawei.com:9086");
		Log4Android.getInstance().initMobileLog(product);
		
		Log4Android.getInstance().logRunInfo(product, "initSDK enter.");
		
		if(callManager.tupInit()==0){
			String RespTime =  new SimpleDateFormat(format).format(new Date());
			Log4Android.getInstance().logInterfaceInfo(product, "1", "Native"
					, "initSDK", "", "", "", reqTime, RespTime, "0", "app="+app);
			LogUtils.d(TAG, "initSDK | app = " + app+",tupInit result: success!");
			return true;
		}else{
			String RespTime =  new SimpleDateFormat(format).format(new Date());
			Log4Android.getInstance().logInterfaceError(product, "1", "Native"
					, "initSDK", "", "", "", reqTime, RespTime, "-1", "app="+app);
			LogUtils.d(TAG, "initSDK | app = " + app+",tupInit result: fail!");
			return false;
		}

	}

	/**
	 * 停止SDK
	 */
	public void stopSDK() {
		LogUtils.d(TAG, "stopSDK");
		String reqTime =  new SimpleDateFormat(format).format(new Date());
		CallManager callManager = CallManager.getInstance();
		if (callManager != null) {
			callManager.tupUninit();
			String RespTime =  new SimpleDateFormat(format).format(new Date());
			Log4Android.getInstance().logInterfaceInfo(product, "1", "Native"
					, "stopSDK", "", "", "", reqTime, RespTime, "0", "");
		}
		Log4Android.getInstance().logUnInit(product);
//		Log4Android.getInstance().logRunInfo(product, "stopSDK end.");
	}
	
	
	public int makeAnonymousCall(String ip,int port,String accessCode){
		String reqTime =  new SimpleDateFormat(format).format(new Date());
		Log4Android.getInstance().logRunInfo(product, "makeAnonymousCall enter.");
		if(!Tools.isNetworkConnected()){
			String RespTime =  new SimpleDateFormat(format).format(new Date());
			Log4Android.getInstance().logInterfaceError(product, "1", "Native"
					, "makeAnonymousCall", "", "", "", reqTime, RespTime, "-3", "ip=" 
			+ ip + ",port=" + port+",accessCode="+accessCode);
			return -3;
		}
		LogUtils.d(TAG, "hostAddr | " + "ip = " + ip + ", port = " + port);
		if(!Tools.checkIP(ip) || !Tools.checkPort(port)){
			String RespTime =  new SimpleDateFormat(format).format(new Date());
			Log4Android.getInstance().logInterfaceError(product, "1", "Native"
					, "makeAnonymousCall", "", "", "", reqTime, RespTime, "-4", "ip=" 
			+ ip + ",port=" + port+",accessCode="+accessCode);
			return -4;
		}
		AccountInfo.getInstance().setAnonymous(true);
		CallManager.getInstance().getVoipConfig().resetData("", "", ip, port+"");	
		CallManager.getInstance().tupConfig();
		if(AccountInfo.getInstance().getAnonymousNum()!=""){
			int callId = CallManager.getInstance().makeAnonymousCall(accessCode);
			if(callId!=0){
				String RespTime =  new SimpleDateFormat(format).format(new Date());
				Log4Android.getInstance().logInterfaceInfo(product, "1", "Native"
						, "makeAnonymousCall", "", "", "", reqTime, RespTime, "0", "ip=" 
				+ ip + ",port=" + port+",accessCode="+accessCode);
			}else{
				String RespTime =  new SimpleDateFormat(format).format(new Date());
				Log4Android.getInstance().logInterfaceError(product, "1", "Native"
						, "makeAnonymousCall", "", "", "", reqTime, RespTime, "-2", "ip=" 
				+ ip + ",port=" + port+",accessCode="+accessCode);
			}
			return callId;
		}else{
			Log4Android.getInstance().logRunError(product, "makeAnonymousCall -1,anonymousNum is null.");
			String RespTime =  new SimpleDateFormat(format).format(new Date());
			Log4Android.getInstance().logInterfaceError(product, "1", "Native"
					, "makeAnonymousCall", "", "", "", reqTime, RespTime, "-1", "ip=" 
			+ ip + ",port=" + port+",accessCode="+accessCode);
			return -1;
		}
		
	}
	
	/**
	 * 挂断通话
	 */
	public void releaseCall(){
		String reqTime =  new SimpleDateFormat(format).format(new Date());
		CallManager.getInstance().releaseCall();
		CallManager.getInstance().setTupCall(null);
		AccountInfo.getInstance().setCallMode(CALLMODE.CALL_CLOSED);
		String RespTime =  new SimpleDateFormat(format).format(new Date());
		Log4Android.getInstance().logInterfaceInfo(product, "1", "Native"
				, "releaseCall", "", "", "", reqTime, RespTime, "0", "");
	}
	
	/**
	 * 发送SipInfo消息
	 * @param mediaType  info主类型
	 * @param subMediaType info子类型
	 * @param infoBody info Body体
	 * @return
	 */
	public int sendDialogInfo(String mediaType, String subMediaType, String infoBody){
		String reqTime =  new SimpleDateFormat(format).format(new Date());
		TupCall call = CallManager.getInstance().getTupCall();
		if(call!=null){
			int ret = call.sendDialogInfo(mediaType, subMediaType, infoBody);
			LogUtils.d(TAG, "sendSipInfo result:"+ret);
			if(ret==0){
				String RespTime =  new SimpleDateFormat(format).format(new Date());
				Log4Android.getInstance().logInterfaceInfo(product, "1", "Native"
						, "sendDialogInfo", "", "", "", reqTime, RespTime,"0", "mediaType="
				+mediaType+",subMediaType="+subMediaType+",infoBody="+infoBody);
			}else{
				String RespTime =  new SimpleDateFormat(format).format(new Date());
				Log4Android.getInstance().logInterfaceError(product, "1", "Native"
						, "sendDialogInfo", "", "", "", reqTime, RespTime,"-2", "mediaType="
				+mediaType+",subMediaType="+subMediaType+",infoBody="+infoBody);
			}
			return ret;
		}else{
			String RespTime =  new SimpleDateFormat(format).format(new Date());
			Log4Android.getInstance().logInterfaceError(product, "1", "Native"
					, "sendDialogInfo", "", "", "", reqTime, RespTime,"-1", "mediaType="
			+mediaType+",subMediaType="+subMediaType+",infoBody="+infoBody);
			return -1;
		}
	}
	
	/**
	 * 设置音频编解码
	 * @param audioCode
	 */
	public void setAudioCode(String audioCode){
		CallManager.getInstance().setAudioCode(audioCode);
	}
	
	/**
	 * 获取音频编解码
	 * @return
	 */
	public String getAudioCode(){
		return CallManager.getInstance().getAudioCode();
	}
	/**
	 * 设置匿名卡号
	 * @param card
	 */
	public void setAnonymousCard(String card){
		CallManager.getInstance().setAnonymousCard(card);
	}
	/**
	 * 查询匿名卡号
	 * @return
	 */
	public String getAnonymousCard(){
		return CallManager.getInstance().getAnonymousCard();
	}
	
	
}
