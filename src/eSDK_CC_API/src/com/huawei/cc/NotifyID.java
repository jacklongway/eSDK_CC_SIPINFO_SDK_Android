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

/**
 * 广播通知ID
 */
public class NotifyID
{
    
    /**
     * VOIP通话开始
     */
    public static final String TERMINAL_TALKING_EVENT = "voip_talking_event";

    /**
     * VIOP呼叫结束
     */
    public static final String TERMINAL_CALLING_RELEASE_EVENT = "voip_calling_release_event";
    
    
    /**
     * 接收SipInfo消息通知
     */
    public static final String RECEIVE_SIP_INFO_EVENT = "receive_sip_info_event";
    
    /**
     * 终端挂断电话通知
     */
    public static final String TERMINAL_RELEASE_CALLING__EVENT = "release call";
}
