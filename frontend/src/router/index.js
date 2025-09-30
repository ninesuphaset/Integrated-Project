import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import SaleItemGalleryView from '@/views/SaleItemGalleryView.vue'
import SaleItemDetailView from '@/views/SaleItemDetailView.vue'
import PageNotFound from '@/views/PageNotFound.vue'
import AddEditSaleItemView from '@/views/AddEditSaleItemView.vue'
import SaleItemManager from '@/components/SaleItemManager.vue'
import SaleItemListView from '@/views/SaleItemListView.vue'
import BrandListView from '@/views/BrandListView.vue'
import BrandManager from '@/components/BrandManager.vue'
import AddEditBrandView from '@/views/AddEditBrandView.vue'
import UserManager from '@/components/UserManager.vue'
import RegisterForm from '@/views/RegisterForm.vue'
import VerifyEmail from '@/views/VerifyEmail.vue'
import SignIn from '@/views/SignIn.vue'
import ViewEditProfile from '@/views/ViewEditProfile.vue'
import CartView from '@/views/CartView.vue'

const history = createWebHistory(import.meta.env.BASE_URL)

const routes = [
  { path: '/', name: 'Home', component: HomeView },
  {
    path: '/sale-items',
    component: SaleItemManager,
    children: [
      { path: '', name: 'SaleItemGallery', component: SaleItemGalleryView },
      { path: 'list', name: 'SaleItemList', component: SaleItemListView },
      {
        path: ':saleItemId',
        name: 'SaleItemDetail',
        component: SaleItemDetailView,
      },
      {
        path: ':saleItemId/edit',
        name: 'EditSaleItem',
        component: AddEditSaleItemView,
      },

      {
        path: 'add',
        name: 'AddSaleItem',
        component: AddEditSaleItemView,
      },
    ],
  },
  {
    path: '/brands',
    component: BrandManager,
    children: [
      { path: '', name: 'BrandListView', component: BrandListView },
      { path: 'add', name: 'AddBrand', component: AddEditBrandView },
      { path: ':brandId/edit', name: 'EditBrand', component: AddEditBrandView },
    ],
  },
  {
    path: '/register',
    component: UserManager,
    children: [
      {
        name: 'RegisterForm',
        path: '',
        component: RegisterForm,
      },
    ],
  },
  {
    path: '/signin',
    component: UserManager,
    children: [
      {
        name: 'SignIn',
        path: '',
        component: SignIn,
      },
    ],
  },
  {
    path: '/profile',
    component: UserManager,
    children: [
      {
        name: 'Profile',
        path: '',
        component: ViewEditProfile,
      },
      {
        name: 'EditProfile',
        path: 'edit',
        component: ViewEditProfile,
      },
    ],
  },
  { path: '/cart', name: 'Cart', component: CartView },
  {
    path: '/verify-email',
    name: 'VerifyEmail',
    component: VerifyEmail,
  },
  {
    path: '/:catchNotMatchPath(.*)',
    name: 'PageNotFound',
    component: PageNotFound,
  },
]

const router = createRouter({
  history,
  routes,
})

export default router
