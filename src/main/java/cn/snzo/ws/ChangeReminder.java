package cn.snzo.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by chentao on 2017/07/22 0005.
 */
@ServerEndpoint(value = "/reminder")
@Component
public class ChangeReminder {

    private Logger logger  = LoggerFactory.getLogger(ChangeReminder.class);

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<ChangeReminder> webSocketSet = new CopyOnWriteArraySet<ChangeReminder>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);     //加入set中
        logger.info("建立WebSocket连接");
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        logger.info("关闭WebSocket连接");
    }

    /**
     * 发送消息
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
    }


    /**
     * 群发消息
     */
    public void sendMessageToAll(String message){
        logger.info("消息推送:"+message);
        for (ChangeReminder reminder : webSocketSet) {
            try {
                reminder.sendMessage(message);
            } catch (IOException e) {
                logger.error(">>>>>>>>> 发送消息错误，内容{}", message);
                continue;
            }
        }
    }
}
