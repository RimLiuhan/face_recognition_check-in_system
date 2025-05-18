<template>
    <nav class="navbar navbar-expand-lg bg-body-tertiary">
        <div class="container">
            <a class="navbar-brand" href="#">人脸识别签到系统</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarText" aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarText">

            <span v-if="$store.state.user.usertype == 3">管理员界面</span>
            <span v-if="$store.state.user.usertype == 2">教师界面</span>
            <span v-if="$store.state.user.usertype == 1">学生界面</span>

            <ul class="navbar-nav ms-auto" v-if="$store.state.user.is_login">
                <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="javascript:;" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                    {{ $store.state.user.username }}
                </a>
                <ul class="dropdown-menu">
                    <li v-if="$store.state.user.usertype == 2"><router-link :to="{name: 'add_new_course', params: {username: $store.state.user.usertype}}">新建课程</router-link></li>
                    <li v-if="$store.state.user.usertype == 1"><router-link :to="{name: 'update_password'}">修改密码</router-link></li>
                    <li><a class="dropdown-item" href="javascript:;" @click="logout">退出</a></li>
                </ul>
                </li>
            </ul>
            <ul class="navbar-nav ms-auto" v-else>
                <li class="nav-item">
                <router-link class="nav-link" :to="{name: 'user_login'}" role="button">
                    登录
                </router-link>
                </li>
                <li class="nav-item">
                <router-link class="nav-link" :to="{name: 'user_register'}" role="button">
                    教师注册
                </router-link>
                </li>
            </ul>
            </div>
        </div>
    </nav>
</template>

<script>
import { useStore } from 'vuex';
export default {
    setup() {
        const store = useStore();
        const logout = () => {
            store.dispatch("logout");
        }
        return {
            logout
        }
    }

}
</script>

<style scoped>
    
</style>