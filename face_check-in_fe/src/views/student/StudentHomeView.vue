<template>
  <content-field>
    <div class="mb-3">
      <div v-if="!error_message">您还未登记人脸信息, 可选择上传照片或拍照登记</div>
      <div v-else>您已经登记过人脸信息, 可以重新登记</div>
      <input ref="fileInput" class="form-control" type="file" id="formFile" accept="image/*">
      <button @click="upload" type="button" class="btn btn-primary">上传</button>
    </div>
  </content-field>
</template>

<script>
import ContentField from '@/components/ContentField.vue';
import { useStore } from 'vuex';
import { ref } from 'vue';
import $ from 'jquery';

export default {
  components: { ContentField },
  setup() {
    const store = useStore();
    const fileInput = ref(null);
    let error_message = ref(false);

    $.ajax({
      async: false,
      url: "http://127.0.0.1:3007/user/account/checkFaceFeatures/",
      type: "get",
      headers: {
        Authorization: "Bearer " + store.state.user.token,
      },
      success(resp) {
        error_message.value = resp.error_message === 'true';
      }, 
      error(resp) {
        console.log(resp);
      }
    })

    console.log(error_message.value);

    const upload = () => {
      if (!fileInput.value || fileInput.value.files.length === 0) {
        alert('请选择一张图片');
        return;
      }
      const file = fileInput.value.files[0];
      const formData = new FormData();
      formData.append('photo', file, store.state.user.id + store.state.user.username + ".jpg");

      $.ajax({
        url: "http://127.0.0.1:3007/upload/", // 后端接口地址
        type: 'POST',
        data: formData,
        processData: false, // 禁止jQuery处理数据
        contentType: false, // 禁止jQuery设置Content-Type
        headers: {
            Authorization: "Bearer " + store.state.user.token,
        },
        success: function(response) {
            console.log('Photo saved:', response);
        },
        error: function(error) {
            console.error('Error uploading photo:', error);
        }
    });
    };

    return {
      fileInput,
      upload,
      error_message
    };
  }
};
</script>

<style scoped>
/* 样式代码 */
</style>