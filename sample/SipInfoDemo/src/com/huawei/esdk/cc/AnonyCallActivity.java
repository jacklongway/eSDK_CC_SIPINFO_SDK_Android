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
package com.huawei.esdk.cc;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.R;
import com.huawei.cc.MobileCC;
import com.huawei.cc.NotifyID;
import com.huawei.cc.NotifyMsg;
import com.huawei.cc.NotifyMsg.NOTIFY;
import com.huawei.cc.utils.LogUtils;
import com.huawei.cc.utils.Tools;

public class AnonyCallActivity extends Activity{
	private String TAG = "AnonyCallActivity";
	private EditText accessNum;
	private Button anonyCallBtn,sipInfoBtn,releaseCallBtn;
	private TextView logTv;
	private IntentFilter filter;
	private EditText ipStr,portStr,sipBody;
	private String ipAddr = "";
	private int port = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.anony_call_activity);
		filter = new IntentFilter();
		filter.addAction(NotifyID.TERMINAL_TALKING_EVENT);
		filter.addAction(NotifyID.TERMINAL_CALLING_RELEASE_EVENT);
		filter.addAction(NotifyID.TERMINAL_RELEASE_CALLING__EVENT);
		filter.addAction(NotifyID.RECEIVE_SIP_INFO_EVENT);
		registerReceiver(receiver, filter);
		initView();
	}


	private void initView() {
		ipStr = (EditText) findViewById(R.id.uapip);
		ipStr.setText("172.22.8.69"); 
		portStr = (EditText) findViewById(R.id.uapport);
		portStr.setText("5060");
		accessNum = (EditText) findViewById(R.id.calleeTextField);
		accessNum.setText("2001"); 
		sipBody = (EditText) findViewById(R.id.sipBody);
		sipBody.setText("test message");
		anonyCallBtn = (Button) findViewById(R.id.makeCallBtn);
		sipInfoBtn = (Button) findViewById(R.id.sipInfoBtn);
		releaseCallBtn = (Button) findViewById(R.id.releaseCallBtn);
		logTv = (TextView) findViewById(R.id.logtext);
		logTv.setMovementMethod(ScrollingMovementMethod.getInstance());
		
		anonyCallBtn.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				if(!checkIpPort()){
					Toast.makeText(AnonyCallActivity.this, getString(R.string.checkServerConfig),
		                    Toast.LENGTH_SHORT).show();
					return;
				}
				String calledNum = accessNum.getText().toString().trim();
				if (calledNum.length() > 0)
				{
//					MobileCC.getInstance().setAnonymousCard("AnonymousCard");
//					MobileCC.getInstance().setAudioCode("18");
					
					int callFlag = MobileCC.getInstance().makeAnonymousCall(ipAddr,port,calledNum);

					if (callFlag == -1) 
					{
						addLog("make call to " + calledNum + " error!");
					}else if(callFlag == -3){
						addLog("The current network is not available!");
					}else if(callFlag == -4){
						addLog("ip or port error!");
					}else
					{
						addLog("make call to " + calledNum + "!");
					}
				}else{
					Toast.makeText(AnonyCallActivity.this, getString(R.string.accessCodeTip),
		                    Toast.LENGTH_SHORT).show();
				}
			}

			
		});
		
		sipInfoBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String msg = sipBody.getText().toString().trim();
				if(Tools.isEmpty(msg)){
					addLog("sipInfo body is null!");
					return;
				}
				int ret = MobileCC.getInstance().sendDialogInfo("text", "xml", msg);
				if (ret == -1) 
				{
					addLog("call is null!");
				}
				else
				{
					addLog("sendDialogInfo result:"+ret);
				}
			}
		});
		
		releaseCallBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MobileCC.getInstance().releaseCall();
			}
		});
		
	}
	
	private boolean checkIpPort() {
		ipAddr = ipStr.getText().toString();
	    String portStr2 = portStr.getText().toString();
	    if (Tools.isEmpty(ipAddr) || Tools.isEmpty(portStr2))
        {
            return false;
        }
	    try{
	    	 port = Integer.parseInt(portStr2);
	    }catch(Exception e){
	    	LogUtils.d("parseInt exception:"+e.toString());
	    	return false;
	    }
	   
	    if(!Tools.checkIP(ipAddr)||!Tools.checkPort(port)){
	    	  return false;
	    }
	    return true;
	}
	
	private void addLog(String logText)
	{
		logTv.append(logText);
		logTv.append("\n");
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			NotifyMsg notifyMsg = (NotifyMsg) intent
					.getSerializableExtra(NOTIFY.KEY_NAME);
			LogUtils.d(TAG, notifyMsg.getAction());
			if(NotifyID.TERMINAL_TALKING_EVENT.equals(action)){
				addLog("call connected!");
			}else if(NotifyID.TERMINAL_CALLING_RELEASE_EVENT.equals(action)){
				addLog("call ended!");
			}else if(NotifyID.RECEIVE_SIP_INFO_EVENT.equals(action)){
				addLog(notifyMsg.getMsg());
			}else if(NotifyID.TERMINAL_RELEASE_CALLING__EVENT.equals(action)){
				addLog(notifyMsg.getMsg());
			}
		}
	};

	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		LogUtils.i(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtils.i(TAG, "onPause");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		LogUtils.i(TAG, "onRestart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		LogUtils.i(TAG, "onResume");
	}

	@Override
	protected void onStart() {
		super.onStart();
		LogUtils.i(TAG, "onStart");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		MobileCC.getInstance().releaseCall();
	}
	
	
}
