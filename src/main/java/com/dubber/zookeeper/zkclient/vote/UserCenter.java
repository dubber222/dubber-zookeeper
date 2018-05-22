package com.dubber.zookeeper.zkclient.vote;

import java.io.Serializable;

/**
 * Demo class
 *
 * @author dubber
 * @date 2018/5/20
 */
public class UserCenter implements Serializable {

    private static final long serialVersionUID = 594444005879460463L;

    /**
     * 机器id
     */
    private int pcId;
    /**
     * 机器名称
     */
    private String pcName;

    public int getPcId() {
        return pcId;
    }

    public void setPcId(int pcId) {
        this.pcId = pcId;
    }

    public String getPcName() {
        return pcName;
    }

    public void setPcName(String pcName) {
        this.pcName = pcName;
    }
}
