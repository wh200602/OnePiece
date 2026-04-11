package org.example.onepiece.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.onepiece.dto.Result;
import org.example.onepiece.dto.VoucherDTO;
import org.example.onepiece.entity.Voucher;

public interface IVoucherService extends IService<Voucher> {

    Result queryVoucherOfShop(Long shopId);

    Result addVoucher(VoucherDTO dto);
}
