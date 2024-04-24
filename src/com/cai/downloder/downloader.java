package com.cai.downloder;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * &#064;Description
 * @Author
 * @Date 2024/4/24
 */
public class downloader {


    public void Downloader(String source, String targetDir) {
        InputStream is = null;
        OutputStream os = null;
        try {
//            https://manongbiji.oss-cn-beijing.aliyuncs.com/imooc/pexels/pexels-photo-11572548.jpeg
            String fileName = source.substring(source.lastIndexOf("/") + 1);//读取下载连接中自带的文件名做为文件名
            File targetFile = new File(targetDir + "/" + fileName);//创建一个新文件；
            if (!targetFile.exists()) {
               boolean successDown = targetFile.createNewFile();
                System.out.println("文件下载是否成功"+ successDown);
            }
            URL url = new URL(source);
            URLConnection connection = url.openConnection();//打开网络连接，会返回一个对象
            is = connection.getInputStream();//得到网络的输入流，也就是图片的数据流
            os = new FileOutputStream(targetFile);
            byte[] bs = new byte[1024];//最多读1k字节
            int len;
            while ((len = is.read(bs)) != -1) {
                //需要一边读，一边写
                os.write(bs, 0, len);//bs为数据，0为从头开始，len表示有效的部分

            }
            System.out.println("[INFO]图片下载完毕:"+source+"\n\t ->"+targetFile.getPath()+"("+Math.floor((double) targetFile.length() /1024)+"kb)");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (os != null)
                    os.close();
                if (is != null)
                    is.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public static void main(String[] args) {
            downloader a=new downloader();
            a.Downloader("https://manongbiji.oss-cn-beijing.aliyuncs.com/imooc/pexels/pexels-photo-11572548.jpeg"
            ,"E:\\download");

    }


}
