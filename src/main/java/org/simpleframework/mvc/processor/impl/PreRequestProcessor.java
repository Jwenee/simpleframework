package org.simpleframework.mvc.processor.impl;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.mvc.RequestProcessorChain;
import org.simpleframework.mvc.processor.RequestProcessor;

// 请求预处理，编码及路径处理
@Slf4j
public class PreRequestProcessor implements RequestProcessor {
    @Override
    public boolean process(RequestProcessorChain requestProcessorChain) throws Exception {
        //1.设置请求的编码 UTF-8
        requestProcessorChain.getRequest().setCharacterEncoding("UTF-8");
        //2. 去除请求路径末尾的斜杠  方便后续适配Controller
        // 比如/aaa/bbb = /aaa/bbb
        String requestPath = requestProcessorChain.getRequestPath();
        if (requestPath.length() > 1 && requestPath.endsWith("/")) {
            String substring = requestPath.substring(0, requestPath.length() - 1);
            requestProcessorChain.setRequestPath(substring);
        }
        log.info("preprocessor request {} {}",requestProcessorChain.getRequestMethod(), requestProcessorChain.getRequestPath());
        //编译后续的处理器执行
        return true;
    }
}
