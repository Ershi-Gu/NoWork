package com.ershi.hotboard.controller;

import com.ershi.common.resp.ApiResult;
import com.ershi.hotboard.domain.vo.HotBoardVO;
import com.ershi.hotboard.service.IHotBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Ershi-Gu.
 * @since 2025-09-02
 */
@Tag(name = "热榜数据")
@RestController
@RequestMapping("/hot")
public class HotBoardController {

    @Resource
    private IHotBoardService hotBoardService;

    @Operation(summary = "获取所有热榜最新数据")
    @GetMapping("/list")
    public ApiResult<List<HotBoardVO>> getHotBoardList() {
        return ApiResult.success(hotBoardService.getHotBoardList());
    }
}
