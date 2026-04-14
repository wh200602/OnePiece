import { createRouter, createWebHistory } from 'vue-router'
// 修正路径：HomeView在components文件夹下
import HomeView from '../components/HomeView.vue'
import UserLogin from '../components/User_Login.vue'
import Main_1 from '../components/Main_1.vue'
import WB_Shop from '../components/WB_Shop.vue'
import WB_Cart from '../components/WB_Cart.vue'
import WB_Group from '../components/WB_Group.vue'
import WB_Detail from '../components/WB_Detail.vue'
import WB_Voucher from '../components/WB_Voucher.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView // 指向正确的首页组件
    },
    {
      path: '/login',
      name: 'login',
      component: UserLogin
    },
    {
      path: '/home',
      name: 'main',
      component: Main_1
    },
    {
      path: '/shop',
      name: 'shop',
      component: WB_Shop
    },
    {
      path: '/cart',
      name: 'cart',
      component: WB_Cart
    },
    {
      path: '/group',
      name: 'group',
      component: WB_Group
    },
    {
      path: '/shop/:id',
      name: 'detail',
      component: WB_Detail
    },
    {
      path: '/voucher/:shopId?',
      name: 'voucher',
      component: WB_Voucher
    }
  ]
})

export default router
