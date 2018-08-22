package com.mine.angular.redis

import com.mine.angular.tools.ClassResolver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Service

@Service
class RedisService implements ApplicationRunner, DisposableBean {
    private final static Logger LOGGER = LoggerFactory.getLogger(RedisService.class);

    private final static REDIS_SVC_NAME = "AssistantRedis";

    private final static RESOURCE_DIR = "src/main/resources/redis";
    // 之所以使用StringBuilder是因为使用了Groovy惰性求值的特点，此值可根据是否在jar中运行来更新redis的运行路径
    private static StringBuilder REDIS_DIR = new StringBuilder("redis");

    private final static REDIS_PATH = "$REDIS_DIR/redis-server.exe";
    private final static REGISTER_REDIS = "$REDIS_PATH --service-install --service-name $REDIS_SVC_NAME redis.conf";
    private final static START_REDIS = "$REDIS_PATH --service-start --service-name $REDIS_SVC_NAME redis.conf";
    private final static STOP_REDIS = "$REDIS_PATH --service-stop --service-name $REDIS_SVC_NAME redis.conf";
    private final static UNREGISTER_REDIS = "$REDIS_PATH --service-uninstall --service-name $REDIS_SVC_NAME redis.conf";

    private final static QUERY_REDIS_STATUS = "SC QUERY AssistantRedis";

    @Autowired
    ClassResolver classResolver;

    @Override
    void run(ApplicationArguments args) throws Exception {
        // create run dir and exe/conf files
        def dir = classResolver.getClassRunLocation(this.getClass());
        if (!dir.endsWith(".jar")) {
            // 本地class运行，作为调试或者ci时使用此目录
            REDIS_DIR.replace(0, REDIS_DIR.length(), RESOURCE_DIR);
        } else {
            // 在jar中运行(说明不是开发环境了), 从jar拷贝文件到redis目录
            File redisRunDir = new File(REDIS_DIR.toString());
            redisRunDir.exists() ?: redisRunDir.mkdir();
            new File("${REDIS_DIR.toString()}/redis-server.exe").exists() ?: classResolver
                    .copyFileFromJar("/BOOT-INF/classes/redis/redis-server.exe", "${REDIS_DIR.toString()}/redis-server.exe");
            new File("${REDIS_DIR.toString()}/redis.conf").exists() ?: classResolver
                    .copyFileFromJar("/BOOT-INF/classes/redis/redis.conf", "${REDIS_DIR.toString()}/redis.conf");
        }

        def procStatus = QUERY_REDIS_STATUS.execute();
        procStatus.waitFor();
        if (procStatus.text.indexOf("1060") > -1) {
            // service is not installed, install first
            LOGGER.warn("Installing service $REDIS_SVC_NAME...");
            def procRegister = REGISTER_REDIS.execute(null, new File(REDIS_DIR.toString()));
            procRegister.waitFor();
            if (procRegister.exitValue() != 0) {
                LOGGER.error("Install service $REDIS_SVC_NAME Error, abort.");
                System.exit(procRegister.exitValue());
            } else {
                LOGGER.warn("Installed service $REDIS_SVC_NAME.");
            }
        }
        // start service
        LOGGER.warn("Starting service $REDIS_SVC_NAME...");
        def procStart = START_REDIS.execute(null, new File(REDIS_DIR.toString()));
        procStart.waitFor();
        if (procStart.exitValue() != 0) {
            LOGGER.error("Start service $REDIS_SVC_NAME Error, abort.")
            System.exit(procStart.exitValue());
        } else {
            LOGGER.warn("Started service $REDIS_SVC_NAME.")
        }
    }

    @Override
    void destroy() throws Exception {
        def procStatus = QUERY_REDIS_STATUS.execute();
        procStatus.waitFor();
        if (procStatus.text.indexOf("RUNNING") > -1) {
            // service is running, stop first
            LOGGER.warn("Stopping service $REDIS_SVC_NAME...");
            def procStop = STOP_REDIS.execute(null, new File(REDIS_DIR.toString()));
            procStop.waitFor();
            if (procStop.exitValue() != 0) {
                LOGGER.error("Stop service $REDIS_SVC_NAME Error.");
            } else {
                LOGGER.warn("Stopped service $REDIS_SVC_NAME.");
            }
        }
        // uninstall service
        LOGGER.warn("Uninstalling service $REDIS_SVC_NAME...");
        def procUnregister = UNREGISTER_REDIS.execute(null, new File(REDIS_DIR.toString()));
        procUnregister.waitFor();
        if (procUnregister.exitValue() != 0) {
            LOGGER.error("Uninstall service $REDIS_SVC_NAME Error.");
        } else {
            LOGGER.warn("Uninstalled service $REDIS_SVC_NAME.");
        }
    }
}
