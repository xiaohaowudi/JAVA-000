package io.kimmking.rpcfx.api;

public class RpcfxRequest {

    private String serviceClass;
    private String method;
    private Object[] params;
    private Object[] paramsTypes;

    public Object[] getParamsTypes() {
        return paramsTypes;
    }

    public void setParamsTypes(Object[] paramsTypes) {
        this.paramsTypes = paramsTypes;
    }

    public String getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
