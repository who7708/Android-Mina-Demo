package com.example.mina;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mina.config.Constant;
import com.example.mina.config.MinaService;
import com.example.mina.config.SessionManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 自定义广播接收器
     */
    private final MessageBroadcastReceiver receiver = new MessageBroadcastReceiver();
    private TextView textView;
    private Button startBtn;
    private Button sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        registerBroadcast();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        // mConnectView = findViewById(R.id.start_service_view);
        // mSendView = findViewById(R.id.send_view);

        textView = findViewById(R.id.sample_text);
        startBtn = findViewById(R.id.start_mina_btn);
        sendBtn = findViewById(R.id.send_msg_btn);

        startBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);
    }

    /**
     * 注册广播接收器
     */
    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter(Constant.BROADCAST_ACTION);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(receiver, filter);
    }

    /**
     * 取消注册
     */
    private void unregisterBroadcast() {
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.stopService(new Intent(this, MinaService.class));
        unregisterBroadcast();
    }

    @Override
    public void onClick(View v) {
        if (R.id.send_msg_btn == v.getId()) {
            SessionManager.getInstance().writeToServer("123");
        } else if (R.id.start_mina_btn == v.getId()) {
            Intent intent = new Intent(this, MinaService.class);
            this.startService(intent);
        }
    }

    private class MessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MainActivity.this.setTitle(intent.getStringExtra(Constant.MESSAGE));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(intent.getStringExtra(Constant.MESSAGE));
                }
            });
        }
    }
}