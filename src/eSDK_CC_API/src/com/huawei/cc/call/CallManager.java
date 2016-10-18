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
package com.huawei.cc.call;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import tupsdk.TupAudioQuality;
import tupsdk.TupAudioStatistic;
import tupsdk.TupCall;
import tupsdk.TupCallCfgAudioVideo;
import tupsdk.TupCallCfgSIP;
import tupsdk.TupCallLocalQos;
import tupsdk.TupCallManager;
import tupsdk.TupCallNotify;
import tupsdk.TupCallParam;
import tupsdk.TupCallQos;
import tupsdk.TupComFunc;
import tupsdk.TupMsgWaitInfo;
import tupsdk.TupRegisterResult;
import tupsdk.TupVideoQuality;
import tupsdk.TupVideoStatistic;
import android.os.Environment;

import com.huawei.cc.NotifyID;
import com.huawei.cc.NotifyMsg;
import com.huawei.cc.call.data.VOIPConfigParamsData;
import com.huawei.cc.call.data.VoiceQuality;
import com.huawei.cc.common.AccountInfo;
import com.huawei.cc.common.Constants.CALLMODE;
import com.huawei.cc.service.CCApp;
import com.huawei.cc.utils.LogUtils;
import com.huawei.cc.utils.StringUtils;
import com.huawei.cc.utils.Tools;

public final class CallManager implements TupCallNotify {


	private boolean initFlag = false;

	private static CallManager instance;

	TupCallManager tupManager;

	public static final String TAG = "[TUPC30]";

	private static final int audioMin = 10500;

	private static final int audioMax = 10519;

	private static final int videoMin = 10580;

	private static final int videoMax = 10599;

	private static final int FORCE_OPEN = 1;
	private TupCallCfgAudioVideo tpCllCfgAdVd = null;

	/** 硬解码默认码率 */
	public static final int MAX_DATARATE_HARDCODEC = 512;

	/** 软解码默认码率 */
	public static final int MAX_DATARATE_SOFTCODEC = 256;

	/**
	 * 在登录时协商一次后，后面不应该再去修改，否则会入不了会 硬编码能力下最大支持512，软编码能力下最大支持256
	 */
	private int maxBw = MAX_DATARATE_SOFTCODEC;

	/**
	 * 在登录时协商一次后，后面不应该再去修改，否则会入不了会 可选，解码器处理的图像格式。
	 * 1：SQCIF格式；2：QCIF格式；3：CIF格式；4：4CIF格式；5：16CIF格式；
	 * 6：QQVGA格式；7：QVGA格式；8：VGA格式；9：720P。
	 */
	private int decodeFrameSize = 3;

	private int minDataRate = 1;

	/**
	 * 在登录时协商一次后，后面不应该再去修改，否则会入不了会 硬编码能力下最大支持512，软编码能力下最大支持256
	 */
	private int maxDataRate = MAX_DATARATE_SOFTCODEC;

	/**
	 * 缓存Call集合， 可以扩展到多路通话，目前只有一路
	 */
	private Map<Integer, CallSession> calls = null;


	private List<String> unInterruptSessionIds = new ArrayList<String>();
	
	private String audioCode = "0,8,18";
	
	private String anonymousCard = "AnonymousCard";
	

	public String getAnonymousCard() {
		return anonymousCard;
	}

	public void setAnonymousCard(String anonymousCard) {
		this.anonymousCard = anonymousCard;
	}

	public String getAudioCode() {
		return audioCode;
	}

	public void setAudioCode(String audioCode) {
		this.audioCode = audioCode;
	}

	/**
	 * VOIP 注册状态
	 */
	public enum State {
		UNREGISTE, // 未注册
		REGISTING, // 注册过程中 包括注册失败 刷新注册
		REGISTED, // 注册成功
	}

	public void saveUninterruptIds(String sessionId) {
		unInterruptSessionIds.add(sessionId);

	}

	public synchronized static CallManager getInstance() {
		if (instance == null) {
			instance = new CallManager();
		}
		return instance;
	}

	/**
	 * VOIP 参数对象
	 */
	private VOIPConfigParamsData voipConfig = new VOIPConfigParamsData();

	private SIPRegister register = new SIPRegister();

	private CallSession currentCallSession = null;
	
	private TupCall tupCall = null;
	

	public TupCall getTupCall() {
		return tupCall;
	}

	public void setTupCall(TupCall tupCall) {
		this.tupCall = tupCall;
	}

	private CallManager() {
		calls = new ConcurrentHashMap<Integer, CallSession>();

		tupManager = new TupCallManager(this, CCApp.getInstances()
				.getApplication());
		loadSo();

		// codecParams = new VideoCaps.CodecParams();
		tpCllCfgAdVd = new TupCallCfgAudioVideo();
	}

	public SIPRegister getRegister() {
		return register;
	}

	public static byte[] tupRsaPublicEncrypt(String strSrcData,
			String strKeyPath) {
		TupComFunc comFunc = new TupComFunc();
		return comFunc.rsaPublicEncrypt(strSrcData, strKeyPath);
	}

	public VOIPConfigParamsData getVoipConfig() {
		return voipConfig;
	}

	/**
	 * Function: 发送注册消息
	 */
	public void register(boolean delay) {
		registerVoip();
	}

	/**
	 * 发起SIP注册 (UI 发起注册，需要自动重新config， 本地IP地址可能切换)
	 */
	public void registerVoip() {
		register.registerVoip();
	}

	/**
	 * Function: 注销VOIP 对外接口
	 * 
	 * @author luotianjia 00186254/huawei
	 */
	public void unRegister() {
		unRegistVoip();
	}

	/**
	 * 注销SIP注册
	 */
	public void unRegistVoip() {
		register.unRegisterVOIP();
	}

	/**
	 * 获取当前SIP注册状态
	 * 
	 * @return
	 */
	public State getStatus() {
		if (tupManager.getRegState() == TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_REGISTERED) {
			return State.REGISTED;
		}

		return State.UNREGISTE;
	}
	/**
	 * 获取音量？
	 * @return
	 */
	public int getAudioVolume(){
		return tupManager.mediaGetSpeakVolume();
	}

	/**
	 * Function: 加载静态的 SO 库
	 * 
	 * @author luotianjia 00186254/huawei
	 * @return void
	 */
	private void loadSo() {

		// 加载公共API
		// System.loadLibrary("comFunc");
		// 加载SIP信令相关的so
		tupManager.loadLib();

		// 加载云盘 HTTP 相关的so
		System.loadLibrary("tup_tupcore");
		// 配置 HME 的媒体接口
		tupManager.setAndroidObjects();
		// 配置TUP 日志

		String logFile = Environment.getExternalStorageDirectory().toString()
				+ File.separator + "CCLOG";

		File dirFile = new File(logFile);
		if (!(dirFile.exists()) && !(dirFile.isDirectory())) {
			if (dirFile.mkdir()) {
				LogUtils.d("CALL", "mkdir " + dirFile.getPath());
			}
		}

		tupManager.logStart(3, 5 * 1000, 1, logFile);
	}

	@Override
	public void onCallComing(TupCall call) {
		LogUtils.d("CallManager onCallComing");
		if (tupManager.getRegState() != TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_REGISTERED) {
			call.endCall();
			return;
		}
		CallSession callSession = new CallSession(call);
		callSession.setCallManager(this);
		calls.put(callSession.getTupCall().getCallId(), callSession);
	}

	@Override
	public void onRegisterResult(TupRegisterResult result) {
		LogUtils.d("onRegisterResult");
	}

	@Override
	public void onSessionModified(TupCall call) {
		LogUtils.d("CallManager onSessionModified");
	}

	@Override
	public void onCallStartResult(TupCall call) {
		LogUtils.d("CallManager onCallStartResult");
	}

	@Override
	public void onCallGoing(TupCall call) {
		LogUtils.d("CallManager onCallGoing");
	}

	@Override
	public void onCallRingBack(TupCall call) {
		LogUtils.d("CallManager onCallRingBack");
	}

	@Override
	public void onCallConnected(TupCall call) {
		LogUtils.d("CallManager onCallConnected");
		tupCall = call;//将当前tupCall记录下来
		setTupCall(call);
		AccountInfo.getInstance().setCallMode(CALLMODE.CALL_TALKING);
		AccountInfo.getInstance().setCurrentCallID(call.getCallId());

		NotifyMsg notifyMsg = new NotifyMsg(NotifyID.TERMINAL_TALKING_EVENT);
		notifyMsg.setRecode(String.valueOf(call.getCallId()));
		notifyMsg.setMsg(call.getRemoteAddr());
		CCApp.getInstances().sendBroadcast(notifyMsg);
		
//		try {
//			Thread.currentThread().sleep(6000);
//			if(call!=null){
//				int res = call.sendDialogInfo("text", "xml", "test");
//				LogUtils.d("sendDialogInfo result:"+res);
//			}
//			
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
	}

	@Override
	public void onCallEnded(TupCall call) {
		LogUtils.d("CallManager onCallEnded");
		AccountInfo.getInstance().setCallMode(CALLMODE.CALL_CLOSED);
		AccountInfo.getInstance().setCurrentCallID(AccountInfo.DEFAULT_CALLID);

		AccountInfo.getInstance().clear();
		tupCall = null;
		currentCallSession = null;
		NotifyMsg notifyMsg = new NotifyMsg(
				NotifyID.TERMINAL_CALLING_RELEASE_EVENT);
		CCApp.getInstances().sendBroadcast(notifyMsg);
	}

	@Override
	public void onCallDestroy(TupCall call) {
		LogUtils.d("CallManager onCallDestroy");
	}

	@Override
	public void onCallRTPCreated(TupCall call) {
		LogUtils.d("CallManager onCallRTPCreated");
	}

	@Override
	public void onCallAddVideo(TupCall call) {
		LogUtils.d("CallManager onCallAddVideo");
	}

	@Override
	public void onCallDelVideo(TupCall call) {
		LogUtils.d("CallManager onCallDelVideo");
	}

	@Override
	public void onCallViedoResult(TupCall call) {
		LogUtils.d("CallManager onCallViedoResult");
	}

	@Override
	public void onCallRefreshView(TupCall call) {
		LogUtils.d("CallManager onCallRefreshView");
	}

	@Override
	public void onCallHoldSuccess(TupCall call) {
		LogUtils.d("CallManager onCallHoldSuccess");
	}

	@Override
	public void onCallHoldFailed(TupCall call) {
		LogUtils.d("CallManager onCallHoldFailed");
	}

	@Override
	public void onCallUnHoldSuccess(TupCall call) {
		LogUtils.d("CallManager onCallUnHoldSuccess");
	}

	@Override
	public void onCallUnHoldFailed(TupCall call) {
		LogUtils.d("CallManager onCallUnHoldFailed");
	}

	@Override
	public void onCallBldTransferSuccess(TupCall call) {
		LogUtils.d("CallManager onCallBldTransferSuccess");
	}

	@Override
	public void onCallBldTransferRecvSucRsp(TupCall call) {
		LogUtils.d("CallManager onCallBldTransferRecvSucRsp");
	}

	@Override
	public void onCallBldTransferFailed(TupCall call) {
		LogUtils.d("CallManager onCallBldTransferFailed");
	}

	@Override
	public void onMobileRouteChange(TupCall call) {
		LogUtils.d("CallManager onMobileRouteChange");
	}

	@Override
	public void onAudioEndFile(int handler) {
		LogUtils.d("CallManager onAudioEndFile");
	}

	@Override
	public void onNetQualityChange(TupAudioQuality audioQuality) {
		LogUtils.d("CallManager onNetQualityChange");
		VoiceQuality level = new VoiceQuality();
		level.convertFrom(String.valueOf(audioQuality.getAudioNetLevel()));
	}

	@Override
	public void onStatisticNetinfo(TupAudioStatistic audioStatistic) {
//		LogUtils.d("CallManager onStatisticNetinfo, lost:"+audioStatistic.getAudioLost()+" ,delay:"
//					+audioStatistic.getAudioDelay()+" ,jitter:"+audioStatistic.getAudioJitter());
	}

	@Override
	public void onStatisticMos(int callId, int mos) {
		LogUtils.d("CallManager onStatisticMos");
		float value = mos / 1000f;
		LogUtils.i(TAG, "onNotifyMos |sessionId:" + callId + "mos:" + value);
	}

	@Override
	public void onVideoOperation(TupCall call) {
		LogUtils.d("CallManager onVideoOperation");
	}

	@Override
	public void onVideoStatisticNetinfo(TupVideoStatistic videoStatistic) {
		LogUtils.d("CallManager onVideoStatisticNetinfo");
	}

	@Override
	public void onVideoQuality(TupVideoQuality videoQuality) {
		LogUtils.d("CallManager onVideoQuality");
	}

	@Override
	public void onVideoFramesizeChange(TupCall call) {
		LogUtils.d("CallManager onVideoFramesizeChange");
	}

	@Override
	public void onSessionCodec(TupCall call) {
		LogUtils.d("CallManager onSessionCodec");
		CallSession callSession = null;

		if (call.getCallId() != 0) {
			callSession = calls.get(call.getCallId());
		}

		if (callSession != null) {
			if (callSession.isVoiceMail()) {
				return;
			}
		}
	}

	@Override
	public void onSipaccountWmi(List<TupMsgWaitInfo> tupWaitMsgInfos) {
		LogUtils.d("CallManager onSipaccountWmi");
	}

	@Override
	public void onImsForwardResult(List<String> tempHistoryNums) {
		LogUtils.d("CallManager onImsForwardResult");
		if (tempHistoryNums != null && tempHistoryNums.size() > 0) {
			String historyNumber = tempHistoryNums
					.get(tempHistoryNums.size() - 1);
		}
	}

	@Override
	public void onCallUpateRemoteinfo(TupCall call) {
		LogUtils.d("CallManager onCallUpateRemoteinfo");
	}

	@Override
	public void onSetIptServiceSuc(int serviceCallType) {
		LogUtils.d("CallManager onSetIptServiceSuc");
	}

	@Override
	public void onSetIptServiceFal(int serviceCallType) {
		LogUtils.d("CallManager onSetIptServiceFal");
	}

	@Override
	public void onVoicemailSubSuc() {
		LogUtils.d("CallManager onVoicemailSubSuc");
	}

	@Override
	public void onVoicemailSubFal() {
		LogUtils.d("CallManager onVoicemailSubFal");
	}

	/**
	 * 播放语音留言
	 * 
	 * @param shortNumber
	 *            号码
	 * @param domain
	 *            域名
	 * @return CallSession
	 */
	public CallSession playVoiceMail(String shortNumber, String domain) {

		CallSession callSession = makeCall(shortNumber, domain, false);
		if (callSession != null) {
			callSession.setVoiceMail(true);
		}
		return callSession;
	}

	/**
	 * 发起呼叫
	 * 
	 * @param number
	 *            呼叫号码
	 * @param isVideo
	 *            是否是视频呼叫 , 普通呼叫传 false
	 * @return CallSession
	 */
	public CallSession makeCall(String number, String domain, boolean isVideo) {
		TupCall call = tupMakeCall(number, isVideo);
		if (call != null) {
			CallSession callSession = new CallSession(this);
			callSession.setTupCall(call);
			calls.put(callSession.getTupCall().getCallId(), callSession);

			currentCallSession = callSession;

			return callSession;
		}
		return null;
	}

	/**
	 * 发起语音呼叫
	 * 
	 * @param toNumber
	 * @Param isVideo 是否带视频
	 * @return
	 */
	private TupCall tupMakeCall(String toNumber, boolean isVideo) {
		if (isVideo) {
			return tupManager.makeVideoCall(toNumber);
		}
		return tupManager.makeCall(toNumber);
	}


	/**
	 * 设置摄像头参数
	 * 
	 * @param index
	 */
	public void setVideoIndex(int index) {
		int reg = tupManager.mediaSetVideoIndex(index);
		LogUtils.d("CallManager setVideoIndex result:" + reg);
	}


	/**
	 * 设置音频路由
	 * 
	 * @param rote
	 */
	public void setAudioRoute(int rote) {
		tupManager.setMobileAudioRoute(rote);
	}

	/**
	 * 获取当前路由
	 * 
	 * @return
	 */
	public int getAudioRoute() {
		int rote = tupManager.getMobileAudioRoute();
		return rote;
	}

	/**
	 * 开始录音
	 * 
	 * @param path
	 */
	public void startRecord(String path) {
		tupManager.mediaStartRecord(0, path, 0);
	}

	/**
	 * 结束录音
	 */
	public void stopRecord() {
		tupManager.mediaStopRecord(0);
	}

	/**
	 * 获取mic的音量
	 * 
	 * @return
	 */
	public int getMircoVol() {
		int reg = tupManager.mediaGetMicLevel();
		return reg;
	}

	/**
	 * 开始播放
	 */
	public int startPlay(String path, int loop) {
		if (StringUtils.isStringEmpty(path)) {
			return -1;
		}
		if (path.toLowerCase(Locale.ENGLISH).endsWith("pcm")) {
			tupManager
					.setAudioPlayfileAdditioninfo(TupCallParam.CALL_E_FILE_FORMAT.CALL_FILE_FORMAT_PCM);
		} else if (path.toLowerCase(Locale.ENGLISH).endsWith("amr")) {
			tupManager
					.setAudioPlayfileAdditioninfo(TupCallParam.CALL_E_FILE_FORMAT.CALL_FILE_FORMAT_AMR);
		} else {
			tupManager
					.setAudioPlayfileAdditioninfo(TupCallParam.CALL_E_FILE_FORMAT.CALL_FILE_FORMAT_WAV);
		}
		int ret = tupManager.mediaStartplay(loop, path);
		return ret;
	}

	/**
	 * 停止播放
	 */
	public int stopPlay(int handler) {
		int ret = tupManager.mediaStopplay(handler);
		return ret;
	}

	/**
	 * TUP 全局配置
	 */
	public void tupConfig() {
		configMedia();
		configSip();

	}

	private void configMedia() {
		tpCllCfgAdVd.setAudioPortRange(audioMin, audioMax);
		tpCllCfgAdVd.setVideoPortRange(videoMin, videoMax);
		// audioCode ， 区分 3G 和WIFI
		tpCllCfgAdVd.setAudioCodec(audioCode); //getVoipConfig().getAudioCode()
		// aec
		tpCllCfgAdVd.setAudioAec(1);
		// Dscp
		tpCllCfgAdVd.setDscpAudio(getVoipConfig().getAudioDSCP());
		tpCllCfgAdVd.setDscpVideo(getVoipConfig().getVideoDSCP());
		// net level
		tpCllCfgAdVd.setAudioNetatelevel(getVoipConfig().getNetate() == 1);
		// opus 采样率
		tpCllCfgAdVd.setAudioClockrate(getVoipConfig().getOpusSamplingFreq());
		tpCllCfgAdVd.setForceIdrInfo(FORCE_OPEN);
		tpCllCfgAdVd.setVideoCaptureRotation(/* caps.getCameraRotation() */0);
		tpCllCfgAdVd.setVideoDisplayType(0);
		tpCllCfgAdVd.setVideoCoderQuality(/* caps.getQuality() */15);
		tpCllCfgAdVd.setVideoKeyframeinterval(/* caps.getKeyInterval() */10);
		tpCllCfgAdVd
				.setAudioDtmfMode(TupCallParam.CALL_E_DTMF_MODE.CALL_E_DTMF_MODE_CONST2833);

		// 以下参数只配置一次，否则会导致融合会议无法入会
		VOIPConfigParamsData data = getVoipConfig();
		if (data.isHardCodec()) {
			// 硬编码能力下最大支持512，软编码能力下最大支持256
			maxBw = MAX_DATARATE_HARDCODEC;
			maxDataRate = MAX_DATARATE_HARDCODEC;
			// 硬编码能力下最大支持4CIF，软编码能力下最大支持CIF
			// decodeFrameSize = UCResource.VideoFrameSize._4CIF;
			decodeFrameSize = 4;
		}
		tupManager.setCfgAudioAndVideo(tpCllCfgAdVd);
		// 先只配置默认值
		tupManager.setMboileVideoOrient(0, 1, 1, 0, 0, 0);
	}

	private void configSip() {

		TupCallCfgSIP tupCallCfgSIP = new TupCallCfgSIP();

		// ip port
		tupCallCfgSIP.setServerRegPrimary(getVoipConfig().getServerIp(),
				StringUtils.stringToInt(getVoipConfig().getServerPort()));
		tupCallCfgSIP.setServerProxyPrimary(getVoipConfig().getServerIp(),
				StringUtils.stringToInt(getVoipConfig().getServerPort()));
		LogUtils.d("tupcallcfg serverIp:"+getVoipConfig().getServerIp()+" ,serverPort:"+getVoipConfig().getServerPort());
		
		// localip
		String svnIp = StringUtils.getIpAddress();
		tupCallCfgSIP.setNetAddress(svnIp);
		tupCallCfgSIP.setSipPort(5060);
		// 刷新注册的时间
		tupCallCfgSIP.setSipRegistTimeout(getVoipConfig().getRegExpires());
		// 刷新订阅的时间
		tupCallCfgSIP.setSipSubscribeTimeout(getVoipConfig()
				.getSessionExpires());
		// 会话
		tupCallCfgSIP.setSipSessionTimerEnable(true);
		// 会话超时
		tupCallCfgSIP.setSipSessionTime(90);
		LogUtils.d("setSipSessionTime : 90s");
		
		//订阅sipInfo消息
		tupCallCfgSIP.setContentType(0, "text", "xml");

		// 设置 DSCP
		tupCallCfgSIP.setDscpEnable(true);
		// 设置 tup 再注册的时间间隔， 注册失败后 间隔再注册
		tupCallCfgSIP.setSipReregisterTimeout(10);
		if (AccountInfo.getInstance().isAnonymous()) {
//			String calledNum = "AnonymousCard";
			String localIpAddress = Tools.getLocalIpAddress();
//			String port = "5060";
			String anonymousNum = anonymousCard + "@" + localIpAddress + ":" + tupCallCfgSIP.getSipPort();
			LogUtils.d("anonymousNum = " + anonymousNum);
			tupCallCfgSIP.setAnonymousNum(anonymousNum);
			tupManager.setCfgSIP(tupCallCfgSIP);
			AccountInfo.getInstance().setAnonymousNum(anonymousNum);

		} else {
			tupManager.setCfgSIP(tupCallCfgSIP);
		}

	}

	public int tupInit() {
		int initRet = 0;
		if (!initFlag) {
			LogUtils.d(TAG, "call_Init enter");
			initRet = tupManager.callInit();
			tupManager.registerReceiver();
			if(initRet==0){
				initFlag = true;
				LogUtils.d(TAG, "call_Init end");
			}
		}
		return initRet;

	}

	/**
	 * 去初始化 TUP组件
	 * 
	 * @l00186254
	 */
	public void tupUninit() {
	}

	public class SIPRegister {

		public SIPRegister() {
		}

		/**
		 * Function: VOIP注册
		 */
		void registerVoip() {

			LogUtils.i("tupConfig-------------");
			tupConfig();

			LogUtils.i("callRegister------------");

			tupManager.callRegister(getVoipConfig().getVoipNumber(),
					getVoipConfig().getVoipNumber(), getVoipConfig()
							.getVoipPassword());
			LogUtils.i("registerVoip end------------");
		}

		/**
		 * Function: VOIP注销
		 */
		void unRegisterVOIP() {
			if (tupManager.getRegState() != 0) {
				tupManager
						.setRegState(TupCallParam.CALL_E_REG_STATE.CALL_E_REG_STATE_DEREGISTERING);
				tupManager.callDeregister();
			}
		}
	}

	public void releaseCall() {
		NotifyMsg notifyMsg = new NotifyMsg(NotifyID.TERMINAL_RELEASE_CALLING__EVENT);
		if (currentCallSession != null) {
			notifyMsg.setMsg("release call");
			currentCallSession.hangUp(false);
			currentCallSession = null;
		}else{
			notifyMsg.setMsg("current call is null!");
		}
		CCApp.getInstances().sendBroadcast(notifyMsg);
	}

	public int makeAnonymousCall(String number) {

		int callId = tupManager.startAnonmousCall(number);
		if (0 != callId) {
			TupCall tupCall = new TupCall(callId, 0);
			tupCall.setCaller(true);
			tupCall.setNormalCall(true);
			tupCall.setToNumber(number);
			CallSession callSession = new CallSession(this);
			callSession.setTupCall(tupCall);
			calls.put(callId, callSession);
			currentCallSession = callSession;
		}
		return callId;
	}



	@Override
	public void onCallDialoginfo(int callId, String mediaType, String subMediaType, String body) {
		String newBody = body;
//		try {
//			newBody = new String(body.getBytes("utf-8"), "gbk");
//			newBody = new String(newBody.getBytes("ISO-8859-1"), "ascii");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
	
//			byte[] bodyContent = body.getBytes();
//			//newBody = new String(bodyContent, "UTF-8");
//			//发送sipinfo消息内容
//			for(int i = 0; i< bodyContent.length ;i++){
//				
//				System.err.println("" + i + ":" + Integer.toHexString(bodyContent[i]));
//			}
//			
//			
			
		LogUtils.d("onCallDialoginfo callId:"+callId+",mediaType:"+mediaType+",subMediaType:"+subMediaType
				+",body:"+newBody);
		NotifyMsg notifyMsg = new NotifyMsg(NotifyID.RECEIVE_SIP_INFO_EVENT);
		notifyMsg.setMsg("sipInfo message:mediaType="+mediaType+",subMediaType:"+subMediaType
				+",body:"+newBody);
		CCApp.getInstances().sendBroadcast(notifyMsg);
	}

	@Override
	public void onNotifyLocalQosinfo(TupCallLocalQos arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNotifyQosinfo(TupCallQos arg0) {
		// TODO Auto-generated method stub

	}

	public boolean mute(int type, boolean isMute) {
		if (currentCallSession != null) {
			return currentCallSession.mute(type, isMute);
		}
		return false;
	}


}
