package com.example.controller.superadmin;

import com.example.entity.bo.ShopCategory;
import com.example.entity.dto.R;
import com.example.service.solo.ShopCategoryService;
import org.simpleframework.core.annotation.Controller;
import org.simpleframework.inject.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class ShopCategoryOperationController {

    @Autowired
    private ShopCategoryService shopCategoryService;

    public R<Boolean> addShopCategory(HttpServletRequest req, HttpServletResponse res) {
        return shopCategoryService.addShopCategory(new ShopCategory());
    }

    public R<Boolean> removeShopCategory(HttpServletRequest req, HttpServletResponse res) {
        return shopCategoryService.removeShopCategory(1);
    }

    public R<Boolean> modifyShopCategory(HttpServletRequest req, HttpServletResponse res) {
        return shopCategoryService.modifyShopCategory(new ShopCategory());
    }

    public R<ShopCategory> queryShopCategoryById(HttpServletRequest req, HttpServletResponse res) {
        return shopCategoryService.queryShopCategoryById(1);
    }

    public R<List<ShopCategory>> queryShopCategory(HttpServletRequest req, HttpServletResponse res) {
        return shopCategoryService.queryShopCategory(null, 1, 100);
    }
}
