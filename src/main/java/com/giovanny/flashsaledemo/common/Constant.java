package com.giovanny.flashsaledemo.common;

/**
 * @packageName: com.giovanny.flashsaledemo.common
 * @className: Constant
 * @description: 常量
 * @author: YangJun
 * @date: 2020/10/23 10:26
 * @version: v1.0
 **/
public class Constant {
    public interface RegexStr {
        //name限制正则 2 - 20个字符
        String NAME_REGEX = "^.{2,20}$";
        //身份证号码检查正则 18位
        String ID_NUM_REGEX = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
        //手机号检测正则 1开头 第二位2到9
        String PHONE_REGEX = "^1[2-9]\\d{9}$";
    }
}
