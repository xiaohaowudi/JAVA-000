package io.kimmking.rpcfx.api;

import java.io.Serializable;

public class RpcfxException extends Throwable implements Serializable {
    private String causeString;                   // 异常原因字符串
    private String lastStackFilenme;        // 异常调用栈最后一层的文件名
    private Integer lastStackLineNum;       // 异常调用最后一层的行号
    private String lastMethodName;          // 异常最后调用的函数名

    public String getLastMethodName() {
        return lastMethodName;
    }

    public void setLastMethodName(String lastMethodName) {
        this.lastMethodName = lastMethodName;
    }

    public String getCauseString() {
        return causeString;
    }

    public void setCauseString(String cause) {
        this.causeString = cause;
    }

    public String getLastStackFilenme() {
        return lastStackFilenme;
    }

    public void setLastStackFilenme(String lastStackFilenme) {
        this.lastStackFilenme = lastStackFilenme;
    }

    public Integer getLastStackLineNum() {
        return lastStackLineNum;
    }

    public void setLastStackLineNum(Integer lastStackLineNum) {
        this.lastStackLineNum = lastStackLineNum;
    }

    @Override
    public String toString() {
        return "RpcfxException{" +
                "cause='" + causeString + '\'' +
                ", lastStackFilenme='" + lastStackFilenme + '\'' +
                ", lastStackLineNum=" + lastStackLineNum +
                ", lastMethodName='" + lastMethodName + '\'' +
                '}';
    }
}
