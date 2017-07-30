package cn.snzo.ipsc;

import com.hesong.ipsc.ccf.RpcError;

/**
 * Created by Administrator on 2017/7/30 0030.
 */
public class CallExistRpcResultListener extends SimpleRpcResultListener {
    private boolean isExist;

    public CallExistRpcResultListener(String rpcMethodName, boolean isExist) {
        super(rpcMethodName);
        this.isExist = isExist;
    }

    @Override
    protected void onResult(Object o) {
        isExist = true;
    }

    @Override
    protected void onError(RpcError rpcError) {
        logger.info("===========> 调用rpc方法：{} 失败！code= {}, msg={}", rpcMethodName, rpcError.getCode(), rpcError.getMessage());
    }

    @Override
    protected void onTimeout() {
        logger.info("===========> 调用rpc方法：{} 超时！", rpcMethodName);
    }

    public boolean isExist() {
        return isExist;
    }
}
