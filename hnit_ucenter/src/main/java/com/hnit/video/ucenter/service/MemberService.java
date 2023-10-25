package com.hnit.video.ucenter.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hnit.model.entity.ucenter.Member;
import com.hnit.model.vo.ucenter.admin.MemberQueryCondition;
import com.hnit.model.vo.ucenter.user.LoginVO;
import com.hnit.model.vo.ucenter.user.PasswordVO;
import com.hnit.model.vo.ucenter.user.RegisterVO;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author King Gigi
 * @since 2022-07-25
 */
public interface MemberService extends IService<Member> {
    // 用户登录
    String userLogin(LoginVO loginVO);

    // 用户注册
    void userRegister(RegisterVO registerVO);

    // 修改密码
    void updatePassword(PasswordVO passwordVO, Member userMember);

    // 注册人数统计
    int registerCount(String day);

    // 根据openId获取用户
    Member getMemberByOpenId(String openid);

    // 分页查询用户信息
    Page<Member> pageQueryMember(Long index, Long limit, MemberQueryCondition queryCondition);
}
