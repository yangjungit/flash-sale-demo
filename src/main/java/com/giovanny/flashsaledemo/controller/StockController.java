package com.giovanny.flashsaledemo.controller;


import com.giovanny.flashsaledemo.service.IStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping("flash/sale")
public class StockController {
    @Autowired
    private IStockService stockService;

    @PostMapping(path = "order")
    public String flashSale(Long goodsId, Integer count) {
        boolean isOrder = stockService.goodsOrder(goodsId, count);
        if (isOrder) {
            return "success";
        } else {
            return "此商品秒杀结束";
        }
    }

    @GetMapping(path = "test")
    public String test() {
        try {
            stockService.addGoods();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }


}
