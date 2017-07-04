package cn.snzo.common;

/**
 * 文件上传返回结果类
 */
public class UploadResult {

    private Integer status;// 状态为1 为上传成功
    private String fileName;//ftp文件名称
    private String fieldName; //参数文件名
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public UploadResult() {
    }

    public UploadResult(Integer status) {
        this.status = status;
    }
}
