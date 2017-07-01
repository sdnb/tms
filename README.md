# tms
电话会议管理系统(Teleconference management system)

## 前提条件
- [Git 1.9+](http://git-scm.com/downloads)
- [JDK 1.8+](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
- [mvn 3.3+](http://maven.apache.org/download.cgi/)

### 前提条件 - 环境配置
执行以下命令,确认版本符合前提条件中指定的要求
```SHELL
git --version
java -version
mvn --version
```




## 配置文件
本项目使用的配置文件位于
- [$/src/main/resources/application.yml](https://github.com/HP-Enterprise/Rental653/blob/dev/src/main/resources/application.yml)
- 默认激活dev配置,因此,可以在`$/src/*/resources/`下创建一个名为`application-dev.yml`的配置文件,按自己的需要重载配置项
- 也可以通过定义一个名为spring.profiles.active的系统属性来指定激活的配置,例如:
```SHELL
mvn -Dspring.profiles.active=product spring-boot:run
```
- 那么直接运行时 $/src/main/resources/application-product.yml 将被激活.
- 单元测试时 $/src/test/resources/application-product.yml 将被激活.
- 没有在`application-product.yml`里定义的配置,会继承`application.yml`里的定义.


## 数据库初始化命令
创建数据库和用户
```SHELL
mysql -u root -p -h 127.0.0.1 -e 'CREATE DATABASE rental CHARACTER SET = utf8;'
mysql -u root -p -h 127.0.0.1 -e 'CREATE USER javapp@localhost IDENTIFIED BY 'p@ssw0rd';'
mysql -u root -p -h 127.0.0.1 -e 'GRANT ALL ON rental.* TO javapp@localhost;'
mysql -u root -p -h 127.0.0.1 -e 'GRANT FILE ON *.* TO javapp@localhost;'
```

数据库初始化需要在`$/gradle.properties`中配置数据库连接,请参考示例文件`$/gradle-sample.properties`
```SHELL
    mvn flyway:migrate #迁移数据库并且自动创建数据库的表
    mvn flyway:info #打印所有迁移的表的详细信息和状态信息
    mvn flyway:clean #删除数据库中所有的表
```
可在 http://flywaydb.org/documentation/gradle/ 链接查看更多具体用法

### flyway创建SQL脚本的文件命名规则
```
V<VERSION>__<NAME>.sql，<VERSION>可以写成1 或者 2_1或者3.1
<VERSION>规定写成日期.序号,例如:20170113.1
```

## 运行
```SHELL
mvn spring-boot:run
```
