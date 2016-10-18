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
package com.huawei.cc.common;

import java.util.ArrayList;
import java.util.List;

import com.huawei.cc.common.Constants.CALLMODE;

/**
 * 
 * 用于存储当前账号的相关信息
 * 
 */
public final class AccountInfo
{
    private static AccountInfo ins;
    
	/**
     * 默认的呼叫id，必须是0. 如果非0，表示当前呼叫成功过
     */
    public static final int DEFAULT_CALLID = 0;

    private int callMode = CALLMODE.CALL_NOTSTART;

    private int currentCallID;


    /** SIP终端号码 **/
    private String phoneNo = "";

    /** SIP终端密码 **/
    private String password = "";

    /** 媒体网关服务器IP地址 UAP IP **/
    private String sipServerIp = "";

    /** 媒体网关服务器端口号 UAP Port **/
    private int sipServerPort = 5060;
    
    /** 媒体网关服务器是否加密 */
    private boolean sipIsEncoded = false;

    private boolean isAnonymous = false;
    
    /** 匿名呼叫时的被叫号码 **/
    private String calledNumber;
    
    /** 预留字段，呼叫信息 **/
    private String callInfo = "" ;
    
    /**匿名呼叫设置的号码**/
    private String anonymousNum = "";
    
    public String getAnonymousNum() {
		return anonymousNum;
	}

	public void setAnonymousNum(String anonymousNum) {
		this.anonymousNum = anonymousNum;
	}

	private List<String[]> ipItemList = new ArrayList<String[]>();
    
    private AccountInfo()
    {

    }

    public synchronized static AccountInfo getInstance()
    {
        if (ins == null)
        {
            ins = new AccountInfo();
        }
        return ins;
    }

    public void clear()
    {
        releaseIns();
    }
    
    private synchronized static void releaseIns()
    {
        ins = null;
    }

    public int getCallMode()
    {
        return callMode;
    }

    public void setCallMode(int callMode)
    {
        this.callMode = callMode;
    }


    public String getPhoneNo()
    {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo)
    {
        this.phoneNo = phoneNo;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getSipServerIp()
    {
        return sipServerIp;
    }

    public void setSipServerIp(String sipServerIp)
    {
        this.sipServerIp = sipServerIp;
    }


    public int getSipServerPort() {
		return sipServerPort;
	}

	public void setSipServerPort(int sipServerPort) {
		this.sipServerPort = sipServerPort;
	}

	public boolean isSipIsEncoded() {
		return sipIsEncoded;
	}

	public void setSipIsEncoded(boolean sipIsEncoded) {
		this.sipIsEncoded = sipIsEncoded;
	}


    public int getCurrentCallID()
    {
        return currentCallID;
    }

    public void setCurrentCallID(int currentCallID)
    {
        this.currentCallID = currentCallID;
    }


    public boolean isAnonymous()
    {
        return isAnonymous;
    }
    
    public void setAnonymous(boolean isAnonymous)
    {
        this.isAnonymous = isAnonymous;
    }
    
    public String getCalledNumber()
    {
        return calledNumber;
    }
    
    public void setCalledNumber(String calledNumber)
    {
        this.calledNumber = calledNumber;
    }
    
    
    public String getallInfo()
    {
        return callInfo;
    }
    
    public void setCallInfo(String callInfo)
    {
        this.callInfo = callInfo;
    }
    
    public void addIpItem(String[] IpItem)
    {
        ipItemList.add(IpItem);
    }
    
    public List<String[]> getIpItemList()
    {
        return ipItemList;
    }
    
}

