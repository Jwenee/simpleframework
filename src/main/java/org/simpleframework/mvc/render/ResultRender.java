package org.simpleframework.mvc.render;

import org.simpleframework.mvc.RequestProcessorChain;

public interface ResultRender {

    void render(RequestProcessorChain requestProcessorChain) throws Exception;
}
