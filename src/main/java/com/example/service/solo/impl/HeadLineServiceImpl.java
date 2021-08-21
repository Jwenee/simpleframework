package com.example.service.solo.impl;

import com.example.entity.bo.HeadLine;
import com.example.entity.dto.R;
import com.example.service.solo.HeadLineService;
import lombok.extern.slf4j.Slf4j;
import org.simpleframework.core.annotation.Service;

import java.util.List;

@Slf4j
@Service
public class HeadLineServiceImpl implements HeadLineService {
    @Override
    public R<Boolean> addHeadLine(HeadLine headLine) {
        log.info("addHeadLine exec...");
        return null;
    }

    @Override
    public R<Boolean> removeHeadLine(int headLineId) {
        return null;
    }

    @Override
    public R<Boolean> modifyHeadLine(HeadLine headLine) {
        return null;
    }

    @Override
    public R<HeadLine> queryHeadLineById(int headLineId) {
        return null;
    }

    @Override
    public R<List<HeadLine>> queryHeadLine(HeadLine headLineCondition, int pageIndex, int pageSize) {
        return null;
    }
}
