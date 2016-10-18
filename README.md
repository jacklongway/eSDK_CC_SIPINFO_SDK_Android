## eSDK\_CC\_SIPINFO\_SDK\_Android  ##
eSDK CC SIPINFO Android支持Andorid 4.0.3到6.0之间的版本（包含6.0）,开放TUP SIPINFO相关的业务接口。
在有华为CC环境的基础上，eSDK CC SIPINFO API接口可直接供第三方应用调用TUP 呼叫及收发SIPINFO消息的能力。 
eSDK CC将TUP的能力以接口形式开放，ISV只需要调用相应接口就能完成如匿名呼叫、收发SIPINFO消息等功能，ISV在此基础上可以进行灵活的界面定制，融入自己的特色业务。
	
	
## 版本更新 ##
eSDK CC SIPINFO最新版本v1.5.50
	
## 开发环境 ##
	
- 操作系统： Windows7
- 开发工具：eclipse+Android SDK+ADT
- JDK：1.6及以上版本
	
## 文件指引 ##
	
- src文件夹：eSDK CC SIPINFO Android依赖库
- sample文件夹：eSDK CC SIPINFO样例工程
- doc：eSDK CC SIPINFO SDK的接口参考、开发指南
	
## 入门指导 ##
	
- 下载工程：下载提供的工程文件
- 引入库工程：打开eclipse，菜单栏“File >Import”，选择Android>Existing Android Code Into Workspace
点击Next,选择下载的src/eSDK_CC_API确定完成。
- 库工程设为依赖库：右键工程Properties,选择Android,将Is Library勾选，Apply,OK。
- 引入Demo工程添加依赖库：菜单栏“File >Import”，选择Android>Existing Android Code Into Workspace
点击Next,选择下载的sample/SipInfoDemo确定完成，右键工程Properties,选择Android,点击Add..，选择
SipInfoApi,点击Apply,OK。到此DEMO工程可编译运行。
- 若集成到自己工程，操作只需将Demo替换为自己工程。详细的开发指南请参考doc中的开发指南（库工程的src即为）。
	

	
## 获取帮助 ##
	
在开发过程中，您有任何问题均可以至[DevCenter](https://devcenter.huawei.com)中提单跟踪。也可以在[华为开发者社区](http://bbs.csdn.net/forums/hwucdeveloper)中查找或提问。另外，华为技术支持热线电话：400-822-9999（转二次开发）
