package com.example.controller.superadmin;

import com.example.entity.bo.HeadLine;
import com.example.entity.dto.R;
import com.example.service.solo.HeadLineService;
import org.simpleframework.core.annotation.Controller;
import org.simpleframework.inject.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class HeadLineOperationController {

    @Autowired(value = "HeadLineServiceImpl")
    private HeadLineService headLineService;

    public R<Boolean> addHeadLine(HttpServletRequest req, HttpServletResponse res) {
        return headLineService.addHeadLine(new HeadLine());
    }

    public R<Boolean> removeHeadLine(HttpServletRequest req, HttpServletResponse res) {
        return headLineService.removeHeadLine(1);
    }

    public R<Boolean> modifyHeadLine(HttpServletRequest req, HttpServletResponse res) {
        return headLineService.modifyHeadLine(new HeadLine());
    }

    public R<HeadLine> queryHeadLineById(HttpServletRequest req, HttpServletResponse res) {
        return headLineService.queryHeadLineById(1);
    }

    public R<List<HeadLine>> queryHeadLine(HttpServletRequest req, HttpServletResponse res) {
        return headLineService.queryHeadLine(null, 1, 100);
    }
}
