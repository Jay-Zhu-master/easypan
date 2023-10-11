package com.jayzhu.easypan.controller;


import com.jayzhu.easypan.entity.constats.Constants;
import com.jayzhu.easypan.entity.dto.SessionWebUserDto;
import com.jayzhu.easypan.entity.enums.ResponseCodeEnum;
import com.jayzhu.easypan.entity.vo.PaginationResultVO;
import com.jayzhu.easypan.entity.vo.ResponseVO;
import com.jayzhu.easypan.utils.CopyTools;
import jakarta.servlet.http.HttpSession;

public class ABaseController {

    protected static final String STATUC_SUCCESS = "success";

    protected static final String STATUC_ERROR = "error";


    protected <T> ResponseVO getSuccessResponseVO(T t) {
        ResponseVO<T> responseVO = new ResponseVO<>();
        responseVO.setStatus(STATUC_SUCCESS);
        responseVO.setCode(ResponseCodeEnum.CODE_200.getCode());
        responseVO.setInfo(ResponseCodeEnum.CODE_200.getMsg());
        responseVO.setData(t);
        return responseVO;
    }

    protected <S, T> PaginationResultVO<T> convert2PaginationVO(PaginationResultVO<S> result, Class<T> tClass) {
        PaginationResultVO<T> resultVO = new PaginationResultVO<>();
        resultVO.setList(CopyTools.copyList(result.getList(), tClass));
        resultVO.setPageNo(result.getPageNo());
        resultVO.setPageSize(result.getPageSize());
        resultVO.setPageTotal(result.getPageTotal());
        resultVO.setTotalCount(result.getTotalCount());

        return resultVO;
    }

    protected SessionWebUserDto getUserInfoFromSession(HttpSession session) {
        return (SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY);
    }
}
