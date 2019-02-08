package com.testing.Appium;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.testing.UI.AutoLogger;

import io.appium.java_client.TouchAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.touch.offset.PointOption;

public class KeywordOfApp {
	public AndroidDriver driver;

	public KeywordOfApp() {
		
	}

	//强制等待
	public void wait(String time) {
		int t = 0;
		try {
			t = Integer.parseInt(time);
			Thread.sleep(t);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 脚本执行CMD命令的函数
	public void runCmd(String str) {
		String cmd = "cmd /c start " + str;
		Runtime runtime = Runtime.getRuntime();
		try {
			AutoLogger.log.info("执行cmd命令:"+str);
			runtime.exec(cmd);
		} catch (Exception e) {
			AutoLogger.log.error("cmd命令执行失败");
			AutoLogger.log.error(e,e.fillInStackTrace());
		}
	}
	
	//通过cmd启动appium的服务
	public void StartAppium(String port, String time) {
		// 启动appium的服务端
		AutoLogger.log.info("启动appiumserver服务");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd+HH-mm-ss");
		// 当前时间的字符串
		String createdate = sdf.format(date);
		// 拼接文件名，形式为：工作目录路径+方法名+执行时间.png
		String appiumLogFile = "SCRshot/" + createdate + "AppiumLog.txt";
		String startAppiumCMD ="appium -a 127.0.0.1 -p " + port + " --log " + appiumLogFile +" --local-timezone";
		runCmd(startAppiumCMD);
		try {
			int t = 1000;
			t = Integer.parseInt(time);
			Thread.sleep(t);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// 启动被测APP
	public void runAPP(String platformVersion, String deviceName, String appPackage,String appActivity,String appiumServerIP,String time) {
			try {
				AutoLogger.log.info("启动待测App");
				AppDriver app = new AppDriver(platformVersion, deviceName, appPackage, appActivity, appiumServerIP, time);
				driver = app.getdriver();
			} catch (Exception e) {
				AutoLogger.log.error("启动待测App失败");
				AutoLogger.log.error(e, e.fillInStackTrace());
			}
	}

	public void runBrowser(String platformVersion,String deviceName,  String appiumServerIP,String waitTime) {
		try {
			AutoLogger.log.info("启动安卓浏览器");
			BrowserDriver browser = new BrowserDriver(platformVersion, deviceName, appiumServerIP, waitTime);
			driver = browser.getdriver();
		} catch (Exception e) {
			AutoLogger.log.error("启动安卓浏览器失败");
			AutoLogger.log.error(e, e.fillInStackTrace());
		}
	}
	
	public void visitH5(String url) {
		try {
			AutoLogger.log.info("安卓浏览器访问"+url);
			driver.get(url);
		} catch (Exception e) {
			AutoLogger.log.error(e, e.fillInStackTrace());
		}
	}
	
	public void input(String xpath, String text) {
		try {
			explicityWait(xpath);
			driver.findElement(By.xpath(xpath)).clear();
			driver.findElement(By.xpath(xpath)).sendKeys(text);
		} catch (Exception e) {
			AutoLogger.log.error(e, e.fillInStackTrace());
			saveScrShot("input");
		}
	}

	public void click(String xpath) {
		try {
			explicityWait(xpath);
			driver.findElement(By.xpath(xpath)).click();
		} catch (Exception e) {
			AutoLogger.log.error(e, e.fillInStackTrace());
			saveScrShot("click");
		}
	}

	// 调用adb滑动
	public void adbSwipe(int i, int j, int k, int l, int m) {
		try {
			this.runCmd("adb shell input swipe " + i + " " + j + " " + k + " " + l + " " + m);
		} catch (Exception e) {
			AutoLogger.log.error("通过adb执行滑动失败");
			AutoLogger.log.error(e, e.fillInStackTrace());
		}
	}

	// 调用adb模拟按键
	public void adbPressKey(String keycode) {
		try {
			int k = Integer.parseInt(keycode);
			String cmd = " adb shell input keyevent " + k;
			runCmd(cmd);
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			AutoLogger.log.error("通过adb执行按键事件失败");
			AutoLogger.log.error(e, e.fillInStackTrace());
		}
	}

	public void adbTap(String xAxis, String yAxis) {
		try {
			int x = Integer.parseInt(xAxis);
			int y = Integer.parseInt(yAxis);
			runCmd("adb shell input tap " + x + " " + y);
		} catch (Exception e) {
			AutoLogger.log.error("通过adb执行点击失败");
			AutoLogger.log.error(e, e.fillInStackTrace());
		}
	}

	public void quitApp() {
		try {
			driver.closeApp();
		} catch (Exception e) {
			AutoLogger.log.error("关闭app失败");
			AutoLogger.log.error(e, e.fillInStackTrace());
		}
	}

	public void killAppium() {
		try {
			runCmd("taskkill /F /IM node.exe");
		} catch (Exception e) {
			AutoLogger.log.error("关闭appiumserver服务失败");
			AutoLogger.log.error(e, e.fillInStackTrace());
		}
	}

	// 断言
	public void assertSame(String xpath, String paramRes) {
		try {
			explicityWait(xpath);
			String result = driver.findElement(By.xpath(xpath)).getText();
			System.out.println(result);
			if (result.equals(paramRes)) {
				AutoLogger.log.info("测试用例执行成功");
			} else {
				AutoLogger.log.info("测试用例执行失败");
			}
		} catch (Exception e) {
			AutoLogger.log.error("执行断言时报错");
			AutoLogger.log.error(e, e.fillInStackTrace());
		}
	}

	// 通过appium的方法进行滑屏
	public void appiumSwipe(String iniX, String iniY, String finX, String finY) {
		try {
			int x = Integer.parseInt(iniX);
			int y = Integer.parseInt(iniY);
			int x1 = Integer.parseInt(finX);
			int y1 = Integer.parseInt(finY);
			TouchAction action = new TouchAction(driver);
			PointOption pressPoint=PointOption.point(x, y);
			PointOption movePoint=PointOption.point(x1, y1);
			action.longPress(pressPoint).moveTo(movePoint).release().perform();
		} catch (Exception e) {
			AutoLogger.log.error("执行Appium滑动方法失败");
			AutoLogger.log.error(e, e.fillInStackTrace());
		}
	}

	// 使用appium的方法点击坐标
	public void appiumTap(String x, String y) {
		try {
			int xAxis = Integer.parseInt(x);
			int yAxis = Integer.parseInt(y);
			TouchAction action = new TouchAction(driver);
			PointOption pressPoint=PointOption.point(xAxis, yAxis);
			// action类分解动作，先长按，再移动到指定位置，再松开
			action.tap(pressPoint).release().perform();
		} catch (NumberFormatException e) {
			AutoLogger.log.error("执行Appium点击坐标方法失败");
			AutoLogger.log.error(e, e.fillInStackTrace());
		}
	}

	// 使用appium方法长按
	public void appiumHold(String x, String y, String time) {
		try {
			int xAxis = Integer.parseInt(x);
			int yAxis = Integer.parseInt(y);
			int t = Integer.parseInt(time);
			PointOption pressPoint=PointOption.point(xAxis, yAxis);
			Duration last = Duration.ofMillis(t);
			TouchAction action = new TouchAction(driver);
			action.longPress(pressPoint);
		} catch (NumberFormatException e) {
			AutoLogger.log.error("执行Appium滑动方法失败");
			AutoLogger.log.error(e, e.fillInStackTrace());
		}
	}
	
	/**
	 * 实现显式等待的方法，在每次定位元素时，先尝试找元素，给10秒钟的最长等待。
	 */
	public void explicityWait(String xpath) {
		try {
			WebDriverWait eWait = new WebDriverWait(driver, 10);
			eWait.until(new ExpectedCondition<WebElement>() {
				public WebElement apply(WebDriver d) {
					return d.findElement(By.xpath(xpath));
				}
			});
		} catch (Exception e) {
			AutoLogger.log.error(e,e.fillInStackTrace());
		}
	}
	
	/**
	 * 实现隐式等待的方法
	 */
	public void implicitlyWait() {
		try {
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		} catch (Exception e) {
			AutoLogger.log.error(e,e.fillInStackTrace());
		}
	}

	public void printContexts() {
		Set<String> contexts=driver.getContextHandles();
		for(String s:contexts) {
			System.out.println(s);
		}
	}

	public void switchContext(String contextName) {
		try {
			AutoLogger.log.info("切换到"+contextName+"context");
			driver.context(contextName);
		} catch (Exception e) {
			AutoLogger.log.error("切换context失败。");
		}
	}
	
	public void saveScrShot(String method) {
		// 获取当前的执行时间
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd+HH-mm-ss");
		// 当前时间的字符串
		String createdate = sdf.format(date);
		// 拼接文件名，形式为：工作目录路径+方法名+执行时间.png
		String scrName = "SCRshot/" + method + createdate + ".png";
		// 以当前文件名创建文件
		File scrShot = new File(scrName);
		// 将截图保存到临时文件
		File tmp = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(tmp, scrShot);
		} catch (IOException e) {
			AutoLogger.log.error(e,e.fillInStackTrace());
			AutoLogger.log.error("截图失败！");
		}
	}
}