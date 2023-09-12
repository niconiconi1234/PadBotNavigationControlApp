package com.fdse.padbotnavigationcontrolapp.bind;

import cn.inbot.componentnavigation.domain.ActionStatus;
import cn.inbot.componentnavigation.domain.NavigateStatus;
import cn.inbot.padbotbasesdk.constant.PadBotBaseSDKConstants;

public class StatusResponse {
    private Integer batteryPercentage;
    private PadBotBaseSDKConstants.BatteryStatus batteryStatus;
    private ActionStatus actionStatus;

    private NavigateStatus navigationStatus;

    private String robotLocation;

    public StatusResponse() {
    }

    public Integer getBatteryPercentage() {
        return batteryPercentage;
    }

    public void setBatteryPercentage(Integer batteryPercentage) {
        this.batteryPercentage = batteryPercentage;
    }

    public PadBotBaseSDKConstants.BatteryStatus getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryStatus(PadBotBaseSDKConstants.BatteryStatus batteryStatus) {
        this.batteryStatus = batteryStatus;
    }

    public ActionStatus getActionStatus() {
        return actionStatus;
    }

    public void setActionStatus(ActionStatus actionStatus) {
        this.actionStatus = actionStatus;
    }

    public NavigateStatus getNavigationStatus() {
        return navigationStatus;
    }

    public void setNavigationStatus(NavigateStatus navigationStatus) {
        this.navigationStatus = navigationStatus;
    }

    public String getRobotLocation() {
        return robotLocation;
    }

    public void setRobotLocation(String robotLocation) {
        this.robotLocation = robotLocation;
    }
}
