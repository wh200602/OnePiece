import request from '@/utils/request.js'

// 加入购物车
export const addToCartAPI = (shopId, num = 1) => {
  return request.post('/cart/add', { shopId, num })
}

// 获取购物车列表
export const getCartListAPI = () => {
  return request.get('/cart/list')
}

// 修改数量
export const updateCartNumAPI = (id, num) => {
  return request.put('/cart/update', { id, num })
}

// 删除购物车项
export const removeCartItemAPI = (id) => {
  return request.delete(`/cart/${id}`)
}
