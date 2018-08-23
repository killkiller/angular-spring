package com.mine.angular.entity

import com.jcraft.jsch.Channel
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RemoteHost {
    private final static Logger LOGGER = LoggerFactory.getLogger(RemoteHost.class);

    String ip;
    int port;
    String username;
    String password;

    private Session session;
    private Channel shell;
    private ChannelSftp sftp;

    RemoteHost(String ip, String username, String password, int port = 22) {
        this.ip = ip;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    def connect(timeout = 30000) {
        if (!this.session || !this.session.isConnected()) {
            this.session = new JSch().getSession(this.username, this.ip, this.port);
            this.session.setPassword(this.password);
            session.setConfig("StrictHostKeyChecking", "no");
            try {
                this.session.connect(timeout);
                LOGGER.info("Connected {}.", this.ip);

                this.shell = this.session.openChannel("shell");
                this.shell.connect(timeout);
                LOGGER.info("Connected ssh {}.", this.ip);

                this.sftp = this.session.openChannel("sftp");
                this.sftp.connect(timeout);
                LOGGER.info("Connected sftp {}.", this.ip);
            } catch (ex) {
                LOGGER.error("Connect to {} error!", this.ip);
            }
        }
    }

    def disconnect() {
        try {
            !this.sftp.isConnected() ?: this.sftp.disconnect();
            LOGGER.info("Disconnected sftp {}.", this.ip);
            !this.shell.isConnected() ?: this.shell.disconnect();
            LOGGER.info("Disconnected ssh {}.", this.ip);
            !this.session.isConnected() ?: this.session.disconnect();
            LOGGER.info("Disconnected {}.", this.ip);
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
        cmd += "\n"; // return hit
        def ins = this.shell.inputStream;
        def ous = this.shell.outputStream;

        ous.write(cmd.getBytes());
        ous.flush();

        if (ins.available() > 0) {
            byte[] data = new byte[ins.available()];
            try {
                int len = ins.read(data);
                if (len < 0) {
                    throw new IOException("Network error, cant get shell error.");
                }

                return new String(data, 0, len, "iso8859-1")
            } finally {
                ins.close();
                ous.close();
            }
        }

    }

    /**
     * 下载文件到本地目录
     * @param from , 远端文件，绝对路径
     * @param to
     */
    def downloadFile(String from, String to) {
        if (from.endsWith("/") || from.lastIndexOf("/") == -1) {
            throw new IllegalArgumentException("Bad path: $from.");
        }
        sftp.cd(from.substring(0, from.lastIndexOf("/")));
        File file = new File(to);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        sftp.get(from.substring(from.lastIndexOf("/") + 1), fileOutputStream);
        fileOutputStream.close();

        return file;
    }

    /**
     * 上传文件
     * @param from
     * @param to , 远端文件，绝对路径
     */
    def uploadFile(String from, String to) {
        if (to.endsWith("/") || to.lastIndexOf("/") == -1) {
            throw new IllegalArgumentException("Bad path: $to.");
        }
        sftp.cd(to.substring(0, to.lastIndexOf("/")));
        File file = new File(from);
        FileInputStream fileInputStream = new FileInputStream(file);
        sftp.put(fileInputStream, file.getName());
        fileInputStream.close();

        return true;
    }

    /**
     * 删除文件
     * @param path , 远端文件，绝对路径
     */
    def deleteFile(String path) {
        if (path.endsWith("/") || path.lastIndexOf("/") == -1) {
            throw new IllegalArgumentException("Bad path: $path.");
        }
        sftp.cd(path.substring(0, path.lastIndexOf("/")));
        sftp.rm(path.substring(path.lastIndexOf("/") + 1));
    }

}
