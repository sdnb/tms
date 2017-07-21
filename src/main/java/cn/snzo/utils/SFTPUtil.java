package cn.snzo.utils;

/**
 * 描述：sftp服务器文件上传下载类
 * @author jss
 * @version 1.0, 2012-12-03
 */

import cn.snzo.common.Constants;
import cn.snzo.common.UploadResult;
import com.jcraft.jsch.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;


public class SFTPUtil {

    /**
     * 连接sftp服务器
     * @param host 主机
     * @param port 端口
     * @param username 用户名
     * @param password 密码
     * @return ChannelSftp对象
     * @throws JSchException JSch异常
     */
    public ChannelSftp connect(String host, int port, String username,String password) throws JSchException {
        ChannelSftp sftp = null;
//        try {
            JSch jsch = new JSch();
            jsch.getSession(username, host, port);
            Session sshSession = jsch.getSession(username, host, port);
            System.out.println("Session created.");
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.setTimeout(5000); //设置timeout时间
            sshSession.connect();
            System.out.println("Session connected.");
            System.out.println("Opening Channel.");
            Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
            System.out.println("Connected to " + host + ".");
            //System.out.println("登录成功");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return sftp;
    }

    /**
     * 上传文件
     * @param directory 上传的目录
     * @param inputStream 文件流
     * @param fileName 文件名
     * @param sftp sftp
     * @return boolean类型 true成功 false失败
     */
//    public void upload(String directory, String uploadFile, ChannelSftp sftp) {
    public boolean upload(String directory, InputStream inputStream,String fileName, ChannelSftp sftp) {
        try {
            sftp.cd(directory);
            //File file=new File(uploadFile);
//            sftp.put(new FileInputStream(file), file.getName());
            sftp.put(inputStream, fileName);
            System.out.println("上传成功！");
            return true;
        } catch (Exception e) {
            if(directory.contains(Constants.PREFIX_SFTP_PATH)){
                try {
                    sftp.mkdir(directory);
                    sftp.put(inputStream, fileName);
                    System.out.println("上传成功！");
                    return true;
                } catch (SftpException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;

        }finally {
            try {
                //如果有sesstion的disconnect，程序不会退出
                sftp.getSession().disconnect();
                System.out.println("关闭上传文件sftp的session");
            } catch (JSchException e) {
                e.printStackTrace();
            }
            try{
                inputStream.close();
                System.out.println("关闭输入流");
                sftp.disconnect();
                System.out.println("关闭上传文件的sftp");
            }catch (IOException io){
                io.printStackTrace();
            }
        }
    }

    public static void cd(String directory, ChannelSftp sftp){
        try {
            sftp.cd(directory);
        } catch (SftpException e) {
            if(directory.contains(Constants.PREFIX_SFTP_PATH)){
                try {
                    sftp.mkdir(directory);
                    sftp.cd(directory);
                    System.out.println("创建文件夹！");
                } catch (SftpException e1) {
                    System.out.println("创建新文件夹失败");
                }
            }
        }
    }

    /**
     * 上传多个文件
     * @param directory 上传的目录
     * @param sftp sftp
     * @return boolean类型 true成功 false失败
     */
//    public void upload(String directory, String uploadFile, ChannelSftp sftp) {
    public List<UploadResult> uploadMultiFile(String directory, Map<String,MultipartFile> map, ChannelSftp sftp) {
        List<UploadResult> results = new ArrayList<>();
        UploadResult uploadResult = new UploadResult(-1);
        try {
            cd(directory, sftp);
            Set<String> keys = map.keySet();
            for(String fileName : keys) {
                MultipartFile file = map.get(fileName);
                sftp.put(file.getInputStream(), fileName);
                UploadResult ur = new UploadResult(1);
                ur.setFieldName(file.getName());
                ur.setFileName(fileName);
                results.add(ur);
            }

            System.out.println("上传成功！");
            return results;
        } catch (Exception e) {
            uploadResult.setStatus(-1);
            results.add(uploadResult);
            return results;
        }finally {
            try {
                //如果有sesstion的disconnect，程序不会退出
                sftp.getSession().disconnect();
                System.out.println("关闭上传文件sftp的session");
            } catch (JSchException e) {
                e.printStackTrace();
            }
            try{
                Collection<MultipartFile> inputStreams = map.values();
                for(MultipartFile file : inputStreams){
                    file.getInputStream().close();
                    System.out.println("关闭输入流");
                    sftp.disconnect();
                    System.out.println("关闭上传文件的sftp");
                }
            }catch (IOException io){
                io.printStackTrace();
            }
        }
    }


    /**
     * 下载文件
     * @param directory 下载目录
     * @param downloadFile 下载的文件
     * @param os 输出流
     * @param sftp ChannelSftp对象
     * @return boolean类型 true成功 false失败
     */
    public boolean download(String directory, String downloadFile,OutputStream os, ChannelSftp sftp) {
        try {
            sftp.cd(directory);
//            File file=new File(saveFile);
//            return new FileInputStream(file);

            sftp.get(downloadFile, os);
            System.out.println("文件所在目录："+ directory);
            System.out.println("文件名："+ downloadFile);
            System.out.println("下载成功！");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().toLowerCase().equals("no such file")) {
                System.out.println("sftp中找不到要下载的文件"+directory+downloadFile);
            }
            return false;
        }finally {

            try {
                //如果有sesstion的disconnect，程序不会退出
                sftp.getSession().disconnect();
                System.out.println("关闭下载文件sftp的session");
            } catch (JSchException e) {
                e.printStackTrace();
            }

            try{
                os.close();
                System.out.println("关闭输入流");
                sftp.disconnect();
                System.out.println("关闭下载文件的sftp");
            }catch (IOException io){
                io.printStackTrace();
            }
        }
    }

    /**
     * 删除文件
     * @param directory 要删除文件所在目录
     * @param deleteFile 要删除的文件
     * @param sftp ChannelSftp对象
     * @return boolean类型 true成功 false失败
     */
    public boolean delete(String directory, String deleteFile, ChannelSftp sftp) {
        try {
            sftp.cd(directory);
            sftp.rm(deleteFile);
            System.out.println("删除成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            try {
                //如果有sesstion的disconnect，程序不会退出
                sftp.getSession().disconnect();
                System.out.println("关闭删除文件sftp的session");
            } catch (JSchException e) {
                e.printStackTrace();
            }
            sftp.disconnect();
            System.out.println("关闭删除文件的sftp");
        }
    }

    /**
     * 列出目录下的文件
     * @param directory 要列出的目录
     * @param sftp ChannelSftp对象
     * @return Vector
     * @throws SftpException Sftp异常
     */
    public Vector listFiles(String directory, ChannelSftp sftp) throws SftpException {
        return sftp.ls(directory);
    }
/*
    public static void main(String[] args) {
        SFTPUtil sf = new SFTPUtil();
        String host = "121.40.157.200";
        int port = 22;
        String username = "deploy";
        String password = "deploy";
        String directory = "/home/deploy/upload/";
        String uploadFile = "C:\\Users\\wh\\Desktop\\lishi\\messages.properties";

        String downloadFile = "upload.txt";
        String saveFile = "D:\\tmp\\download.txt";
        String deleteFile = "delete.txt";


        ChannelSftp sftp=sf.connect(host, port, username, password);


        File file=new File(uploadFile);
        try{
            InputStream inputStream = new FileInputStream(file);
            String fileName = file.getName();
            System.out.println("fileName----->"+fileName);
            sf.upload(directory, inputStream,fileName,sftp);  //上传
        }catch (Exception e){
            e.printStackTrace();
        }



//        sf.download(directory, downloadFile, saveFile, sftp);
//        sf.delete(directory, deleteFile, sftp);
        try{
//            sftp.cd(directory);
//            sftp.mkdir("ss");
            System.out.println("finished");
        }catch(Exception e){
            e.printStackTrace();
        }
    }*/
}
