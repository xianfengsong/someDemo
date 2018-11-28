package com.miui.global.browser.module.ad.interstitial.dao.mapper;

import com.miui.global.browser.module.ad.interstitial.dao.model.InterstitialAdDO;
import com.miui.global.browser.module.ad.interstitial.dao.model.InterstitialAdDOCriteria;
import java.util.List;

public interface InterstitialAdMapper {
    List<InterstitialAdDO> selectByExample(InterstitialAdDOCriteria example);

    InterstitialAdDO selectByPrimaryKey(Long id);
}