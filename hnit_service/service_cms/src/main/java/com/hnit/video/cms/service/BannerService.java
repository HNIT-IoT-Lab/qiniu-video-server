package com.hnit.video.cms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hnit.video.model.entity.cms.Banner;
import com.hnit.video.model.vo.cms.BannerQueryCondition;

import java.util.List;

/**
 * <p>
 * 首页banner表 服务类
 * </p>
 *
 * @author King Gigi
 * @since 2022-07-25
 */
public interface BannerService extends IService<Banner> {
    // 分页查询banner信息
    Page<Banner> pageQueryBanner(Long index, Long limit, BannerQueryCondition queryCondition);

    // 获取所有的未被禁止的banner数据
    List<Banner> getAllBanner();
}
