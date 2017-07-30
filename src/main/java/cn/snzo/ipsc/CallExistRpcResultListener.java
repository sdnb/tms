package cn.snzo.ipsc;

/**
 * Created by Administrator on 2017/7/30 0030.
 */
public class CallExistRpcResultListener extends SimpleRpcResultListener {
    private boolean isExist;

    @Override
    protected void onResult(Object o) {
        isExist = true;
    }

    public boolean isExist() {
        return isExist;
    }
}
