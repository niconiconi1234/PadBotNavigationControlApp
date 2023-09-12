package com.fdse.padbotnavigationcontrolapp.service;


import com.fdse.padbotnavigationcontrolapp.bind.StatusResponse;
import com.fdse.padbotnavigationcontrolapp.eventhandler.BatteryInfoEventHandler;

import cn.inbot.componentnavigation.domain.ActionStatus;
import cn.inbot.navigationlib.PadBotNavigationClient;

public class RobotStatusService {

    private final BatteryInfoEventHandler batteryInfoEventHandler;

    private final NavigationService navigationService;

    public RobotStatusService(BatteryInfoEventHandler batteryInfoEventHandler, NavigationService navigationService) {
        this.batteryInfoEventHandler = batteryInfoEventHandler;
        this.navigationService = navigationService;
    }

    public StatusResponse status() {
        StatusResponse rsp = new StatusResponse();
        rsp.setBatteryPercentage(batteryInfoEventHandler.getBatteryPercentage());
        rsp.setBatteryStatus(batteryInfoEventHandler.getBatteryStatus());

        try {
            rsp.setActionStatus(PadBotNavigationClient.getInstance().getActionStatus());
        } catch (Exception ignored) {
        }

        rsp.setNavigationStatus(navigationService.getNavigateStatus());
        rsp.setRobotLocation(navigationService.getRobotLocation());

        return rsp;
    }

}
