package com.fdse.padbotnavigationcontrolapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson2.JSON;
import com.fdse.padbotnavigationcontrolapp.bind.NavigationRequest;
import com.fdse.padbotnavigationcontrolapp.bind.NavigationResponse;
import com.fdse.padbotnavigationcontrolapp.bind.RelocationRequest;
import com.fdse.padbotnavigationcontrolapp.bind.RelocationResponse;
import com.fdse.padbotnavigationcontrolapp.service.NavigationService;
import com.fdse.padbotnavigationcontrolapp.service.RelocationService;
import com.fdse.padbotnavigationcontrolapp.eventhandler.BatteryInfoEventHandler;
import com.fdse.padbotnavigationcontrolapp.service.RobotStatusService;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;

import java.util.concurrent.CompletableFuture;

import cn.inbot.navigationlib.PadBotNavigationClient;
import cn.inbot.padbotbasesdk.RobotManager;

public class MainActivity extends AppCompatActivity {

    private final NavigationService navigationService = new NavigationService();
    private final RelocationService relocationService = new RelocationService();
    private final BatteryInfoEventHandler batteryInfoEventHandler = new BatteryInfoEventHandler();
    private final RobotStatusService robotStatusService = new RobotStatusService(batteryInfoEventHandler, navigationService);

    private final AsyncHttpServer server = new AsyncHttpServer();

    public static final String APPLICATION_JSON = "application/json";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNavigation();
        initRobotManager();
        startHttpServer();
    }

    private void initNavigation() {
        PadBotNavigationClient.getInstance().connect(getApplicationContext()); // 连接导航
    }

    private void initRobotManager() {
        RobotManager.getInstance().init(getApplicationContext());
    }

    private void startHttpServer() {
        server.post("/navigation", (asyncHttpServerRequest, asyncHttpServerResponse) -> {
            CompletableFuture.runAsync(() -> {
                try {
                    NavigationRequest req = parseHttpBodyJsonObject(asyncHttpServerRequest, NavigationRequest.class); // 解析请求
                    NavigationResponse rsp = navigationService.navigation(req); // 处理导航请求
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
                    RelocationResponse rsp = relocationService.relocation(req);
                    asyncHttpServerResponse.send(APPLICATION_JSON, JSON.toJSONString(rsp));
                } catch (Exception e) {
                    RelocationResponse rsp = new RelocationResponse(false, e.getMessage());
                    asyncHttpServerResponse.send(APPLICATION_JSON, JSON.toJSONString(rsp));
                }
            });
        });
        server.get("/status", (asyncHttpServerRequest, asyncHttpServerResponse) -> CompletableFuture.runAsync(() -> asyncHttpServerResponse.send(APPLICATION_JSON, JSON.toJSONString(robotStatusService.status()))));
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