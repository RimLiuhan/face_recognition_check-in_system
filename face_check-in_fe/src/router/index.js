import StudentHomeView from '@/views/student/StudentHomeView.vue'
import UserLoginView from '@/views/user/UserLoginView.vue'
import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store/index'

const routes = [
  {
    path: "/",
    name: "home",
    meta: {
      requestAuth: true
    }
  },
  {
    path: "/user/login/",
    name: "user_login",
    component: UserLoginView,
    meta: {
      requestAuth: false
    }
  },
  {
    path: "/student/",
    name: 'student_index',
    component: StudentHomeView,
    meta: {
      requestAuth: true
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})


router.beforeEach((to,from,next)=>{
  const jwt_token = localStorage.getItem('jwt_token');
  if(jwt_token){
    store.commit('updateToken',jwt_token);
    store.dispatch('getinfo',{
      success(){
        next();
      },
      error(){
        // console.log(store.state.user);
        store.dispatch('logout');
        alert("token无效，请重新登录！");
        next({name:"user_login"});
      }
    })
  }else{
    if(to.meta.requestAuth && !store.state.user.is_login){
      next({name:"user_login"});
    }else{
      next();
    }
  }
})

export default router
