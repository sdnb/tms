package cn.snzo.service.impl;

import cn.snzo.entity.SysSetting;
import cn.snzo.repository.SysSettingRepository;
import cn.snzo.service.ISysSettingService;
import cn.snzo.utils.BeanUtil;
import cn.snzo.vo.SysSettingShow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2017/7/4 0004.
 */
@Service
public class SysSettingService implements ISysSettingService {

    @Autowired
    private SysSettingRepository sysSettingRepository;
    @Override
    public int add(SysSettingShow sysSettingShow) {
        SysSetting sysSetting = new SysSetting();
        BeanUtil.showToEntity(sysSettingShow, sysSetting);
        sysSettingRepository.save(sysSetting);
        return 0;
    }



    @Override
    public int modify(int id, SysSettingShow sysSettingShow) {
        SysSetting check = sysSettingRepository.findOne(id);
        if (check == null)
            return 2;
        SysSetting sysSetting = new SysSetting();
        BeanUtil.showToEntity(sysSettingShow, sysSetting);
        sysSettingRepository.save(sysSetting);
        return 1;
    }
}
