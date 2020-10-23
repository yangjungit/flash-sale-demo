package com.giovanny.flashsaledemo.entity.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @packageName: com.giovanny.flashsaledemo.entity.dto
 * @className: TestLom
 * @description: @Builder 不会生成get set方法，会覆盖无参构造方法 详细可以编译了查看
 * @author: YangJun
 * @date: 2020/9/22 11:12
 * @version: v1.0
 **/
@Builder
public class TestLom {
    private String var1;
    private Integer var2;
    private Date var3;
    private LocalDateTime var4;
    private List<String> var5;
    private Map<String, String> var6;

}
