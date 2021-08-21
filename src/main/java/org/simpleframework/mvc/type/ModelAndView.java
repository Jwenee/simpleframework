package org.simpleframework.mvc.type;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class ModelAndView {
    // path
    @Getter
    private String view;
    // data
    @Getter
    private Map<String, Object> model = new HashMap<>();

    public ModelAndView setView(String view) {
        this.view = view;
        return this;
    }

    public ModelAndView addViewData(String attributeName, String attributeValue) {
        model.put(attributeName, attributeValue);
        return this;
    }
}
