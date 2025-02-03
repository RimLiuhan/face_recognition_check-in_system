<template>
  <content-field>
    学生
    <div class="mb-3">
      <label for="formFile" class="form-label">选择照片上传</label>
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
    };
  }
};
</script>

<style scoped>
/* 样式代码 */
</style>