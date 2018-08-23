package com.mine.angular.entity

import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RemoteHost {
    private final static Logger LOGGER = LoggerFactory.getLogger(RemoteHost.class);
    private final static int BUFFER_LEN = 1024;
    private final static int CONN_TIMEOUT = 3000;
    private final static int EXEC_TIMEOUT = 200;

    String ip;
    int port;
    String username;
    String password;

    private Session session;

    RemoteHost(String ip, String username, String password, int port = 22) {
        this.ip = ip;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    def connect(timeout = CONN_TIMEOUT * 5) {
        if (!this.session || !this.session.isConnected()) {
            this.session = new JSch().getSession(this.username, this.ip, this.port);
            this.session.setPassword(this.password);
            session.setConfig("StrictHostKeyChecking", "no");
            try {
                this.session.connect(timeout);
                LOGGER.info("Connected session {}.", this.ip);
            } catch (ex) {
                LOGGER.error("Connect to {} error!", this.ip);
            }
        }
    }

    def disconnect() {
        try {
            !this.session.isConnected() ?: this.session.disconnect();
            LOGGER.info("Disconnected session {}.", this.ip);
        } catch (ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    /**
     * 执行shell命令
     * @param cmd
     * @return
     */
    def exec(String cmd) {
        def command = this.session.openChannel("exec");
        command.setCommand(cmd);
        command.connect(CONN_TIMEOUT);
        LOGGER.info("Connected ssh {}.", this.ip);

        InputStream ins = command.getInputStream();
        StringBuilder builder = new StringBuilder();
        byte[] data = new byte[BUFFER_LEN];
        while (true) {
            while (ins.available() > 0) {
                int len = ins.read(data, 0, BUFFER_LEN);
                if (len < 0) break;
                builder.append(new String(data, 0, len));
            }
            if (command.isClosed()) {
                if (ins.available() > 0) continue;
                break;
            }

            Thread.sleep(EXEC_TIMEOUT);
        }

        command.disconnect();
        LOGGER.info("Disconnected ssh {}.", this.ip);

        return builder.toString();
    }

    /**
     * 下载文件到本地目录
     * @param from , 远端文件，绝对路径
     * @param to
     */
    def downloadFile(String from, String to) {
        def sftp = this.session.openChannel("sftp");
        sftp.connect(CONN_TIMEOUT);
        LOGGER.info("Connected sftp {}.", this.ip);

        if (from.endsWith("/") || from.lastIndexOf("/") == -1) {
            throw new IllegalArgumentException("Bad path: $from.");
        }
        sftp.cd(from.substring(0, from.lastIndexOf("/")));
        File file = new File(to);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        sftp.get(from.substring(from.lastIndexOf("/") + 1), fileOutputStream);
        fileOutputStream.close();

        sftp.disconnect();
        LOGGER.info("Disconnected sftp {}.", this.ip);

        return file;
    }

    /**
     * 上传文件
     * @param from
     * @param to , 远端文件，绝对路径
     */
    def uploadFile(String from, String to) {
        def sftp = this.session.openChannel("sftp");
        sftp.connect(CONN_TIMEOUT);
        LOGGER.info("Connected sftp {}.", this.ip);

        if (to.endsWith("/") || to.lastIndexOf("/") == -1) {
            throw new IllegalArgumentException("Bad path: $to.");
        }
        sftp.cd(to.substring(0, to.lastIndexOf("/")));
        File file = new File(from);
        FileInputStream fileInputStream = new FileInputStream(file);
        sftp.put(fileInputStream, to.substring(to.lastIndexOf("/") + 1));
        fileInputStream.close();

        sftp.disconnect();
        LOGGER.info("Disconnected sftp {}.", this.ip);

        return true;
    }

    /**
     * 删除文件
     * @param path , 远端文件，绝对路径
     */
    def deleteFile(String path) {
        def sftp = this.session.openChannel("sftp");
        sftp.connect(CONN_TIMEOUT);
        LOGGER.info("Connected sftp {}.", this.ip);

        if (path.endsWith("/") || path.lastIndexOf("/") == -1) {
            throw new IllegalArgumentException("Bad path: $path.");
        }
        sftp.cd(path.substring(0, path.lastIndexOf("/")));
        sftp.rm(path.substring(path.lastIndexOf("/") + 1));

        sftp.disconnect();
        LOGGER.info("Disconnected sftp {}.", this.ip);
    }

}
