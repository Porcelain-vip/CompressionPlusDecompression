package CompressionPlusDecompression;

/**
 * Created by Porcelain.vip on 2017/5/30.
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class CompressionPlusDecompression {

    private static String path;

    public static void main(String[] args) {

        System.out.println("Please enter your source file path");
        Scanner scanner = new Scanner(System.in);
        path = scanner.nextLine();
        try {
            //如果不存在目标压缩文件，程序会新创建一个
            if (new File(path).isDirectory()) {
                try (ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(
                        new FileOutputStream(new File(path + ".zip"))))) {
                    zipOutputStream.setComment("This is a test program programed by Porcelain IOT");
                    compression(path, zipOutputStream);
                }
                //zip文件的输出流一定要在这个地方关闭而不能在最后关闭，否则会出现java.util.zip.ZipException zip file is empty
                //当一个流对象不再使用时应当立马关闭,否则会出现一些问题
                System.out.println("Please enter your zip file path");
                String zippedPath = scanner.nextLine();
                deCompression(zippedPath);
            } else {
                singleFileCompression(path);
                System.out.println("Please enter your zip file path");
                String zippedPath = scanner.nextLine();
                singleFileDecompression(zippedPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //scanner流一定要关闭
            scanner.close();
        }
    }


    private static void compression(String sourcePath, ZipOutputStream zipOutputStream) {
        try {
            if (new File(sourcePath).isDirectory()) {
                zipOutputStream.putNextEntry(new ZipEntry(sourcePath.substring(sourcePath.indexOf(
                        path.substring(path.lastIndexOf(File.separator) + 1))) + File.separator));
                //列举目录下的文件/目录
                File[] files = new File(sourcePath).listFiles();
                for (File file : files) {
                    compression(file.getAbsolutePath(), zipOutputStream);
                }
            } else {
                //本人不小心把sourcePath写成了"sourcePath"找了好长时间bug。。。。
                BufferedInputStream bufferedInputStream = new BufferedInputStream(
                        new FileInputStream(new File(sourcePath)));
                //类似于在压缩文件中创建目录
                zipOutputStream.putNextEntry(new ZipEntry(sourcePath.substring(sourcePath.indexOf(
                        path.substring(path.lastIndexOf(File.separator) + 1)))));
                //如果是个空文件夹（即没有文件）那么就会没有任何数据写入
                int b;
                while ((b = bufferedInputStream.read()) != -1) zipOutputStream.write(b);

                bufferedInputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void singleFileCompression(String sourcePath) {
        try {
            //建立读取流
            BufferedInputStream bufferedInputStream = new BufferedInputStream(
                    new FileInputStream(new File(sourcePath)));
            //建立压缩流
            ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(
                    new File(sourcePath.substring(0, sourcePath.lastIndexOf('.')) + ".zip"))));

            zipOutputStream.setComment("This is a test program programed by Porcelain...");
            //创建压缩文件中的条目（目录）
            ZipEntry zipEntry = new ZipEntry(new File(sourcePath).getName());
            //将创建好的条目（目录）加入到压缩文件中
            zipOutputStream.putNextEntry(zipEntry);
            //在创建好的条目中（文件）写入具体的数据
            byte[] b = new byte[8 * 1024];
            while (bufferedInputStream.read(b) != -1) zipOutputStream.write(b);
            //关闭流
            bufferedInputStream.close();
            zipOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deCompression(String zippedPath) {
        try {
            //创建解压目录
            String destPath = zippedPath.substring(0, zippedPath.lastIndexOf('.')) + "(副本)";
            if (!new File(destPath).exists()) new File(destPath).mkdirs();

            ZipFile zipFile = new ZipFile(new File(zippedPath));
            //返回 ZIP 文件条目的枚举
            Enumeration entrySet = zipFile.entries();
            while (entrySet.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entrySet.nextElement();
                //创建解压目录
                if (!entry.getName().endsWith(File.separator)) {
                    //手动创建解压目录
                    File file = new File(destPath + File.separator +
                            entry.getName().substring(0, entry.getName().lastIndexOf(File.separator)));
                    if (!file.exists()) file.mkdirs();
                    //程序会自动创建解压文件
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(
                            destPath + File.separator + entry.getName())));
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(zipFile.getInputStream(entry));

                    int b;
                    while ((b = bufferedInputStream.read()) != -1) {
                        bufferedOutputStream.write(b);
                    }

                    bufferedOutputStream.close();
                    bufferedInputStream.close();

                } else new File(destPath + File.separator + entry.getName()).mkdirs();

            }
            zipFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void singleFileDecompression(String zippedPath) {
        try {
            //解压缩必备的类ZipFile
            ZipFile zipFile = new ZipFile(new File(zippedPath));
            Enumeration entrySet = zipFile.entries();
            while (entrySet.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entrySet.nextElement();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(zipFile.getInputStream(entry));
                //解压目录一定要手动创建
                File file = new File(zippedPath.substring(0, zippedPath.lastIndexOf('.')) + "(副本)");
                if (!file.exists()) file.mkdirs();
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(
                        file.getAbsolutePath() + File.separator + entry.getName()));

                int b;
                while ((b = bufferedInputStream.read()) != -1) bufferedOutputStream.write(b);

                bufferedInputStream.close();
                bufferedOutputStream.close();
                zipFile.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
