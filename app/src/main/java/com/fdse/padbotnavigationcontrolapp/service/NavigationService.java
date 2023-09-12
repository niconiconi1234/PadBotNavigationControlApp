package com.fdse.padbotnavigationcontrolapp.service;

import android.os.RemoteException;
import android.util.Log;

import com.alibaba.fastjson2.JSON;
import com.fdse.padbotnavigationcontrolapp.bind.NavigationRequest;
import com.fdse.padbotnavigationcontrolapp.bind.NavigationResponse;
import com.fdse.padbotnavigationcontrolapp.service.PadbotTargetPointService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.inbot.componentnavigation.domain.ActionStatus;
import cn.inbot.componentnavigation.domain.NavigateStatus;
import cn.inbot.componentnavigation.domain.RobotTargetPointVo;
import cn.inbot.navigationlib.PadBotNavigationClient;
import cn.inbot.navigationlib.event.OnNavigateInfoChangedEvent;

public class NavigationService {

    private volatile boolean navigationSuccess = false;
    private volatile String navigationMessage = "";
    private volatile Thread handleNavigationRequestThread = null;

    private NavigateStatus navigateStatus = NavigateStatus.IDLE;

    private String robotLocation = "UNKNOWN";

    public NavigationService() {
        EventBus.getDefault().register(this);
    }

    public synchronized NavigationResponse navigation(NavigationRequest req) throws RemoteException {
        String targetPointName = req.getTargetPoint();
        if (targetPointName == null) {
            throw new RuntimeException("targetPoint cannot be null!");
        }

        // 获得目标点
        RobotTargetPointVo targetPointVo = PadbotTargetPointService.getRobotTargetPointVoOnDefaultMap(targetPointName);

        // 目标点id
        String targetPointId = targetPointVo.getId();

        // 启动导航
        navigationSuccess = false;
        handleNavigationRequestThread = Thread.currentThread();
        PadBotNavigationClient.getInstance().startNavigateByPointId(targetPointId);

        // Waiting to be awakened (interrupted) by eventbus thread when navigation ends......
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException ignored) {
        }

        if (navigateStatus == NavigateStatus.FINISHED) {
            robotLocation = targetPointName;
        } else {
            robotLocation = "UNKNOWN";
        }

        return new NavigationResponse(navigationSuccess, navigationMessage);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onNavigateInfoChangedEvent(OnNavigateInfoChangedEvent event) {
        if (this.handleNavigationRequestThread == null) {
            return;
        }
        Log.i("fdse", JSON.toJSONString(event));
        try {
            ActionStatus actionStatus = PadBotNavigationClient.getInstance().getActionStatus();
            NavigateStatus navigateStatus = event.getNavigateInfo().getNavigateStatus();
            this.navigateStatus = navigateStatus;
            this.robotLocation = "MOVING";
            if ((navigateStatus == NavigateStatus.FINISHED || navigateStatus == NavigateStatus.FAILED) && (actionStatus == ActionStatus.IDLE || actionStatus == ActionStatus.NAVIGATING)) {
                if (navigateStatus == NavigateStatus.FINISHED) {
                    navigationMessage = "Success";
                    navigationSuccess = true;
                } else {
                    navigationMessage = "Failed";
                    navigationSuccess = false;
                }
                // Notify the handleNavigationRequestThread that the navigation has ended!
                handleNavigationRequestThread.interrupt();
                handleNavigationRequestThread = null;
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
            navigationSuccess = false;
            navigationMessage = "Failed";
            // Notify the handleNavigationRequestThread that the navigation has ended!
            handleNavigationRequestThread.interrupt();
            handleNavigationRequestThread = null;
        }
    }

    public NavigateStatus getNavigateStatus() {
        return navigateStatus;
    }

    public String getRobotLocation() {
        return robotLocation;
    }
}
