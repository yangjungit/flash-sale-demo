# flash-sale-demo

## 商品秒杀DEMO
**商品库存PO**，数据库表列有以下字段及id字段，id字段为了简便就用的数据库自增

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
**controller**

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
**service**

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
**项目启动时会初始化商品库存到Redis**，保存成hash,key:商品ID，value:商品库存 

如：["1":100,"2":100],

设置秒杀时间为5分钟

根据实际情况可以变化初始化库存到redis的操作，多节点的时候，这个是初始化可能需要去抢锁，只允许一个节点初始化或者怎么分配初始化。

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

GlobalExceptionAdvice.java

@RestControllerAdvice 切面注解

@ExceptionHandler(value={xx.class,xxx.class})  异常处理，value为处理哪些异常

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvice {

    @ExceptionHandler(value = {
            BindException.class,
            MethodArgumentNotValidException.class
    })
    public MyResponse handle(BindException e) {
        log.error("check system error :", e);
        StringBuilder stringBuilder = new StringBuilder();
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        fieldErrors.forEach(
                fieldError -> {
                    stringBuilder
                            .append(fieldError.getField())
                            .append(":")
                            .append(fieldError.getDefaultMessage())
                            .append(System.lineSeparator());
                }
        );

        return MyResponse.failed(HttpStatus.BAD_REQUEST.value(), stringBuilder.toString());
    }


    @ExceptionHandler(value = ConstraintViolationException.class)
    public MyResponse handle(ConstraintViolationException e) {
        log.error("check system error :", e);
        StringBuilder stringBuilder = new StringBuilder();

        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        constraintViolations.forEach(
                constraintViolation -> {
                    stringBuilder
                            .append(constraintViolation.getInvalidValue())
                            .append(":")
                            .append(constraintViolation.getMessage())
                            .append(System.lineSeparator());
                }
        );
        return MyResponse.failed(HttpStatus.BAD_REQUEST.value(), stringBuilder.toString());
    }

    @ExceptionHandler(value = BaseRuntimeException.class)
    public MyResponse handle(BaseRuntimeException e) {
        log.error("check system error :", e);
        return MyResponse.failed(e.getResultCode().getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public MyResponse handle(Exception e) {
        log.error("check system error :", e);
        return MyResponse.failed(e.getMessage());
    }
}
```

自定义异常

```java
@Getter
public class BaseRuntimeException extends RuntimeException {

    private IResultCode resultCode;

    public BaseRuntimeException(IResultCode resultCode) {
        super();
        this.resultCode = resultCode;
    }

    public BaseRuntimeException(Throwable cause) {
        super(cause);
    }

    public BaseRuntimeException(Throwable cause, IResultCode resultCode) {
        super(cause);
        this.resultCode = resultCode;
    }
}
```

自定义状态码IResultCode

IResultCode接口

```java
public interface IResultCode {
    int getErrorCode();

    String getErrorMessage();
}
```

IResultCode实现类 枚举类型

```java
public enum ResultCode implements IResultCode{
    /**
     * 自定义code及信息
     */
    SYSTEM_LOGIC_ERROR(9001, "系统错误"),
    RPC_ERROR(9002, "远程服务调用失败"),
    AUTHENTICATION_ERROR(9003, "验证失败 请重新登录"),
    SESSION_INVALID(9004, "session失效 请重新登录"),

    VERIFY_SIGN_HEAD_MISS(9101, "sign api head param miss"),
    VERIFY_SIGN_PUBLIC_KEY_MISS(9102, "sign api public key miss"),
    VERIFY_SIGN_FAIL(9103, "sign api check error"),

    XSTORE_UPLOAD_FAIL(9201, "xstore upload error"),
    XSTORE_DOWNLOAD_FAIL(9202, "xstore download error"),
    ;

    private int code;
    private String desc;

    ResultCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public int getErrorCode() {
        return this.code;
    }

    @Override
    public String getErrorMessage() {
        return this.desc;
    }
}
```

统一返回对象

```java
@Data
@Slf4j
public class MyResponse {
    private int code;
    private String message;
    private Object data;

    public MyResponse() {
    }

    public MyResponse(int code) {
        this.code = code;
    }

    public MyResponse(Object data) {
        this.code = 200;
        this.data = data;
    }

    public MyResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public MyResponse(String message, Object data) {
        this.code = 200;
        this.message = message;
        this.data = data;
    }

    public MyResponse(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static void responseJson(ServletResponse response, MyResponse myResponse) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            out = response.getWriter();
            out.println(JSON.toJSONString(myResponse));
        } catch (Exception e) {
            log.error("【JSON输出异常】" + e);
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }


    public static MyResponse success() {
        return new MyResponse(200);
    }

    public static MyResponse success(String message, Object data) {
        return new MyResponse(message, data);
    }

    public static MyResponse success(Object data) {
        return new MyResponse(data);
    }

    public static MyResponse failed(int code, String message) {
        return new MyResponse(code, message);
    }

    public static MyResponse failed(String message) {
        return new MyResponse(500, message);
    }

    public static MyResponse failed(int code, String message, Object data) {
        return new MyResponse(code, message, data);
    }
}
```



## 文件模糊hash计算

获取文件模糊hash

```java
FileUtil.java
public static String getFileHash(String fileType, InputStream is);
```

文件模糊hash比较

````java
Ssdeep.java
public int Compare(String signa1, String signa2)
````

## logback 日志输出

1. 日志输出到文件并根据`LEVEL`级别将日志分类保存到不同文件
2. 通过异步输出日志减少磁盘`IO`提高性能
3. 异步输出日志的原理

Spring Boot工程自带logback和slf4j的依赖，所以重点放在编写配置文件上，需要引入什么依赖，日志依赖冲突统统都不需要我们管了。
logback框架会默认加载classpath下命名为logback-spring或logback的配置文件。所以重点放在编写配置文件上，需要引入什么依赖，日志依赖冲突统统都不需要我们管了。

这里贴出来的是异步输出日志，不要异步输出就去掉异步输出后的两个`appender`

`logback`框架会默认加载`classpath`下命名为`logback-spring`或`logback`的配置文件。

配置`logback-spring.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<configuration>
    <springProperty name="LOG_HOME" source="log.path"/>
    <springProperty name="LOG_LEVEL" source="log.level"/>
    <springProperty name="LOG_IP" source="LOG_IP"/>
    <springProperty name="PROJECT_NAME" source="log.project-name"/>

    <appender name="CONSOLE-LOG" class="ch.qos.logback.core.ConsoleAppender">
        <layout class="ch.qos.logback.classic.PatternLayout">
            <pattern>[%d{yyyy-MM-dd' 'HH:mm:ss.sss}] [%C] [%t] [%L] [%-5p] %m%n</pattern>
        </layout>
    </appender>
    <!--获取比info级别高(包括info级别)但除error级别的日志-->
    <appender name="INFO-LOG" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>DENY</onMatch>
            <onMismatch>ACCEPT</onMismatch>
        </filter>
        <encoder>
            <pattern>[%d{yyyy-MM-dd' 'HH:mm:ss.sss}] [%t] [%C] [%L] [%-5p] %msg%n</pattern>
        </encoder>

        <!--滚动策略-->
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!--路径-->
            <fileNamePattern>${LOG_HOME}/${PROJECT_NAME}/${LOG_IP}-%d-info-%i.log</fileNamePattern>
            <maxHistory>7</maxHistory>
            <maxFileSize>10 MB</maxFileSize>
        </rollingPolicy>
    </appender>
    <appender name="ERROR-LOG" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>ERROR</level>
        </filter>
        <encoder>
            <pattern>[%d{yyyy-MM-dd' 'HH:mm:ss.sss}] [%t] [%C] [%L] [%-5p] %msg%n</pattern>
        </encoder>
        <!--滚动策略-->
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!--路径-->
            <fileNamePattern>${LOG_HOME}/${PROJECT_NAME}/${LOG_IP}-%d-err-%i.log</fileNamePattern>
            <maxHistory>7</maxHistory>
            <maxFileSize>10 MB</maxFileSize>
        </rollingPolicy>
    </appender>

    <!-- 异步输出 -->
    <appender name="ASYNC-INFO" class="ch.qos.logback.classic.AsyncAppender">
        <!-- 不丢失日志.默认的,如果队列的80%已满,则会丢弃TRACT、DEBUG、INFO级别的日志 -->
        <discardingThreshold>0</discardingThreshold>
        <!-- 更改默认的队列的深度,该值会影响性能.默认值为256 -->
        <queueSize>256</queueSize>
        <!-- 添加附加的appender,最多只能添加一个 -->
        <appender-ref ref="INFO-LOG"/>
    </appender>

    <appender name="ASYNC-ERROR" class="ch.qos.logback.classic.AsyncAppender">
        <!-- 不丢失日志.默认的,如果队列的80%已满,则会丢弃TRACT、DEBUG、INFO级别的日志 -->
        <discardingThreshold>0</discardingThreshold>
        <!-- 更改默认的队列的深度,该值会影响性能.默认值为256 -->
        <queueSize>256</queueSize>
        <!-- 添加附加的appender,最多只能添加一个 -->
        <appender-ref ref="ERROR-LOG"/>
    </appender>

    <root level="${LOG_LEVEL}">
        <appender-ref ref="CONSOLE-LOG" />
        <appender-ref ref="INFO-LOG" />
        <appender-ref ref="ERROR-LOG" />
    </root>
</configuration>
```

xml用到spring boot配置文件中的属性：

```yaml
log:
  path: /log
  level: info
  project-name: flash-sale-demo
```

## mybatis plus generator

```java

/**
 * @packageName: MyBatisPlusGenerator
 * @className: Generator
 * @description: //Generator
 * @author: YangJun
 * @date: 2020/5/14 11:09
 * @version: v1.0
 **/
public class MyBatisPlusGenerator {

    public static String scanner(String something) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入" + something + "：");
        if (scanner.hasNext()) {
            String sc = scanner.next();
            if (!StringUtils.isBlank(sc)) {
                return sc;
            }
        }
        throw new MybatisPlusException("请输入正确的" + something + "！");
    }

    public static void main(String[] args) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor("YangJun");
        gc.setOpen(false);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://localhost:3306/flash_sale?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&allowMultiQueries=true&&useSSL=true&serverTimezone=Asia/Shanghai");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("123456");
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        //scanner("请输入你的包名")
        pc.setModuleName("flashsaledemo");
        //你哪个父目录下创建包
        pc.setParent("com.giovanny");
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        // 如果模板引擎是 freemarker
        String templatePath = "/templates/mapper.xml.ftl";
        // 如果模板引擎是 velocity
        // String templatePath = "/templates/mapper.xml.vm";

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/resources/mapper/" + pc.getModuleName()
                        + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        // templateConfig.setEntity("templates/entity2.java");
        // templateConfig.setService();
        // templateConfig.setController();

        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);

        // 策略配置,数据库表配置
        StrategyConfig strategy = new StrategyConfig();
        //数据库表映射到实体的命名策略
        strategy.setNaming(NamingStrategy.underline_to_camel);
        //数据库表字段映射到实体类的命名策略
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        //自定义继承entity类，添加这一个会在生成实体类的时候继承entity
        //strategy.setSuperEntityClass("com.wy.testCodeGenerator.entity");
        //实体是否为lombok模型
        strategy.setEntityLombokModel(true);
        //生成@RestController控制器
        strategy.setRestControllerStyle(true);
        //是否继承controller
        // strategy.setSuperControllerClass("com.wy.testCodeGenerator.controller");
        //scanner("请输入表名（多个用‘,’分隔）：").split(",")   "file","apply_process","point","scan_result",
        strategy.setInclude("stock");
        strategy.setSuperEntityColumns("id");
        strategy.setSuperEntityClass(BaseEntity.class);
        //驼峰转连字符串
        strategy.setControllerMappingHyphenStyle(true);
        //表前缀
        strategy.setTablePrefix(pc.getModuleName() + "_");
        mpg.setStrategy(strategy);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }
}
```

