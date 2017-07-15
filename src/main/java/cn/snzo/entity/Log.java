package cn.snzo.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by chentao on 2017/7/14 0014.
 */
@Entity
@Table(name = "t_log")
public class Log extends BaseEntity {
    private String operResId;         //操作资源id
    private Integer operResType;      //操作资源类型 0 会议 1 呼叫
    private String operMethodId;      //操作方法id
    private String operMethodName;    //操作方法名称
    private String operator;          //操作人
    private Integer operType;         //操作类型 0 创建 1 操作 2 事件
    private Integer operResult;       //操作结果 0 成功 1 失败 2 超时
    public Log() {
    }

    public Log(String operResId, Integer operResType, String operMethodId,
               String operMethodName, String operator, Integer operType, Integer operResult) {
        this.operResId = operResId;
        this.operResType = operResType;
        this.operMethodId = operMethodId;
        this.operMethodName = operMethodName;
        this.operator = operator;
        this.operType = operType;
        this.operResult = operResult;
    }

    public String getOperResId() {
        return operResId;
    }

    public void setOperResId(String operResId) {
        this.operResId = operResId;
    }

    public Integer getOperResType() {
        return operResType;
    }

    public void setOperResType(Integer operResType) {
        this.operResType = operResType;
    }

    public String getOperMethodId() {
        return operMethodId;
    }

    public void setOperMethodId(String operMethodId) {
        this.operMethodId = operMethodId;
    }

    public String getOperMethodName() {
        return operMethodName;
    }

    public void setOperMethodName(String operMethodName) {
        this.operMethodName = operMethodName;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Integer getOperType() {
        return operType;
    }

    public void setOperType(Integer operType) {
        this.operType = operType;
    }

    public Integer getOperResult() {
        return operResult;
    }

    public void setOperResult(Integer operResult) {
        this.operResult = operResult;
    }
}
