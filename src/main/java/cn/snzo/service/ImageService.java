package cn.snzo.service;


import cn.snzo.common.Constants;
import cn.snzo.utils.SFTPUtil;
import cn.snzo.common.UploadResult;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.ConfigurationException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by chentao on 2016/3/11 0011.
 */
@Service
public class ImageService {

    private String hostname;
    private Integer port;
    private String username;
    private String password;

    @Value("${com.snzo.sftp.host}")
    public void setHostname(String hostname){
        this.hostname = hostname;
    }

    @Value("${com.snzo.sftp.port}")
    public void setPort(Integer port){
        this.port = port;
    }

    @Value("${com.snzo.sftp.username}")
    public void setUsername(String username){
        this.username = username;
    }

    @Value("${com.snzo.sftp.password}")
    public void setPassword(String password){
        this.password = password;
    }

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);
    /**
     * 上传文件
     * @param file 文件
     * @param path 路径
     * @return result 上传结果
     */
    public UploadResult uploadPackage(MultipartFile file,String path) {
        UploadResult result = new UploadResult(-1);
        try {

            String uploadName = makeFileName(file);
            boolean ftpFlag = sftpUpload(file, uploadName,path); //上传到sftp服务器
            if (ftpFlag) {
                result.setStatus(1);
                result.setFileName(uploadName);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 上传文件
     * @param path 路径
     * @return result 上传结果
     */
    public List<UploadResult> uploadMultiPackage(List<MultipartFile> files,String path) {

        List<UploadResult> uploadResults = new ArrayList<>();
        try {
            Map<String, MultipartFile> map = new HashMap<>();
            for(MultipartFile file : files) {
                String uploadName = makeFileName(file);
                map.put(uploadName, file);
            }
            uploadResults = sftpMultiUpload(map, path); //上传到sftp服务器
        }catch (Exception e){
            logger.error("上传图片发生异常"+e.getMessage());
        }
        return uploadResults;
    }


    /**
     * 生成文件名
     * @param file
     * @return
     */
    private String makeFileName(MultipartFile file){
        String fileName = file.getOriginalFilename();
        String[] fileNameArray = fileName.split("\\.");
        String suffixName = fileNameArray[fileNameArray.length - 1];  //文件后缀名
        UUID uuid = UUID.randomUUID();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FORMATE_yyyyMMddHHmmss);
        LocalDateTime localDateTime = LocalDateTime.now();
        String date = localDateTime.format(formatter); //文件上传日期
        return date + "-" + (uuid.toString()) + "." + suffixName;//uuid 避免文件重复
    }


    /**
     * 将图片保存到sftp服务器
     * @param file 文件
     * @param uploadName 图片名称
     * @param uploadPath 图片保存路径
     * @return 结果
     * @throws Exception 异常
     */
    protected boolean sftpUpload(MultipartFile file, String uploadName,String uploadPath) throws Exception {
        SFTPUtil sf = new SFTPUtil();
        try{
            ChannelSftp sftp = sf.connect(hostname, port, username, password);
            InputStream inputStream = file.getInputStream();

            return sf.upload(uploadPath, inputStream, uploadName, sftp);  //上传
        } catch (JSchException e) {
            throw new JSchException("sftp connect failed!");
        }
    }

    /**
     *多文件上传
     * @param fileMap 文件名-文件
     * @param uploadPath 上传路径
     * @return
     * @throws Exception
     */
    protected List<UploadResult> sftpMultiUpload(Map<String,MultipartFile> fileMap,String uploadPath) throws Exception {
        SFTPUtil sf = new SFTPUtil();
        try{
            ChannelSftp sftp = sf.connect(hostname, port, username, password);
            return   sf.uploadMultiFile(uploadPath, fileMap, sftp);
        } catch (JSchException e) {
            throw new ConfigurationException("sftp connect failed!");
        }
    }


    /**
     * 下载一个图片
     * @param fileName 文件名称
     * @param os 输出流
     * @param path 下载路径
     * @return boolean值
     * @throws Exception 异常
     */
    public boolean downloadImage(String fileName,OutputStream os,String path) throws Exception{

        SFTPUtil sf = new SFTPUtil();
        try{
            ChannelSftp sftp = sf.connect(hostname, port, username, password);
            return sf.download(path, fileName, os, sftp);
        } catch (Exception e) {
            // 客户端放弃下载,忽略
            // throw new ConfigurationException("sftp connect failed!");
        }
        return false;
    }

    /**
     * 删除图片
     * @param oldName image名称
     * @throws Exception 异常
     */
    public boolean deleteImage(String oldName,String path) throws ConfigurationException{
        String fileName = oldName.substring(oldName.indexOf("=")+1,oldName.length());
        System.out.println(fileName);
        //删除sftp服务器中的文件
        SFTPUtil sf = new SFTPUtil();
        boolean ret = false;
        try{
            ChannelSftp sftp = sf.connect(hostname,port,username,password);
            ret = sf.delete(path, fileName, sftp);
        }catch (JSchException e){
            throw new ConfigurationException("sftp connect failed!");
        }
        return ret;
    }

}
