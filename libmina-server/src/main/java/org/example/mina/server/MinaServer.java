package org.example.mina.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.net.InetSocketAddress;

/**
 * @author Chris
 */
@Slf4j
public class MinaServer {

    private static final Gson GSON = new GsonBuilder().create();

    public static void main(String[] args) {
        IoAcceptor ioAcceptor = new NioSocketAcceptor();
        // 添加日志过滤器
        ioAcceptor.getFilterChain().addLast("logger", new LoggingFilter());
        ioAcceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
        ioAcceptor.setHandler(new DemoServerHandler());
        ioAcceptor.getSessionConfig().setReadBufferSize(2048);
        ioAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        log.info("mina server starting...");
        try {
            ioAcceptor.bind(new InetSocketAddress(9123));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 负责session对象的创建监听及消息发送、接收的监听
     */
    private static class DemoServerHandler extends IoHandlerAdapter {
        @Override
        public void sessionCreated(IoSession session) throws Exception {
            log.info("sessionCreated, id {}", session.getId());
            super.sessionCreated(session);
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            log.info("sessionOpened, id {}", session.getId());
            super.sessionOpened(session);
        }

        @Override
        public void sessionClosed(IoSession session) throws Exception {
            log.info("sessionClosed, id {}", session.getId());
            super.sessionClosed(session);
        }

        // private ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            log.info("messageReceived, id {}, msg {}", session.getId(), GSON.toJson(message));
            super.messageReceived(session, message);
            // executor.scheduleWithFixedDelay(new Runnable() {
            //     @Override
            //     public void run() {
            //         String str = message.toString();
            //         String date =
            //                 FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis());
            //         session.write(date);
            //         System.out.println("接收的数据：" + str);
            //     }
            // }, 1L, 3L, TimeUnit.SECONDS);
            String str = message.toString();
            String date = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis());
            session.write(date);
            System.out.println("接收的数据：" + str);
        }

        @Override
        public void messageSent(IoSession session, Object message) throws Exception {
            log.info("messageSent, id {}, msg {}", session.getId(), GSON.toJson(message));
            super.messageSent(session, message);
        }
    }
}