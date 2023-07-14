package com.fdse.padbotnavigationcontrolapp.service;

import android.os.RemoteException;

import java.util.List;
import java.util.Objects;

import cn.inbot.componentnavigation.domain.IndoorMapVo;
import cn.inbot.componentnavigation.domain.IndoorMapVoListResult;
import cn.inbot.componentnavigation.domain.RobotTargetPointVo;
import cn.inbot.navigationlib.PadBotNavigationClient;

public class PadbotTargetPointService {
    public static RobotTargetPointVo getRobotTargetPointVoOnDefaultMap(String targetPointName) throws RemoteException {
        // 获得默认地图
        IndoorMapVoListResult indoorMapVoListResult = PadBotNavigationClient.getInstance().getMapInfo();
        if (null == indoorMapVoListResult || null == indoorMapVoListResult.getMapVoList()) {
            throw new RuntimeException("Failed to get map information");
        }
        List<IndoorMapVo> indoorMapVoList = indoorMapVoListResult.getMapVoList();
        IndoorMapVo map = indoorMapVoList.stream().filter(IndoorMapVo::isDefaultUse).findFirst().orElse(null);
        if (map == null) {
            throw new RuntimeException("Cannot find default map!");
        }

        // 获得目标点
        List<RobotTargetPointVo> targetPointVos = map.getTargetPointVoList();
        RobotTargetPointVo targetPointVo = targetPointVos.stream().filter(t -> Objects.equals(t.getName(), targetPointName)).findFirst().orElse(null);
        if (targetPointVo == null) {
            throw new RuntimeException("Cannot find target point " + targetPointName);
        }
        return targetPointVo;
    }
}
