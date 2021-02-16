package com.example.mina.config;

import android.util.Log;

import org.apache.mina.core.session.IoSession;

/**
 * @author Chris
 * @version 1.0.0
 * @date 2/16/21
 */
public class SessionManager {
    private static final String TAG = SessionManager.class.getName();
    private static SessionManager mInstance = null;

    /**
     * 最终服务器进行通信的对象
     */
    private IoSession mSession;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (mInstance == null) {
            synchronized (SessionManager.class) {
                if (mInstance == null) {
                    Log.d(TAG, "getInstance");
                    mInstance = new SessionManager();
                }
            }
        }
        return mInstance;
    }

    public void setSession(IoSession ioSession) {
        Log.d(TAG, "setSession");
        this.mSession = ioSession;
    }

    public void writeToServer(Object msg) {
        if (this.mSession != null) {
            Log.d(TAG, "writeToServer");
            this.mSession.write(msg);
        }
    }

    public void closeSession() {
        if (this.mSession != null) {
            Log.d(TAG, "closeSession");
            this.mSession.closeOnFlush();
            this.mSession = null;
        }
    }

    // public void removeSession() {
    //     this.mSession = null;
    // }
}
