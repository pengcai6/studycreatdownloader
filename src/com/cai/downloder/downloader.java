package com.cai.downloder;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 *
 * {@code @Author}
 * {@code @Data} 2024/4/24
 */
public class downloader {

    private Integer treadNum=10;
    public void Downloader(String source, String targetDir) {
        InputStream is = null;
        OutputStream os = null;
        try {
//            https://manongbiji.oss-cn-beijing.aliyuncs.com/imooc/pexels/pexels-photo-11572548.jpeg
            String fileName = source.substring(source.lastIndexOf("/") + 1);//读取下载连接中自带的文件名做为文件名
            File targetFile = new File(targetDir + "/" + fileName);//创建一个新文件；
            if (!targetFile.exists()) {
                targetFile.createNewFile();
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
            System.out.println("[INFO]图片下载完毕:" + source + "\n\t ->" + targetFile.getPath() + "(" + Math.floor((double) targetFile.length() / 1024) + "kb)");
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

    /**
     * 从指定文件读取下载地址，批量下载网络资源
     *
     * @param targetDir   下载文件的存储目录
     * @param downloadTxt download.txt完整路径
     */
    public void multiDownloadFromFile(String targetDir, String downloadTxt) {
        File dir = new File(targetDir);
        if (!dir.exists()) {
            dir.mkdirs();//创建路径
            System.out.println("[INFO]发现下载目录[" + dir.getPath() + "]不存在，已自动创建");
        }
        List<String> resources = new ArrayList<>();

        BufferedReader reader = null;//缓冲区的Reader对象
        ExecutorService threadPool=null;//在外侧创建线程池方便关闭
        try {
            reader = new BufferedReader(new FileReader(downloadTxt));//关闭最外侧也会把套娃内部实例化的对象进行关闭
            String line;
            while ((line = reader.readLine()) != null) {
                resources.add(line);
            }
            threadPool= Executors.newFixedThreadPool(this.treadNum);
            downloader that=this;//给其赋予别名
            for(String res:resources)
            {   threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    that.Downloader(res, targetDir);
                }
            });

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if(threadPool!=null)
                {
                    threadPool.shutdown();//等待所有线程处理完毕再关闭
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }

    /**
     * 开始多线程下载
     *
     * @param propDir config.properties所在目录
     */

    public void start(String propDir)//传入配置文件路径
    {

        File propFile = new File(propDir + "\\config.properties");
        Properties properties = new Properties();//不需要传入构造方法参数
        Reader reader = null;//放在try块外方便关闭
        try {
            reader = new FileReader(propFile);
            properties.load(reader);//使用该方法加载配置文件
            String treadNum = properties.getProperty("thread-num");//根据对应属性获取对应的值；
            this.treadNum= Integer.parseInt(treadNum);
            String targetDir = properties.getProperty("target-dir");
//            System.out.println(treadNum);
//            System.out.println(targetDir);
            this.multiDownloadFromFile(targetDir,propDir+"\\download.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }


    public static void main(String[] args) {
        downloader a = new downloader();
        a.start("E:\\Users\\26068\\Desktop\\mydownloder\\src");
//        a.Downloader("https://manongbiji.oss-cn-beijing.aliyuncs.com/imooc/pexels/pexels-photo-11572548.jpeg"
//                , "E:\\download");

    }
}