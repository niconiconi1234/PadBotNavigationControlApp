package com.fdse.padbotnavigationcontrolapp.service;

import android.os.RemoteException;

import com.fdse.padbotnavigationcontrolapp.bind.RelocationRequest;
import com.fdse.padbotnavigationcontrolapp.bind.RelocationResponse;
import com.fdse.padbotnavigationcontrolapp.service.PadbotTargetPointService;

import cn.inbot.componentnavigation.domain.ActionReturn;
import cn.inbot.componentnavigation.domain.RobotTargetPointVo;
import cn.inbot.navigationlib.PadBotNavigationClient;

public class RelocationService {

    /**
     * 派宝机器人在开机后的第一次导航前，使用这个函数告诉机器人它的大概位置（给机器人定位）。
     * 机器人知道自己的大概位置后，通过算法得出其精确位置，才能开始第一次导航。
     * 用人力推着机器人到地图上标定的一个点P，调用这个函数，将req.relocationPoint设置成点P的名字，让机器人知道自己在点P附近。
     * 随后机器人利用内置算法得出其精确位置，之后就可以开始导航了！
     *
     * @param req RelocationRequest
     * @return RelocationResponse
     */
    public RelocationResponse relocation(RelocationRequest req) throws RemoteException {
        String relocationPointName = req.getRelocationPoint();
        if (relocationPointName == null) {
            throw new RuntimeException("relocationPoint cannot be null");
        }

        // 获得定位点
        RobotTargetPointVo pointVo = PadbotTargetPointService.getRobotTargetPointVoOnDefaultMap(relocationPointName);

        // 给机器人定位
        ActionReturn ret = PadBotNavigationClient.getInstance().assistRelocate(pointVo.getCoordinateX(), pointVo.getCoordinateY());

        if (ret.getCode() == ActionReturn.ReturnCode.SUCCESS) {
            return new RelocationResponse(true, ret.getCode().toString());
        } else {
            return new RelocationResponse(false, ret.getCode().toString());
        }
    }
}
