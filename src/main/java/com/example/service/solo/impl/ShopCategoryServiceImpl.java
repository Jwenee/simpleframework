package com.example.service.solo.impl;

import com.example.entity.bo.ShopCategory;
import com.example.entity.dto.R;
import com.example.service.solo.ShopCategoryService;
import org.simpleframework.core.annotation.Service;

import java.util.List;

@Service
public class ShopCategoryServiceImpl implements ShopCategoryService {
    @Override
    public R<Boolean> addShopCategory(ShopCategory shopCategory) {
        return null;
    }

    @Override
    public R<Boolean> removeShopCategory(int shopCategoryId) {
        return null;
    }

    @Override
    public R<Boolean> modifyShopCategory(ShopCategory shopCategory) {
        return null;
    }

    @Override
    public R<ShopCategory> queryShopCategoryById(int shopCategoryId) {
        return null;
    }

    @Override
    public R<List<ShopCategory>> queryShopCategory(ShopCategory shopCategoryCondition, int pageIndex, int pageSize) {
        return null;
    }
}
