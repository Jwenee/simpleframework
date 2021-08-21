package com.example.service.combine.impl;

import com.example.entity.bo.HeadLine;
import com.example.entity.bo.ShopCategory;
import com.example.entity.dto.MainPageInfoDTO;
import com.example.entity.dto.R;
import com.example.service.combine.HeadLineShopCategoryCombineService;
import com.example.service.solo.HeadLineService;
import com.example.service.solo.ShopCategoryService;
import org.simpleframework.core.annotation.Service;
import org.simpleframework.inject.annotation.Autowired;

import java.util.List;

@Service
public class HeadLineShopCategoryCombineServiceImpl implements HeadLineShopCategoryCombineService {

    @Autowired
    private HeadLineService headLineService;

    @Autowired
    private ShopCategoryService shopCategoryService;

    @Override
    public R<MainPageInfoDTO> getMainPageInfo() {
        HeadLine headLine = new HeadLine();
        headLine.setEnableStatus(1);
        R<List<HeadLine>> headLineR = headLineService.queryHeadLine(headLine, 1, 4);

        ShopCategory shopCategory = new ShopCategory(); // parent null
        R<List<ShopCategory>> shopCategoryR = shopCategoryService.queryShopCategory(shopCategory, 1, 100);

        return mergeMainPageInfoR(headLineR, shopCategoryR);
    }

    private R<MainPageInfoDTO> mergeMainPageInfoR(R<List<HeadLine>> headLineR, R<List<ShopCategory>> shopCategoryR) {
        return null;
    }
}
