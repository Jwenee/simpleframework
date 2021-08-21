package com.example.entity.dto;

import com.example.entity.bo.HeadLine;
import com.example.entity.bo.ShopCategory;
import lombok.Data;

import java.util.List;

@Data
public class MainPageInfoDTO {

    private List<HeadLine> headLineList;

    private List<ShopCategory> shopCategoryList;
}
