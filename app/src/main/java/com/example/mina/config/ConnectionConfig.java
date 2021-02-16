package com.example.mina.config;

import android.content.Context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Chris
 * @version 1.0.0
 * @date 2/16/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionConfig {
    private Context context;
    private String ip;
    private int port;
    private int readBufferSize;
    private long connectionTimeout;
}
