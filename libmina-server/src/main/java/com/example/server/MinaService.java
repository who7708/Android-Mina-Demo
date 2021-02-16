package com.example.server;

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

public class MinaService {

    public static void main(String[] args) {
        IoAcceptor ioAcceptor = new NioSocketAcceptor();
        // 添加日志过滤器
        ioAcceptor.getFilterChain().addLast("logger", new LoggingFilter());
        ioAcceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
        ioAcceptor.setHandler(new DemoServerHandler());
        ioAcceptor.getSessionConfig().setReadBufferSize(2048);
        ioAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
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
            super.sessionCreated(session);
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            super.sessionOpened(session);
        }

        @Override
        public void sessionClosed(IoSession session) throws Exception {
            super.sessionClosed(session);
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            super.messageReceived(session, message);
            String str = message.toString();
            String date = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss.SSS").format(System.currentTimeMillis());
            session.write(date);
            System.out.println("接收的数据：" + str);
        }

        @Override
        public void messageSent(IoSession session, Object message) throws Exception {
            super.messageSent(session, message);
        }
    }
}