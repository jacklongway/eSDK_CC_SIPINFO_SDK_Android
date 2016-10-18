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

import com.huawei.cc.service.CCApp;


public class Constants
{

    public static final String VERSION = "V100R005C30B105";
    
    public static final String CC_LOG = "CCLOG";

    public static final String CC_LOG_FILE = "CCLOG";

    public static final String CC_LOG_FILE_NAME = "CC.log";

    public static final String CC_CONF_LOG_FILE_NAME = "CC_CONF_TEMP.log";

    public static final String USERTYPE_TERMINAL = "terminal"; // 机具终端

    public static final String USERTYPE_MOBILE = "mobile"; // 移动终端

    public static final String HTTP = "http://";

    public static final String HTTPS = "https://";

    public static final String RESPONSE_ERROR = "response error";

    public static final String RESPONSE_ERROR_CODE = "-1";

    public static final String NOTIFY_RETCODE = "retcode";

    public static final String NOTIFY_MSG = "msg";

    public static final String RETCODE_SUCCESS = "0000";

    public static final String CHARSET_GBK = "GBK";

    public static final String CHARSET_UTF_8 = "UTF-8";

    public static final String NO_EVENT = "no_event";

    public static final String CBC_KEY = "_Wc1689Abc*";

    public static final int TIMEOUT = 10 * 1000;

    public static final String ANNORESPATH = CCApp.getInstances()
            .getApplication().getFilesDir()
            + "/AnnoRes";

    /**
     * 匿名呼叫类型
     */
    public static interface MEDIA_TYPE
    {
        public static final String WEBPHONE = "MEDIA_TYPE_WEBPHONE";
    }


    public static interface CALLMODE
    {
        /** 初始状态 **/
        public static final int CALL_NOTSTART = 0;

        /** 通话中 **/
        public static final int CALL_TALKING = 1;

        /** 通话已结束 **/
        public static final int CALL_CLOSED = 2;

    }

    public static interface CC_STATE
    {
        /** 主动入会 **/
        public static final String AUDIOTALKING = "AUDIOTALKING";

        /** 已接通 **/
        public static final String TALKING = "TALKING";

        /** 已释放 **/
        public static final String RELEALSE = "RELEASE";
    }


	}
