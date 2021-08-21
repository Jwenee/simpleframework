package org.simpleframework.mvc.render.impl;

import org.simpleframework.mvc.RequestProcessorChain;
import org.simpleframework.mvc.render.ResultRender;
import org.simpleframework.mvc.type.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class ViewResultRender implements ResultRender {

    public static final String VIEW_PATH = "templates";
    private ModelAndView modelAndView;

    public ViewResultRender(Object mv) {
        // 1.如果入参是ModelAndView,则直接赋值
        if (mv instanceof ModelAndView) {
            this.modelAndView = (ModelAndView) mv;
        }
        // 2.如果是String，则为视图，需要包装
        else if (mv instanceof String) {
            this.modelAndView = new ModelAndView().setView((String)mv);
        }
        // 3.其他情况error
        else {
            throw new RuntimeException("Illegal request result type.");
        }
    }

    @Override
    public void render(RequestProcessorChain requestProcessorChain) throws Exception {
        HttpServletRequest request = requestProcessorChain.getRequest();
        HttpServletResponse response = requestProcessorChain.getResponse();
        String path = modelAndView.getView();
        Map<String, Object> model = modelAndView.getModel();

        for (Map.Entry<String, Object> entry : model.entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }
        request.getRequestDispatcher("/" + VIEW_PATH + "/" + path).forward(request, response);
    }
}
