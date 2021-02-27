/*
 * Author: jianqing
 * Date: Nov 24, 2020
 * Description: This document is created for
 */
package com.classchatroom.model;

import cn.hutool.setting.Setting;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;

/**
 * This is responsible for uploading files on OSS.
 * @author jianqing
 */
public class OSSAccessor
{


    public static void goOss()
    {
        Setting setting = new Setting("oss.setting");
        
        OSS client = new OSSClientBuilder().build(setting.get("endpoint"), setting.get("id"), setting.get("secret"));
        String content = "Hello OSS";
// <yourObjectName>表示上传文件到OSS时需要指定包含文件后缀在内的完整路径，例如abc/efg/123.jpg。
        PutObjectRequest putObjectRequest = new PutObjectRequest("xeduo", "logs/tomcat/miao.txt", new ByteArrayInputStream(content.getBytes()));
        client.putObject(putObjectRequest);
        client.shutdown();
    }

    public static void logError(Throwable ex)
    {
        Setting setting = new Setting("oss.setting");
        OSS client = new OSSClientBuilder().build(setting.get("endpoint"), setting.get("id"), setting.get("secret"));

        String content = ex.getMessage() + "\n"
                + ex.toString() + "\n";
        StackTraceElement[] stackTraceElement = ex.getStackTrace();
        for (StackTraceElement stackTraceElement1 : stackTraceElement)
        {
            content += "      "+stackTraceElement1+"\n";
        }
        PutObjectRequest putObjectRequest = new PutObjectRequest("xeduo", "logs/tomcat/classchat/err_" + stackTraceElement.length + "_" + LocalDateTime.now() + "_classchat.txt", new ByteArrayInputStream(content.getBytes()));
        client.putObject(putObjectRequest);
        client.shutdown();
        //boolean d =errorLog.delete();
        //System.out.println(d);

    }

    public static void main(String[] args)
    {
        try
        {
            Integer.parseInt("sb");
        } catch (NumberFormatException e)
        {
            logError(e);
            //e.printStackTrace();
        }

    }
}
