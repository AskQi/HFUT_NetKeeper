package netkeeper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	public static String RUNNING_PATH;
	public static String LOGIN_FILE_NAME = "loginDrcom.bat";
	public static String LOGIN_FILE_PATH;
	public static boolean isConnectedLastTime = false;
	public static String RECEIVE_MESSAGE_EMAIL_ADDRESS = null;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		timerTask(30000);
		RUNNING_PATH = getRunningPath();
		if (args.length > 0) {
			LOGIN_FILE_NAME = args[0];
			if (args.length > 1) {
				RECEIVE_MESSAGE_EMAIL_ADDRESS = args[1];
			}
		}
		LOGIN_FILE_PATH = RUNNING_PATH + LOGIN_FILE_NAME;
		String outputStr = "断网时将执行脚本：" + LOGIN_FILE_PATH;
		if (isEmail(RECEIVE_MESSAGE_EMAIL_ADDRESS)) {
			outputStr += "恢复网络时将自动发邮件到：" + RECEIVE_MESSAGE_EMAIL_ADDRESS;
		}
		System.out.println(outputStr);
	}

	private static String getRunningPath() {
		java.net.URL url = Main.class.getProtectionDomain().getCodeSource().getLocation();
		String filePath = null;
		try {
			filePath = java.net.URLDecoder.decode(url.getPath(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (filePath.endsWith(".jar"))
			filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);

		java.io.File file = new java.io.File(filePath);
		filePath = file.getAbsolutePath() + "\\";
		return filePath;
	}

	static void timerTask(long delayTime) {
		Runnable runnable = new Runnable() {
			public void run() {
				while (true) {
					// ------- code for task to run
					isConnect();
					// ------- ends here
					try {
						Thread.sleep(delayTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}

	public static void isConnect() {
		Runtime runtime = Runtime.getRuntime();
		try {
			Process process = runtime.exec("ping " + "baidu.com");
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			is.close();
			isr.close();
			br.close();

			if (null != sb && !sb.toString().equals("")) {
				String logString = "";
				if (sb.toString().indexOf("TTL") > 0) {
					// 网络畅通
					logString = getNowTime() + ":网络正常 ";
					if (!isConnectedLastTime) {
						// 上次网络连接失败，这次成功了。
						isConnectedLastTime = true;
						logString = logString + "，上次网络连接失败，将发送重连成功Email";
						//
						if (isEmail(RECEIVE_MESSAGE_EMAIL_ADDRESS)) {
							String emailAddress = RECEIVE_MESSAGE_EMAIL_ADDRESS;
							// String emailTheme ="Computer Network Restore";
							// String emailContext = "Your Computer Network have been restored by NetKeeper
							// at "+ getNowTime();
							String emailTheme = "电脑网络恢复";
							String emailContext = "您的电脑于 " + getNowTime() + " 恢复网络连接";

							emailTheme = URLEncoder.encode(emailTheme, "UTF-8");
							emailContext = URLEncoder.encode(emailContext, "UTF-8");
							sendConnectSuccessEmail(emailAddress, emailTheme, emailContext);

						}
					}

					System.out.println(logString);
				} else {
					// 网络不畅通
					logString = getNowTime() + ":网络断开 ";
					isConnectedLastTime = false;
					System.out.println(logString);
					reConnect();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void sendConnectSuccessEmail(String emailAddress, String emailTheme, String emailContext) {
		// http://wol.sharepeople.cn/api/forwardEmail.php?email=974830507@qq.com&message=12456&title=电脑开机
		// 免费提供一个邮件发送接口，希望大家不要滥用。
		String url = "http://wol.sharepeople.cn/api/forwardEmail.php";
		String param = "email=" + emailAddress + "&title=" + emailTheme + "&message=" + emailContext + "&_=";
		String s = HttpRequest.sendPost(url, param);
		System.out.println(s);
	}

	private static String getNowTime() {
		Date day = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(day);
	}

	public static void reConnect() {
		System.out.println(getNowTime() + ":断线重连中");
		runBat(LOGIN_FILE_PATH);
	}

	public static void runBat(String batPath) {
		String cmd = "cmd /c start " + batPath;
		try {
			Process ps = Runtime.getRuntime().exec(cmd);
			ps.waitFor();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static boolean isEmail(String string) {
		if (string == null)
			return false;
		String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		Pattern p;
		Matcher m;
		p = Pattern.compile(regEx1);
		m = p.matcher(string);
		if (m.matches())
			return true;
		else
			return false;
	}

	public static void exeCmd(String commandStr) {
		BufferedReader br = null;
		try {
			Process p = Runtime.getRuntime().exec(commandStr);
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			System.out.println(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
