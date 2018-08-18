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