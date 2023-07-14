package com.fdse.padbotnavigationcontrolapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSON;
import com.fdse.padbotnavigationcontrolapp.bind.NavigationRequest;
import com.fdse.padbotnavigationcontrolapp.bind.NavigationResponse;
import com.fdse.padbotnavigationcontrolapp.bind.RelocationRequest;
import com.fdse.padbotnavigationcontrolapp.bind.RelocationResponse;
import com.fdse.padbotnavigationcontrolapp.controller.NavigationController;
import com.fdse.padbotnavigationcontrolapp.controller.RelocationController;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import cn.inbot.navigationlib.PadBotNavigationClient;

public class MainActivity extends AppCompatActivity {

    private final NavigationController navigationController = new NavigationController();
    private final RelocationController relocationController = new RelocationController();
    private final AsyncHttpServer server = new AsyncHttpServer();

    public static final String APPLICATION_JSON = "application/json";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startHttpServer();
        initNavigation();
    }

    private void initNavigation() {
        PadBotNavigationClient.getInstance().connect(getApplicationContext()); // 连接导航
    }

    private void startHttpServer() {
        server.post("/navigation", (asyncHttpServerRequest, asyncHttpServerResponse) -> {
            CompletableFuture.runAsync(() -> {
                try {
                    NavigationRequest req = parseHttpBodyJsonObject(asyncHttpServerRequest, NavigationRequest.class); // 解析请求
                    NavigationResponse rsp = navigationController.handleNavigationRequest(req); // 处理导航请求
                    asyncHttpServerResponse.send(APPLICATION_JSON, JSON.toJSONString(rsp));
                } catch (Exception e) {
                    NavigationResponse rsp = new NavigationResponse(false, e.getMessage());
                    asyncHttpServerResponse.send(APPLICATION_JSON, JSON.toJSONString(rsp));
                }
            });
        });
        server.post("/relocation", (asyncHttpServerRequest, asyncHttpServerResponse) -> {
            CompletableFuture.runAsync(() -> {
                try {
                    RelocationRequest req = parseHttpBodyJsonObject(asyncHttpServerRequest, RelocationRequest.class);
                    RelocationResponse rsp = relocationController.handleRelocationRequest(req);
                    asyncHttpServerResponse.send(APPLICATION_JSON, JSON.toJSONString(rsp));
                } catch (Exception e) {
                    RelocationResponse rsp = new RelocationResponse(false, e.getMessage());
                    asyncHttpServerResponse.send(APPLICATION_JSON, JSON.toJSONString(rsp));
                }
            });
        });
        new Thread(() -> {
            server.listen(5000);
        }).start();
    }

    private <T> T parseHttpBodyJsonObject(AsyncHttpServerRequest asyncHttpServerRequest, Class<? extends T> clazz) {
        if (!APPLICATION_JSON.equals(asyncHttpServerRequest.getBody().getContentType())) {
            throw new RuntimeException("Content type should be application/json");
        }
        return JSON.parseObject(asyncHttpServerRequest.getBody().get().toString(), clazz);
    }
}