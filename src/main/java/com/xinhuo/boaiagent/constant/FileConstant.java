package com.xinhuo.boaiagent.constant;

/**
 * 文件常量
 */
public interface FileConstant {

    /**
     * 文件保存目录（项目根目录下的 temp 文件夹）
     * user.dir 启动程序时所在的目录，并指向 /temp
     */
    String FILE_SAVE_DIR = System.getProperty("user.dir") + "/tmp";
}