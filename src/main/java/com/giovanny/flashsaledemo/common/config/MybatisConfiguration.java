package com.giovanny.flashsaledemo.common.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @packageName: MybatisConfiguration
 * @className: MybatisConfiguration
 * @description: MybatisConfiguration
 * @author: YangJun
 * @date: 2020/5/14 11:05
 * @version: v1.0
 **/
@Configuration
@MapperScan(basePackages = {"com.giovanny.flashsaledemo.mapper"})
public class MybatisConfiguration {



    /**
     * 分页插件，自动识别数据库类型
     * 多租户，请参考官网【插件扩展】
     *
     * @return PaginationInterceptor
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

   /*
    * oracle数据库配置JdbcTypeForNull
    * 参考：https://gitee.com/baomidou/mybatisplus-boot-starter/issues/IHS8X
    不需要这样配置了，参考 yml:
    mybatis-plus:
      confuguration
        dbc-type-for-null: 'null'
   @Bean
   public ConfigurationCustomizer configurationCustomizer(){
       return new MybatisPlusCustomizers();
   }

   class MybatisPlusCustomizers implements ConfigurationCustomizer {

       @Override
       public void customize(org.apache.ibatis.session.Configuration configuration) {
           configuration.setJdbcTypeForNull(JdbcType.NULL);
       }
   }
   */

}


