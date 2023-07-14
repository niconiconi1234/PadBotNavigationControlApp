package com.fdse.padbotnavigationcontrolapp.bind;

public class RelocationRequest {
    String relocationPoint;

    public RelocationRequest(String relocationPoint) {
        this.relocationPoint = relocationPoint;
    }

    public String getRelocationPoint() {
        return relocationPoint;
    }

    public void setRelocationPoint(String relocationPoint) {
        this.relocationPoint = relocationPoint;
    }
}
