package cn.snzo.ipsc;

import cn.snzo.entity.Log;
import cn.snzo.repository.LogRepository;
import cn.snzo.vo.OperResultEnum;
import com.hesong.ipsc.ccf.RpcError;
import com.hesong.ipsc.ccf.RpcResultListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by Administrator on 2017/7/29 0029.
 */
public class SimpleRpcResultListener extends RpcResultListener {

    protected static Logger logger = LoggerFactory.getLogger(SimpleRpcResultListener.class);


    private Log log;
    private LogRepository logRepository;
    protected String rpcMethodName;
    private Map<String, Object> rpcResult;

    public SimpleRpcResultListener(Log log, LogRepository logRepository, String rpcMethodName) {
        this.log = log;
        this.logRepository = logRepository;
        this.rpcMethodName = rpcMethodName;
    }

    public SimpleRpcResultListener() {
    }

    public SimpleRpcResultListener(String rpcMethodName) {
        this.rpcMethodName = rpcMethodName;
    }

    @Override
    protected void onResult(Object o) {
        @SuppressWarnings("unchecked")
        Map<String, Object> ret = (Map<String, Object>)o;
        rpcResult = ret;
        if (ret != null) {
            logger.info("===========> 调用rpc方法：{} 成功, resId={}！", rpcMethodName, ret.get("res_id"));
            if (log != null) {
                log.setOperResult(OperResultEnum.SUCCESS.ordinal());
                log.setOperResId((String) ret.get("res_id"));
                log.setOperMethodId(rpcMethodName);
                logRepository.save(log);
            }
        }
        additionExcute();
    }

    @Override
    protected void onError(RpcError rpcError) {
        logger.info("===========> 调用rpc方法：{} 失败！code= {}, msg={}", rpcMethodName, rpcError.getCode(), rpcError.getMessage());
        if (log != null) {
            log.setOperResult(OperResultEnum.ERROR.ordinal());
            logRepository.save(log);
        }
    }

    @Override
    protected void onTimeout() {
        logger.info("===========> 调用rpc方法：{} 超时！", rpcMethodName);
        if (log != null) {
            log.setOperResult(OperResultEnum.TIMEOUT.ordinal());
            logRepository.save(log);
        }
    }

    protected void additionExcute(){
        System.out.println("===========> do nothing if not impl!");
    }

    public Map<String, Object> getRpcResult() {
        return rpcResult;
    }
}
