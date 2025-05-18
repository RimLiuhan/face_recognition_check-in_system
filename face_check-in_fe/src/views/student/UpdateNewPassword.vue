<template>
    <content-field>
        <div class="row justify-content-md-center">
            <div class="col-3">
                <form @submit.prevent="updatePassword">
                    <div class="mb-3">
                        <label for="password" class="form-label">新密码</label>
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
                    </div>
                    <div class="mb-3">
                        <label for="confirmedPassword" class="form-label">确认密码</label>
                        <input v-model="confirmedPassword" type="password" class="form-control" id="confirmedPassword" placeholder="请输入密码">
                    </div>
                    <div class="error_message">{{ error_message }}</div>
                    <button type="submit" class="btn btn-primary">提交</button>
                </form>
            </div>
        </div>
    </content-field>
</template>

<script>
import ContentField from '@/components/ContentField.vue';
import { ref } from 'vue';
import { useStore } from 'vuex';
import $ from 'jquery';
import router from '@/router';

export default {
  components: { ContentField },
  setup() {
    const store = useStore();
    let id = store.state.user.id;
    let password = ref('');
    let confirmedPassword = ref('');
    let error_message = ref('');
    const updatePassword = () => {
        $.ajax({
            url: "http://127.0.0.1:3007/student/updatepassword/",
            type: "post",
            data: {
                id: id,
                password: password.value,
                confirmedPassword: confirmedPassword.value
            },
            headers: {
                Authorization: "Bearer " + store.state.user.token,
            },
            success(resp) {
                if (resp.success) {  // 根据 JSON 中的字段判断
                    console.log("修改成功:", resp.message);
                    alert(resp.message);
                    setTimeout(() => {
                        router.push({ name: "student_index" });
                    }, 1500);
                } else {
                    console.error("修改失败:", resp.message);
                    alert(resp.message);
                }
            },
            error(xhr, status, error) {
                console.error("请求错误:", error);
                // this.$message.error("网络错误，请稍后重试");
                alert(xhr.responseJSON.message);
                
                // 检查是否有响应内容
                if (xhr.responseJSON) {
                    console.error("错误详情:", xhr.responseJSON);
                }
            },
        })
    }
    return {
        updatePassword,
        password,
        confirmedPassword,
        error_message
    }
  }
}
</script>

<style scoped>
    
</style>