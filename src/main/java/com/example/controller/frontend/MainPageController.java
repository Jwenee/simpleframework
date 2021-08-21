package com.example.controller.frontend;

import com.example.entity.dto.MainPageInfoDTO;
import com.example.entity.dto.R;
import com.example.service.combine.HeadLineShopCategoryCombineService;
import lombok.Getter;
import org.simpleframework.core.annotation.Controller;
import org.simpleframework.inject.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Getter
@Controller
public class MainPageController {

    @Autowired
    private HeadLineShopCategoryCombineService headLineShopCategoryCombineService;

    public R<MainPageInfoDTO> getMainPageInfo(HttpServletRequest req, HttpServletResponse res) {
        return headLineShopCategoryCombineService.getMainPageInfo();
    }
}
