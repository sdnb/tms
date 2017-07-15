package cn.snzo.service;

import cn.snzo.vo.AccountShow;
import cn.snzo.vo.SysSettingShow;

/**
 * Created by Administrator on 2017/7/4 0004.
 */
public interface ISysSettingService {

    int add(SysSettingShow sysSettingShow);

    int modify(int id, SysSettingShow sysSettingShow);

    int modifyPwd(AccountShow accountShow);

    SysSettingShow getLatestSetting();

}
