## 1、Caused by: java.lang.OutOfMemoryError: GC overhead limit exceeded

GC overhead limt exceed检查是Hotspot VM 1.6定义的一个策略，通过统计GC时间来预测是否要OOM了，提前抛出异常，防止OOM发生。Sun 官方对此的定义是：“并行/并发回收器在GC回收时间过长时会抛出OutOfMemroyError。过长的定义是，超过98%的时间用来做GC并且回收 了不到2%的堆内存。用来避免内存过小造成应用不能正常工作

一般解决：加大Heap Size（-Xmx1024m等）  关闭JVM这个默认的策略（ 不推荐  -XX:-UseGCOverheadLimit）

OOM问题可以用JProfile去查看

也可以用Java自带的jstat等工具去查看

## 2、mysql遇见Expression #1 of SELECT list is not in GROUP BY clause and contains nonaggre的问题

```
Caused by: java.sql.SQLSyntaxErrorException: 
Expression #1 of SELECT list is not in GROUP BY clause and contains nonaggregated column 
'secrecy_fs.apply_process.id' which is not functionally dependent on columns in GROUP BY clause; 
this is incompatible with sql_mode=only_full_group_by
```

————————————————

以上错误信息。

问题出现的原因：
MySQL 5.7.5及以上功能依赖检测功能。如果启用了ONLY_FULL_GROUP_BY SQL模式（默认情况下），MySQL将拒绝选择列表，HAVING条件或ORDER BY列表的查询引用在GROUP BY子句中既未命名的非集合列，也不在功能上依赖于它们。（5.7.5之前，MySQL没有检测到功能依赖关系，默认情况下不启用ONLY_FULL_GROUP_BY。有关5.7.5之前的行为的说明，请参见“MySQL 5.6参考手册”。）

**解决:**

select @@global.sql_mode

set @@global.sql_mode
='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';

set @@global.sql_mode
='STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION';

修改MySQL配置文件my.ini 或者mysql.conf

sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION