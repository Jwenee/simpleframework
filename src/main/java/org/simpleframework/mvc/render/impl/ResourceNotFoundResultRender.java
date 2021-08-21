package org.simpleframework.mvc.render.impl;

import org.simpleframework.mvc.RequestProcessorChain;
import org.simpleframework.mvc.render.ResultRender;

import javax.servlet.http.HttpServletResponse;

public class ResourceNotFoundResultRender implements ResultRender {

    private String requestMethod;

    private String requestPath;

    public ResourceNotFoundResultRender(String requestMethod, String requestPath) {
        this.requestMethod = requestMethod;
        this.requestPath = requestPath;
    }

    @Override
    public void render(RequestProcessorChain requestProcessorChain) throws Exception {
        requestProcessorChain.getResponse().sendError(HttpServletResponse.SC_NOT_FOUND,
                "获取不到请求的资源，请求路径[" + requestPath + "]" + "请求方法[" + requestMethod + "]");
    }
}
