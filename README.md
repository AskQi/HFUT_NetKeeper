# hfut_neetkeeper
这是一个简单的用来开机自动认证并保持合工大校园网连接的软件。理论上支持所有drcom的设备，需要安装jdk或者jre。
## 使用流程：
1. 非windows10用户安装从https://curl.haxx.se/download.html 下载curl.exe并复制到C:\Windows\System32目录下
2. 编辑“loginDrcom.bat”,修改“account”和“password”分别为你的账号和密码。非合工大用户再修改下验证地址。
3. 修改winsw.xml中的参数。第一个为断网时执行的文件名，第二个为网络恢复后接收网络恢复的邮箱地址
4. 以管理员身份运行“install.bat”
## 卸载方法：
以管理员身份运行“uninstall.bat”
## 说明：
自动登录用的 lhlybly 的 hfut-autologin 方案([点击查看]!https://github.com/AskQi/hfut-autologin)，侵删。
## 联系我：
如果遇到问题可以联系齐大：974830507@qq.com
