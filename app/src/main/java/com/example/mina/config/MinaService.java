package com.example.mina.config;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * @author Chris
 * @version 1.0.0
 * @date 2/16/21
 */
public class MinaService extends Service {

    private static final String TAG = MinaService.class.getName();
    private ConnectionThread thread;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        thread = new ConnectionThread("mina", getApplicationContext());
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        thread.disconnect();
        thread = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    static class ConnectionThread extends HandlerThread {

        ConnectionManager mManager;

        public ConnectionThread(String name, Context context) {
            super(name);
            Log.d(TAG, "ConnectionThread");
            ConnectionConfig config = ConnectionConfig.builder()
                    .ip("192.168.31.10")
                    .port(9123)
                    .readBufferSize(2048)
                    .connectionTimeout(10000)
                    .context(context)
                    .build();
            mManager = new ConnectionManager(config);
        }

        @Override
        protected void onLooperPrepared() {
            Log.d(TAG, "onLooperPrepared");
            super.onLooperPrepared();
            for (; ; ) {
                // 完成服务器连接
                boolean isConnection = mManager.connect();
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
            Log.d(TAG, "disconnect");
            // 断开连接
            mManager.disconnect();
        }
    }
}
