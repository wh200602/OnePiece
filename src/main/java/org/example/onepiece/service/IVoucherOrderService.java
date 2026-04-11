package org.example.onepiece.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.onepiece.dto.Result;
import org.example.onepiece.entity.VoucherOrder;

public interface IVoucherOrderService extends IService<VoucherOrder> {

    Result seckillVoucher(Long voucherId);

    Result CreateVoucherOrder(Long voucherId);
}
