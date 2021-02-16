package com.example.mina.config;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
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
    private static final String TAG = ConnectionManager.class.getName();

    private final ConnectionConfig mConfig;

    private WeakReference<Context> mContext;
    private NioSocketConnector mConnection;
    private IoSession mSession;
    private InetSocketAddress mAddress;
    private MinaService.ConnectionThread obj;

    public ConnectionManager(ConnectionConfig config) {
        Log.d(TAG, "ConnectionManager");
        this.mConfig = config;
        this.mContext = new WeakReference<>(config.getContext());
        init();
    }

    private void init() {
        Log.d(TAG, "init");
        mAddress = new InetSocketAddress(mConfig.getIp(), mConfig.getPort());
        mConnection = new NioSocketConnector();
        mConnection.setDefaultRemoteAddress(mAddress);
        mConnection.getSessionConfig().setReceiveBufferSize(mConfig.getReadBufferSize());
        mConnection.getFilterChain().addLast("logging", new LoggingFilter());
        mConnection.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));

        mConnection.setHandler(new DefaultHandler(mContext.get()));
    }

    public boolean connect() {
        Log.d(TAG, "connect");
        try {
            ConnectFuture future = mConnection.connect();
            future.awaitUninterruptibly();
            mSession = future.getSession();
            Log.d(TAG, "connect, id " + mSession.getId());
        } catch (Exception e) {
            Log.e(TAG, "connect error", e);
            return false;
        }
        return mSession != null;
    }

    public void disconnect() {
        Log.d(TAG, "disconnect, id " + mSession.getId());
        mConnection.dispose();
        SessionManager.getInstance().closeSession();
        mConnection = null;
        mSession = null;
        mAddress = null;
        mContext = null;
    }

    public void setObj(MinaService.ConnectionThread obj) {
        this.obj = obj;
    }

    public MinaService.ConnectionThread getObj() {
        return obj;
    }

    private static class DefaultHandler extends IoHandlerAdapter {
        private final Context mContext;

        public DefaultHandler(Context context) {
            this.mContext = context;
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            Log.d(TAG, "sessionOpened, id " + session.getId());
            super.sessionOpened(session);
            // 将session保存到manager类中，后续可以使用
            SessionManager.getInstance().setSession(session);
        }

        @Override
        public void sessionClosed(IoSession session) throws Exception {
            Log.d(TAG, "sessionClosed, id " + session.getId());
            super.sessionClosed(session);
            // SessionManager.getInstance().closeSession();
        }

        @Override
        public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
            Log.d(TAG, "sessionIdle, id " + session.getId());
            super.sessionIdle(session, status);
        }

        @Override
        public void messageReceived(IoSession session, Object message) {
            Log.d(TAG, "messageReceived, id " + session.getId() + ", msg " + message);
            // super.messageReceived(session, message);
            if (mContext != null) {
                Intent intent = new Intent(Constant.BROADCAST_ACTION);
                intent.putExtra(Constant.MESSAGE, message.toString());
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
        }
    }

}
