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
package com.huawei.cc.call.data;

import com.huawei.cc.utils.StringUtils;


public class VoiceQuality
{

	public enum VoiceQualityLevel
	{
		POOL, NORMAL_1, NORMAL_2, NORMAL_3, EXCELLENT
	}

	/**
	 * 语音质量 
	 */
	private VoiceQualityLevel level = VoiceQualityLevel.POOL;

	public VoiceQualityLevel getLevel()
	{
		return level;
	}

	public void setLevel(VoiceQualityLevel level)
	{
		this.level = level;
	}

	public VoiceQualityLevel convertFrom(String param)
	{
		int ret = StringUtils.stringToInt(param);

		switch (ret)
		{
		case 1:
			level = VoiceQualityLevel.POOL;
			break;
		case 2:
			level = VoiceQualityLevel.NORMAL_1;
			break;
		case 3:
			level = VoiceQualityLevel.NORMAL_2;
			break;
		case 4:
			level = VoiceQualityLevel.NORMAL_3;
			break;
		case 5:
			level = VoiceQualityLevel.EXCELLENT;
			break;
		default:
			level = VoiceQualityLevel.EXCELLENT;
		}
		return level;
	}

	@Override
	public String toString()
	{
		return "level = " + level;
	}

}
