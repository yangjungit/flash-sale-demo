# flash-sale-demo
## 商品秒杀DEMO
商品库存PO，数据库表列有以下字段及id字段，id字段为了简便就用的数据库自增

```java
@Data
public class Stock extends BaseEntity {

    /**
     * 名字
     */
    private String name;

    /**
     * 库存
     */
    private Long stock;

    private String version;
    
}
```
controller

主要关注/flash/sale/order这个请求，收到订单请求后调用service处理
```java
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
```
service

此处省略接口直接贴实现类。首先查询商品库存，订单订购商品数量与库存对比判断是否可以下单，可以即发送消息

这里为了简单就用的Redis作为消息队列，开发中可以用较专业成熟的RabbitMQ作为消息队列
```java
@Service
@Slf4j
public class StockServiceImpl extends ServiceImpl<StockMapper, Stock> implements IStockService {

    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Override
    public boolean goodsOrder(Long goodsId, Integer count) {
        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
        Integer stock = (Integer) opsForHash.get(StockInit.GOODS_STOCK_KEY, goodsId);
        if (stock == null) {
            log.info("redis 此商品[{}]秒杀已结束。stock:{}", goodsId, stock);
            return false;
        }
        if (stock <= 0 || stock < count) {
            log.info("redis 库存已不充足。stock:{},count:{}", stock, count);
            return false;
        }
        Long lastStock = opsForHash.increment(StockInit.GOODS_STOCK_KEY, goodsId, -count);
        log.info("剩余库存 lastStock:{}", lastStock);
        Map<String, Object> map = new HashMap<>(8);
        map.put("id", goodsId);
        map.put("count", count);
        redisTemplate.convertAndSend("redis-topic", map);
        return true;
    }

    @Override
    public void countDown(Long goodsId, Integer count) {
        stockMapper.countDownStock(goodsId, count);
    }
}
```
项目启动时会初始化商品库存到Redis，保存成hash,key:商品ID，value:商品库存 

如：["1":100,"2":100],

设置秒杀时间为5分钟
```java
@Component
@Slf4j
public class StockInit implements InitializingBean {
    @Autowired
    private IStockService stockService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public final static String GOODS_STOCK_KEY = "GOODS_STOCK_KEY";

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("----初始化商品库存，将MySQL中的商品库存保存到Redis");
        Map<Long, Long> stockMap = stockService.list()
                .parallelStream()
                .collect(Collectors.toMap(Stock::getId, Stock::getStock));
        HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
        opsForHash.putAll(GOODS_STOCK_KEY, stockMap);
        redisTemplate.expire(GOODS_STOCK_KEY, 5, TimeUnit.MINUTES);
    }
}
```
消息监听。获取队列消息，盘对处理，并生成订单，减少MySql中的库存数量

TODO：打算用websocket的方式将秒杀下单成功的消息推送给用户
```java
@Service
@Slf4j
public class Receiver implements MessageListener {
    @Autowired
    private StockServiceImpl stockService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // redis队列的方式不是很好 很容易丢失消息，也可能是我没有配置好，感觉用rabbitmq会好一点
        log.info("message:{}", message);
        log.info("pattern:{}", pattern);
        //减库存，生成订单
        String msg = message.toString();
        JSONObject object = JSONObject.parseObject(msg);
        Integer goodsId = (Integer) object.get("id");
        Integer count = (Integer) object.get("count");
        stockService.countDown(Long.valueOf(goodsId), count);
    }
}
```
简单减少库存的SQL
```sql
<update id="countDownStock">
    update stock
    set stock=stock - #{count}
    where id=#{goodsId}
</update>
```

## 统一异常处理DEMO

## 文件模糊hash计算

获取文件模糊hash

```java
FileUtil.java
public static String getFileHash(String fileType, InputStream is);
```

文件模糊hash比较

````java
Ssdeep
public int Compare(String signa1, String signa2)
````

