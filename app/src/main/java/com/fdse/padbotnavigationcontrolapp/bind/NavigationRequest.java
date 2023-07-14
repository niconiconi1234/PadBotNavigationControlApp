package com.fdse.padbotnavigationcontrolapp.bind;

public final class NavigationRequest {
    String targetPoint;

    public NavigationRequest(String targetPoint) {
        this.targetPoint = targetPoint;
    }

    public String getTargetPoint() {
        return targetPoint;
    }

    public void setTargetPoint(String targetPoint) {
        this.targetPoint = targetPoint;
    }
}