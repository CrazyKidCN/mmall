package com.crazykid.mmall.service.impl;

import com.crazykid.mmall.service.IFileService;
import com.crazykid.mmall.util.FTPUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service("iFileService")
public class FileServiceImpl implements IFileService {
    public String upload(MultipartFile file, String path) {
        //获取原始文件名
        String fileName = file.getOriginalFilename();
        //获取文件扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);

        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;

        log.info("开始上传文件,上传文件的文件名：{}，上传的路径是：{},新文件名：{}", fileName, path, uploadFileName);

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            //创建文件夹前 赋予可写权限 （tomcat可能不具有写权限）
            fileDir.setWritable(true);
            //mkdir：仅当前级别   mkdirs：多层目录 一次搞定
            fileDir.mkdirs();
        }
        File targetFile = new File(path, uploadFileName);
        try {
            //将上传的文件转移到path去
            file.transferTo(targetFile);

            //将targetFile上传到FTP服务器上
            FTPUtil.uploadFile(Lists.newArrayList(targetFile)); //guava提供的list工具

            //上传完之后，删除upload下面的文件
            targetFile.delete();

            return targetFile.getName();
        } catch (IOException e) {
            log.error("文件上传异常", e);
            return null;
        }
    }
}
