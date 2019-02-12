package com.crazykid.mmall.util;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import sun.net.ftp.FtpClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Getter
@Setter
@Slf4j
public class FTPUtil {
    private static String ftpIp = PropertiesUtil.getProperties("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperties("ftp.user");
    private static String ftpPass = PropertiesUtil.getProperties("ftp.pass");

    public FTPUtil(String ip, int port, String user, String pwd) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pwd = pwd;
    }

    public static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(ftpIp, 21, ftpUser, ftpPass);
        log.info("开始连接FTP服务器");
        boolean result = false;
        result = ftpUtil.uploadFile("img", fileList);
        log.info("FTP上传结束，上传结果：{}", result);
        return result;
    }

    private boolean uploadFile(String remotePath, List<File> fileList) throws IOException{
        boolean uploaded = true;
        FileInputStream fis = null;

        if (connectServer(this.getIp(), this.port, this.user, this.pwd)) {
            try {
                ftpClient.changeWorkingDirectory(remotePath); //切换远程Ftp目录
                ftpClient.setBufferSize(1024); //设置缓冲区大小
                ftpClient.setControlEncoding("UTF-8"); //设置编码
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE); //文件类型设置成二进制类型(防止某些乱码)
                ftpClient.enterLocalPassiveMode();//打开本地被动模式
                for(File fileItem : fileList) {
                    fis = new FileInputStream(fileItem);
                    ftpClient.storeFile(fileItem.getName(), fis); //存储文件 第一个参数是文件名
                }
            } catch (IOException e) {
                log.error("上传文件发生异常", e);
                uploaded = false;
            } finally {
                fis.close();
                ftpClient.disconnect();
            }
        }
        return uploaded;
    }

    private boolean connectServer(String ip, int port, String user, String pwd) {
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user, pwd);
        } catch (IOException e) {
            log.error("连接FTP服务器时发生错误", e);
        }
        return isSuccess;
    }

    private String ip;
    private int port;
    private String user;
    private String pwd;
    private FTPClient ftpClient;
}
