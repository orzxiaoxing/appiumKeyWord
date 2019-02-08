package com.testing.Appium;

import java.net.URL;

import org.openqa.selenium.remote.DesiredCapabilities;

import com.testing.UI.AutoLogger;

import io.appium.java_client.android.AndroidDriver;

public class BrowserDriver {
	public AndroidDriver driver = null;

	// 设备名称、app的main Activity类、appium服务器ip端口、等待启动时间
	public BrowserDriver(String platformVersion,String deviceName,String appiumServerIP,String waitTime) {
		DesiredCapabilities capabilities = new DesiredCapabilities();
		//必要参数
		capabilities.setCapability("deviceName", deviceName);
		capabilities.setCapability("platformVersion", platformVersion);
		capabilities.setCapability("platformName", "Android");
		capabilities.setCapability("browserName", "Browser");
		//可选参数
		capabilities.setCapability("noSign", true);
		capabilities.setCapability("noReset", true);
		capabilities.setCapability("unicodeKeyboard", true);
		capabilities.setCapability("resetKeyboard", true);
		//电脑连接了多个设备的时候，指定设备。
		capabilities.setCapability("udid", deviceName);
		
		try {
			driver=new AndroidDriver(new URL(appiumServerIP), capabilities);
			int t=1000;
			t=Integer.parseInt(waitTime);
			Thread.sleep(t);
			AutoLogger.log.info("安卓浏览器正在启动中……");
		} catch (Exception e) {
			AutoLogger.log.error("安卓浏览器启动失败，请检查配置！");
		}
	}

	public AndroidDriver getdriver() {
		return this.driver;
	}
}
