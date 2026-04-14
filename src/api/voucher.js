// @/api/voucher.js
import request from '@/utils/request.js'

/**
 * 查询指定店铺的优惠券列表
 * GET /voucher/list/{shopId}
 * @param {number|string} shopId 店铺ID
 * @returns {Promise<Array>} 优惠券列表
 */
export const queryVoucherOfShopAPI = (shopId) =>
  request.get(`/voucher/list/${shopId}`)

/**
 * 秒杀抢购优惠券
 * POST /voucher_order/seckill/{id}
 * @param {number|string} voucherId 优惠券ID
 * @returns {Promise<number>} 订单ID
 */
export const seckillVoucherAPI = (voucherId) =>
  request.post(`/voucher_order/seckill/${voucherId}`)
