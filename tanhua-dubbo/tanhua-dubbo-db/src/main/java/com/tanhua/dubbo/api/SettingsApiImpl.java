package com.tanhua.dubbo.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.dubbo.mappers.SettingsMapper;
import com.tanhua.model.domain.Settings;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class SettingsApiImpl implements SettingsApi{

    @Autowired
    private SettingsMapper settingsMapper;

    @Override
    public Settings findByUserId(Long id) {
        QueryWrapper<Settings> qw = new QueryWrapper<>();
        qw.eq("user_id",id);
        return settingsMapper.selectOne(qw);
    }

    @Override
    public void save(Settings settings) {
        settingsMapper.insert(settings);
    }

    @Override
    public void update(Settings settings) {
        settingsMapper.updateById(settings);
    }
}
