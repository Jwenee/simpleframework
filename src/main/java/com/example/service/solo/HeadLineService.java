package com.example.service.solo;

import com.example.entity.bo.HeadLine;
import com.example.entity.dto.R;

import java.util.List;

public interface HeadLineService {

    R<Boolean> addHeadLine(HeadLine headLine);

    R<Boolean> removeHeadLine(int headLineId);

    R<Boolean> modifyHeadLine(HeadLine headLine);

    R<HeadLine> queryHeadLineById(int headLineId);

    R<List<HeadLine>> queryHeadLine(HeadLine headLineCondition, int pageIndex, int pageSize);
}
