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
import { onMounted, ref } from 'vue';
import $ from 'jquery';
import * as faceapi from 'face-api.js';

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

    const loadModels = async () => {
        await faceapi.nets.tinyFaceDetector.loadFromUri('/weights');
        await faceapi.nets.faceLandmark68Net.loadFromUri('/weights');
        await faceapi.nets.faceRecognitionNet.loadFromUri('/weights');
        console.log('Models loaded successfully');
      };

    const upload = async() => {
      if (!fileInput.value || fileInput.value.files.length === 0) {
        alert('请选择一张图片');
        return;
      }
      const file = fileInput.value.files[0];
      const img = await loadImage(file);
      // 使用 face-api.js 检测人脸
      const detections = await faceapi.detectAllFaces(img, new faceapi.TinyFaceDetectorOptions())
          .withFaceLandmarks()
          .withFaceDescriptors();
      if (detections.length === 0) {
        alert('未检测到人脸，请重新选择图片。');
        return;
      }
      const formData = new FormData();
      formData.append('photo', file, store.state.user.id + store.state.user.username + ".jpg");

      await $.ajax({
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
            alert('上传成功');
        },
        error: function(error) {
            console.error('Error uploading photo:', error);
        }
    });
    };

    onMounted(() => {
        loadModels();
    });

    // 辅助函数：将File对象转为HTMLImageElement
    const loadImage = (file) => {
      return new Promise((resolve, reject) => {
        const img = new Image();
        const url = URL.createObjectURL(file);
        img.onload = () => {
          URL.revokeObjectURL(url); // 释放内存
          resolve(img);
        };
        img.onerror = (e) => reject(e);
        img.src = url;
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