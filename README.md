# angular-spring

### 说明
* backend/src/main/resources/static一般用于放置spring-boot项目的静态文件，而此项目结构放置frontend编译后的静态文件，因为编译后文件的动态性，所以不对此目录的文件进行版本跟踪。本项目结构特别适用于ci，在项目根目录下mvn install会自动将frontend的编译文件拷贝于此并打成可运行jar包。

### 开发过程
1. IDE启动backend main.class或者java -jar backend-XX.jar开启后台服务，默认监听8080端口
2. 在frontend目录进行npm run start运行前台开发服务器，前台开发服务器运行在4200端口, 并访问8080端口的后台服务，通过4200端口可对应用进行调测

### 生产部署(自动)
* 项目根目录下直接mvn [clean] install进行编译和打包，运行jar包即启动web应用

### 生产部署(手动)
1. frontend目录下npm run build-prod或者npm run build进行前台编译打包
2. backend目录下mvn clean package进行编译和打包，打包过程会将前台编译文件拷贝到static目录下
3. 运行jar包即启动web应用

### 行为概述
* 启动jar时，会在jar包所在目录生成redis目录，本目录放置redis程序、配置、日志以及数据文件，如果要让数据持久则不要删除rdb和aof文件，下次启动jar时还会恢复保存在其中的数据
* 启动jar时，会有两次用户授权提示，其中第一次是安装redis服务，第二次是启动redis服务，redis的服务名注册为AssistantRedis，端口为7979(这些一般不会和机器上现有实例冲突)
* 使用Ctrl+C关闭jar时，会有两次用户授权提示，其中第一次是停止redis服务，第二次是卸载名为AssistantRedis的redis服务
* 直接关闭运行jar的窗口，此时无法检测到程序退出，所以无法对redis服务进行清理，想要卸载redis服务的话可以重新启动jar，并Ctrl+C关闭之

### 非pom依赖
* Redis-x64-3.2.100 WIN版Redis