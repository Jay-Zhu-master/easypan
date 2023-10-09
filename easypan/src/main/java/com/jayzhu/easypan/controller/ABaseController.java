package com.jayzhu.easypan.controller;


import com.jayzhu.easypan.entity.enums.ResponseCodeEnum;
import com.jayzhu.easypan.entity.vo.PaginationResultVO;
import com.jayzhu.easypan.entity.vo.ResponseVO;
import com.jayzhu.easypan.utils.CopyTools;

public class ABaseController {
    protected <S, T> PaginationResultVO<T> convert2PaginationVO(PaginationResultVO<S> result, Class<T> tClass) {
        PaginationResultVO<T> resultVO = new PaginationResultVO<>();
        resultVO.setList(CopyTools.copyList(result.getList(), tClass));
        resultVO.setPageNo(result.getPageNo());
        resultVO.setPageSize(result.getPageSize());
        resultVO.setPageTotal(result.getPageTotal());
        resultVO.setTotalCount(result.getTotalCount());

        return resultVO;
    }
}
