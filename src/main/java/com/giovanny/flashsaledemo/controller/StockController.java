package com.giovanny.flashsaledemo.controller;


import com.giovanny.flashsaledemo.service.IStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author YangJun
 * @since 2020-06-15
 */
@RestController
@RequestMapping("/flash/sale")
public class StockController {
    @Autowired
    private IStockService stockService;

    @PostMapping(path = "/order")
    public String flashSale(@RequestBody Long goodsId, @RequestBody Integer count) {
        stockService.goodsOrder(goodsId,count);
        return "success";
    }


}
