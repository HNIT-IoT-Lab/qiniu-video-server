//package cn.hnit.starter.config;
//
//import cn.hnit.starter.intercept.ban.*;
//import cn.hnit.starter.intercept.interceptor.BanUserInterceptor;
//import cn.hnit.utils.starter.ApplicationContextHolder;
//import org.springframework.boot.autoconfigure.AutoConfigureOrder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@AutoConfigureOrder(0)
//public class BanAutoConfiguration {
//    @Bean
//    public BanChainNode banUserChainNode(){
//        return new BanUserChainNode();
//    }
//    @Bean
//    public BanChainNode banDeviceChainNode(){
//        return new BanDeviceChainNode();
//    }
//
//    @Bean
//    public BanChainNode banIpChainNode(){
//        return new BanIpChainNode();
//    }
//
//    @Bean
//    public BanChainNode banForeignIpChainNode(){
//        return new BanForeignIpChainNode();
//    }
//
//    /**
//     * ---------------------------------白名单规则配置--------------------------------
//     */
//    @Bean
//    public WhitelistChainNode whitelistDeviceChainNode(){
//        return new WhitelistDeviceChainNode();
//    }
//    @Bean
//    public WhitelistChainNode whitelistIpChainNode(){
//        return new WhitelistIpChainNode();
//    }
//    @Bean
//    public WhitelistChainNode whitelistUserChainNode(){
//        return new WhitelistUserChainNode();
//    }
//
//    @Bean
//    public BanUserInterceptor banUserInterceptor(){
//        return new BanUserInterceptor();
//    }
//
//    @AutoConfigureOrder(0)
//    @Bean
//    public ApplicationContextHolder myApplicationHolder(){
//        return new ApplicationContextHolder();
//    }
//}
