package org.example.onepiece.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.onepiece.dto.Result;
import org.example.onepiece.entity.Voucher;
import org.example.onepiece.entity.VoucherOrder;
import org.example.onepiece.mapper.VoucherOrderMapper;
import org.example.onepiece.service.IVoucherOrderService;
import org.example.onepiece.service.IVoucherService;
import org.example.onepiece.utils.RedisIdWorker;
import org.example.onepiece.utils.UserHolder;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    @Resource
    private IVoucherService voucherservice;
    @Resource
    private RedisIdWorker redisIdWorker;
    @Override
    public Result seckillVoucher(Long voucherId) {

        //查询优惠卷
        Voucher voucher = voucherservice.getById(voucherId);
        //秒杀是否开始
        if(voucher.getBeginTime().isAfter(LocalDateTime.now())){
            return Result.fail("秒杀还未开始");
        }
        //秒杀是否结束
        if(voucher.getEndTime().isBefore(LocalDateTime.now())){
            return Result.fail("秒杀已经结束");
        }
        //库存是否充足
        if(voucher.getStock()<=0){
            return Result.fail("库存不足");
        }

        Long userId = UserHolder.getUser().getId();
        synchronized (userId.toString().intern()) {
            //返回订单id
            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
            return proxy.CreateVoucherOrder(voucherId);
        }
    }

    @Transactional
    public Result CreateVoucherOrder(Long voucherId) {
        //一人一单限制
        Long userId = UserHolder.getUser().getId();
            Long count = lambdaQuery()
                    .eq(VoucherOrder::getUserId, userId)       // 用户ID
                    .eq(VoucherOrder::getVoucherId, voucherId) // 优惠券ID
                    .count();  // 查数量

// 2. 如果 count > 0，说明已经买过了
            if (count > 0) {
                return Result.fail("每人限购一张");
            }

            //扣减库存
            boolean success = voucherservice.update()
                    .setSql("stock=stock-1")
                    .eq("id", voucherId)
                    .gt("stock", 0)
                    .update();
            if (!success) {
                return Result.fail("库存不足");
            }
            //创建订单
            VoucherOrder voucherOrder = new VoucherOrder();
            Long orderId = redisIdWorker.nextId("order_id");
            voucherOrder.setId(orderId);

//        Long userId = UserHolder.getUser().getId();
            voucherOrder.setUserId(userId);

            voucherOrder.setVoucherId(voucherId);

            save(voucherOrder);

            return Result.ok(orderId);
        }
}
