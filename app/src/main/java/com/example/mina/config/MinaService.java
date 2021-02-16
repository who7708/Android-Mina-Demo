package com.example.mina.config;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * @author Chris
 * @version 1.0.0
 * @date 2/16/21
 */
public class MinaService extends Service {

    private ConnectionThread thread;

    @Override
    public void onCreate() {
        super.onCreate();
        thread = new ConnectionThread("mina", getApplicationContext());
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thread.disconnect();
        thread = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class ConnectionThread extends HandlerThread {

        private Context context;
        boolean isConnection;
        ConnectionManager mManager;

        public ConnectionThread(String name, Context context) {
            super(name);
            this.context = context;
            ConnectionConfig config = ConnectionConfig.builder()
                    .ip("")
                    .port(9123)
                    .readBufferSize(2048)
                    .connectionTimeout(10000)
                    .build();

        }

        @Override
        protected void onLooperPrepared() {
            // super.onLooperPrepared();
            for (; ; ) {
                // 完成服务器连接
                isConnection = mManager.connect();
                if (isConnection) {
                    break;
                }
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (Exception ignored) {
                }
            }
        }

        public void disconnect() {
            // 断开连接
            mManager.disconnect();
        }
    }
}
