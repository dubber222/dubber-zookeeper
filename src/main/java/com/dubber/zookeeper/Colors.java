package com.dubber.zookeeper;

/**
 * Created on 2018/5/21.
 *
 * @author dubber
 */
public enum Colors {

    RED(1), GREEN(2), BLUE(3);
    private int code;

    Colors(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}