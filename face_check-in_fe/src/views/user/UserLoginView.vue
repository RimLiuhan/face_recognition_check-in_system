<template>
    <ContentField>
        <div class="row justify-content-md-center">
            <div class="col-3">
                <form @submit.prevent="login">
                    <label for="username" class="form-label">角色</label>
                    <select v-model="usertype" class="form-select" aria-label="Default select example">
                        <option value="3" selected>管理员</option>
                        <option value="2">教师</option>
                        <option value="1">学生</option>
                    </select>
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
                    </div>
                    <div class="mb-3" v-if="usertype == 1">
                        <label for="sid" class="form-label">学号</label>
                        <input v-model="sid" type="text" class="form-control" id="sid" placeholder="请输入学号">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">密码</label>
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
                    </div>
                    <div class="error_message">{{ error_message }}</div>
                    <button type="submit" class="btn btn-primary">提交</button>
                </form>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '@/components/ContentField.vue';
import { ref } from 'vue';
import { useStore } from 'vuex';
import router from '@/router';

export default {
    components: {
        ContentField
    },
    setup() {
        const store = useStore();
        let username = ref('');
        let password = ref('');
        let error_message = ref('');
        let usertype = ref('');
        let sid = ref('');

        const login = () => {
            error_message.value = '';
            store.dispatch("login", {
                usertype: usertype.value,
                username: username.value,
                password: password.value,
                id: String(sid.value).trim(),
                success() {
                    store.dispatch('getinfo', {
                        success() {
                            if (store.state.user.usertype == 1)
                                router.push({ name: 'student_index'})
                            else if (store.state.user.usertype == 2)
                                router.push({name: "teacher_index"})
                            else if (store.state.user.usertype == 3)
                                router.push({name: "admin_index"})
                            console.log(store.state.user);
                        },
                        error: (err) => {
                            error_message.value = '获取用户信息失败';
                            console.error(err);
                        }
                    })
                },
                error() {
                    error_message.value = '用户名或密码错误';
                }
            })
        }

        return {
            username,
            password,
            sid,
            error_message,
            usertype,
            login
        }
    }
}
</script>

<style scoped>
button {
    width: 100%;
}
div.error_message {
    color: red;
}
</style>