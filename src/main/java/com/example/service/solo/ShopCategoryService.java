package com.example.service.solo;

import com.example.entity.bo.ShopCategory;
import com.example.entity.dto.R;

import java.util.List;

public interface ShopCategoryService {

    R<Boolean> addShopCategory(ShopCategory shopCategory);

    R<Boolean> removeShopCategory(int shopCategoryId);

    R<Boolean> modifyShopCategory(ShopCategory shopCategory);

    R<ShopCategory> queryShopCategoryById(int shopCategoryId);

    R<List<ShopCategory>> queryShopCategory(ShopCategory shopCategoryCondition, int pageIndex, int pageSize);
}
