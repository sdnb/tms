package cn.snzo.common;

/**
 * Created by chentao on 2017/2/10 0010.
 */
public class ObjectResult {
    private String status;
    private Object message;

    public ObjectResult() {
    }


    public ObjectResult(String status, Object message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}
