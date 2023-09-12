package com.fdse.padbotnavigationcontrolapp.eventhandler;

import android.util.Log;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.inbot.padbotbasesdk.constant.PadBotBaseSDKConstants;
import cn.inbot.padbotbasesdk.event.ReceiveBatteryInfoEvent;
import cn.inbot.padbotbasesdk.util.EventBusUtils;

public class BatteryInfoEventHandler implements IEventHandler<ReceiveBatteryInfoEvent> {

    private ReceiveBatteryInfoEvent latestEvent;

    public BatteryInfoEventHandler() {
        EventBusUtils.register(this);
    }

    @Override
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void handle(ReceiveBatteryInfoEvent event) {
        Log.i("BatteryInfoEventHandler", "Received battery info event");
        this.latestEvent = event;
    }

    @Override
    public ReceiveBatteryInfoEvent getLatestEvent() {
        return this.latestEvent;
    }

    public Integer getBatteryPercentage() {
        if (this.latestEvent == null) {
            return null;
        }
        return this.latestEvent.getBatteryPercentage();
    }

    public PadBotBaseSDKConstants.BatteryStatus getBatteryStatus() {
        if (this.latestEvent == null) {
            return null;
        }
        return this.latestEvent.getStatus();
    }
}
