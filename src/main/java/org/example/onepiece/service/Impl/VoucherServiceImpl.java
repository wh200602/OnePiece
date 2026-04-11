package org.example.onepiece.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.onepiece.dto.Result;
import org.example.onepiece.dto.VoucherDTO;
import org.example.onepiece.entity.Voucher;
import org.example.onepiece.mapper.VoucherMapper;
import org.example.onepiece.service.IVoucherService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements IVoucherService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryVoucherOfShop(Long shopId) {
        List<Voucher> vouchers = lambdaQuery()
                .eq(Voucher::getShopId, shopId)
                .list();
        return Result.ok(vouchers);
    }

    @Override
    public Result addVoucher(VoucherDTO dto) {
        Voucher voucher = new Voucher();
        BeanUtil.copyProperties(dto, voucher);
        this.save(voucher);
        return Result.ok("添加成功", voucher.getId());
    }
}

