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

import java.io.Serializable;

public class NotifyMsg implements Serializable
{
    private static final long serialVersionUID = 5852695241803490766L;
    
    /**
   	 *广播通知携带消息的Intent的key-name
     */
    public static interface NOTIFY
    {
        /** Intent key-name **/
        public static final String KEY_NAME = "notifyMsg";
    }

    private String action;
    private String recode;
    private String msg;

    public NotifyMsg()
    {
    }

    public NotifyMsg(String action)
    {
        this.action = action;
    }
    
    /**
     * 获取广播通知的action
     * <br>
     * 
     * @return 返回广播通知的action
     */
    public String getAction()
    {
        return action;
    }

    /**
     * 设置广播通知的action
     */
    public void setAction(String action)
    {
        this.action = action;
    }

    public String getRecode()
    {
        return recode;
    }

    public void setRecode(String recode)
    {
        this.recode = recode;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

}
