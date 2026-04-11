package org.example.onepiece.controller;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.onepiece.dto.Result;
import org.example.onepiece.dto.VoucherDTO;
import org.example.onepiece.service.IVoucherService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/voucher")
public class VoucherController {
    @Resource
    private IVoucherService voucherService;

    /** 查询指定商铺的优惠券列表（无需登录） */
    @GetMapping("/list/{shopId}")
    public Result queryVoucherOfShop(@PathVariable Long shopId) {
        return voucherService.queryVoucherOfShop(shopId);
    }

    /** 新增优惠券（需要登录） */
    @PostMapping("/add")
    public Result addVoucher(@RequestBody VoucherDTO dto) {
        return voucherService.addVoucher(dto);
    }
}
