# tms
电话会议管理系统(Teleconference management system)

## 前提条件
- [Git 1.9+](http://git-scm.com/downloads)
- [JDK 1.8+](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
- [maven 3.3+](http://maven.apache.org/download.cgi/)
- [nvm 1.1+](https://github.com/creationix/nvm)

### 前提条件 - 环境配置
执行以下命令,确认版本符合前提条件中指定的要求
```SHELL
git --version
java -version
mvn --version
nvm version
```



### 前提条件 - node
从 http://coreybutler.github.io/nodedistro/ 查询可供使用的node版本(建议使用node 4.2+)
执行以下命令配置node环境
```SHELL
nvm install 4.2.4
nvm use 4.2.4
nvm on
```

### 前提条件 - 依赖组件
执行以下命令安装依赖组件
```SHELL
npm install -g bower gulp karma-cli
npm install
bower install
gulp
```
提示: 在中国内地可以使用--registry参数来指定taobao的镜像来执行npm组件的安装
```SHELL
npm install -g bower gulp karma-cli --registry=http://registry.npm.taobao.org
npm install --registry=http://registry.npm.taobao.org
```

### 检测是否缺少依赖组件
随着开发的进展,其它开发人员会添加新的依赖项,如果缺少依赖项,程序就无法正常工作
执行以下命令检测是否缺少依赖项
```SHELL
npm ls --depth=0 #检测packages.json依赖
bower ls -o #检测boweer.json依赖
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
