package cn.snzo.ipsc;

import cn.snzo.entity.Call;
import cn.snzo.entity.Log;
import cn.snzo.repository.CallRepository;
import cn.snzo.repository.LogRepository;
import cn.snzo.utils.IpscUtil;

/**
 * Created by Administrator on 2017/7/29 0029.
 */
public class CreateCallRpcResultListener extends SimpleRpcResultListener {

    private Call call;
    private CallRepository callRepository;
    private boolean isRecall;

    public CreateCallRpcResultListener(Log log, LogRepository logRepository, String rpcMethodName, Call call, CallRepository callRepository, boolean isRecall) {
        super(log, logRepository, rpcMethodName);
        this.call = call;
        this.callRepository = callRepository;
        this.isRecall = isRecall;
    }

    public CreateCallRpcResultListener(Log log, LogRepository logRepository, String rpcMethodName) {
        super(log, logRepository, rpcMethodName);
    }

    protected void additionExcute(){
        logger.info("===========> CreateCallRpcResultListener.additionExcute()");
        String callId = (String) getRpcResult().get("res_id");
        logger.info("===========> bind call {} to conf {}", callId, call.getConfResId());
        IpscUtil.callConfMap.put(callId, call.getConfResId());
        //如果是对同一个电话重新发起呼叫，将原来的呼叫缓存清除
        if (isRecall) {
            logger.info("===========> remove old call {} from conf {}", callId, call.getConfResId());
            IpscUtil.callConfMap.remove(call.getResId());
        }
        call.setResId(callId);
        callRepository.save(call);
    }
}
