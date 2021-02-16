package com.example.mina.config;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;

/**
 * @author Chris
 * @version 1.0.0
 * @date 2/16/21
 */
public class ConnectionManager {
    private static final String BROADCAST_ACTION = "com.example.mina.server";
    private static final String MESSAGE = "message";

    private final ConnectionConfig mConfig;

    private WeakReference<Context> mContext;
    private NioSocketConnector mConnection;
    private IoSession mSession;
    private InetSocketAddress mAddress;

    public ConnectionManager(ConnectionConfig config) {
        this.mConfig = config;
        this.mContext = new WeakReference<>(config.getContext);

        init();
    }

    private void init() {
        mAddress = new InetSocketAddress(mConfig.getIp(), mConfig.getPort());
        mConnection = new NioSocketConnector();
        mConnection.getSessionConfig().setReceiveBufferSize(mConfig.getReadBufferSize());
        mConnection.getFilterChain().addLast("logging", new LoggingFilter());
        mConnection.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));

        mConnection.setHandler(new DefaultHandler(mContext.get()));
    }

    public boolean connect() {
        try {
            ConnectFuture future = mConnection.connect();
            future.awaitUninterruptibly();
            mSession = future.getSession();
        } catch (Exception e) {
            return false;
        }
        return mSession != null;
    }

    public void disconnect() {
        mConnection.dispose();
        mConnection = null;
        mSession = null;
        mAddress = null;
        mContext = null;
    }

    private static class DefaultHandler extends IoHandlerAdapter {
        private Context mContext;

        public DefaultHandler(Context context) {
            this.mContext = context;
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            // super.sessionOpened(session);
            // 将session保存到manager类中，后续可以使用
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            // super.messageReceived(session, message);
            if (mContext != null) {
                Intent intent = new Intent(BROADCAST_ACTION);
                intent.putExtra(MESSAGE, message.toString());
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
        }
    }

}
