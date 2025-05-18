import StudentHomeView from '@/views/student/StudentHomeView.vue'
import UserLoginView from '@/views/user/UserLoginView.vue'
import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store/index'
import UserRegister from '@/views/user/UserRegister.vue'
import TeacherHomeView from '@/views/teacher/TeacherHomeView.vue'
import CameraView from '@/views/teacher/CameraView.vue'
import AddNewCourse from '@/views/teacher/AddNewCourse.vue'
import TakephotoToUpload from '@/views/student/TakephotoToUpload.vue'
import EditClassView from '@/views/teacher/EditClassView.vue'
import UploadPictureCheckIn from '@/views/teacher/UploadPictureCheckIn.vue'
import AdministratorHomeView from '@/views/administrator/AdministratorHomeView.vue'
import UpdatePasswordView from '@/views/student/UpdatePasswordCheckFaceView.vue'
import UpdateNewPassword from '@/views/student/UpdateNewPassword.vue'

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
    path: "/user/register/",
    name: "user_register",
    component: UserRegister,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/student/",
    name: 'student_index',
    component: StudentHomeView,
    meta: {
      requestAuth: true
    }
  },
  {
    path: "/teacher/",
    name: 'teacher_index',
    component: TeacherHomeView,
    meta: {
      requestAuth: true
    }
  },
  {
    path: "/admin/",
    name: 'admin_index',
    component: AdministratorHomeView,
    meta: {
      requestAuth: true
    }
  },
  {
    path: "/student/updatepassword_checkface/",
    name: 'update_password',
    component: UpdatePasswordView,
    meta: {
      requestAuth: true
    }
  },
  {
    path: "/student/updatenewpassword/",
    name: 'update_new_password',
    component: UpdateNewPassword,
    meta: {
      requestAuth: true
    }
  },
  {
    path: "/teacher/camera/:major/:schoolName",
    name: 'camera',
    component: CameraView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/teacher/:username/",
    name: 'add_new_course',
    component: AddNewCourse,
    meta: {
      // requestAuth: true,
    }
  },
  {
    path: "/student/takephoto/",
    name: 'takephoto',
    component: TakephotoToUpload,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/teacher/editclass/:major/:schoolName",
    name: 'editclass',
    component: EditClassView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/teacher/uploadpicture/:major/:schoolName",
    name: 'uploadpicture',
    component: UploadPictureCheckIn,
    meta: {
      requestAuth: true,
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
