<template>
    <div class="camera-upload-container">
      <h2 class="mb-4">拍照核实身份</h2>
      
      <div class="camera-box mb-3">
        <video ref="video" class="camera-view" autoplay playsinline v-show="!photoTaken"></video>
        <canvas ref="canvas" class="camera-view" v-show="photoTaken"></canvas>
      </div>
      
      <div class="button-group">
        <button 
          class="btn btn-primary mr-2" 
          @click="takePhoto" 
          v-if="!photoTaken && streamActive"
        >
          拍照
        </button>
        <button 
          class="btn btn-secondary mr-2" 
          @click="retakePhoto" 
          v-if="photoTaken"
        >
          重拍
        </button>
        <button 
          class="btn btn-success" 
          @click="uploadPhoto" 
          v-if="photoTaken"
          :disabled="uploading"
        >
          <span v-if="!uploading">上传</span>
          <span v-else>
            <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
            上传中...
          </span>
        </button>
      </div>
      
      <div class="alert alert-success mt-3" v-if="uploadSuccess">
            <div>图片上传成功！正在跳转...</div>
            <div class="progress mt-2">
            <div 
                class="progress-bar progress-bar-striped progress-bar-animated" 
                :style="{ width: progress + '%' }"
            ></div>
            </div>
        </div>
      <div class="alert alert-danger mt-3" v-if="uploadError">
        上传失败: {{ errorMessage }}
      </div>
    </div>
  </template>
  
  <script setup>
  import router from '@/router';
  import { ref, onMounted, onBeforeUnmount } from 'vue';
  import { useStore } from 'vuex';
  
  // 响应式数据
  const video = ref(null);
  const canvas = ref(null);
  const stream = ref(null);
  const streamActive = ref(false);
  const photoTaken = ref(false);
  const photoData = ref(null);
  const uploading = ref(false);
  const uploadSuccess = ref(false);
  const uploadError = ref(false);
  const errorMessage = ref('');
  const store = useStore();

  const progress = ref(0);
  let progressInterval;
  
  // 初始化摄像头
  const initCamera = async () => {
    try {
      stream.value = await navigator.mediaDevices.getUserMedia({
        video: {
          width: { ideal: 1280 },
          height: { ideal: 720 },
          facingMode: 'environment' // 优先使用后置摄像头
        },
        audio: false
      });
      
      video.value.srcObject = stream.value;
      streamActive.value = true;
    } catch (err) {
      console.error("摄像头访问失败:", err);
      errorMessage.value = '无法访问摄像头: ' + err.message;
      uploadError.value = true;
    }
  };
  
  // 关闭摄像头
  const stopCamera = () => {
    if (stream.value) {
      stream.value.getTracks().forEach(track => track.stop());
      streamActive.value = false;
    }
  };
  
  // 拍照
  const takePhoto = () => {
    const context = canvas.value.getContext('2d');
    canvas.value.width = video.value.videoWidth;
    canvas.value.height = video.value.videoHeight;
    
    context.drawImage(video.value, 0, 0, canvas.value.width, canvas.value.height);
    
    photoTaken.value = true;
    photoData.value = canvas.value.toDataURL('image/jpeg', 0.8);
    stopCamera();
  };
  
  // 重拍
  const retakePhoto = () => {
    photoTaken.value = false;
    photoData.value = null;
    uploadSuccess.value = false;
    uploadError.value = false;
    initCamera();
  };
  
  // 将DataURL转换为Blob
  const dataURLtoBlob = (dataURL) => {
    const arr = dataURL.split(',');
    const mime = arr[0].match(/:(.*?);/)[1];
    const bstr = atob(arr[1]);
    let n = bstr.length;
    const u8arr = new Uint8Array(n);
    
    while (n--) {
      u8arr[n] = bstr.charCodeAt(n);
    }
    
    return new Blob([u8arr], { type: mime });
  };
  
  // 上传照片
  const uploadPhoto = async () => {
    if (!photoData.value) return;
    
    uploading.value = true;
    uploadSuccess.value = false;
    uploadError.value = false;
    
    try {
      const blob = dataURLtoBlob(photoData.value);
      const formData = new FormData();
      formData.append('image', blob, 'photo.jpg');
      formData.append('id', store.state.user.id);

      // 调试：打印FormData内容
        for (let [key, value] of formData.entries()) {
        console.log(key, value);
        }
    
      
      const response = await fetch('http://127.0.0.1:3007/student/takephoto/', {
        method: 'POST',
        body: formData,
        headers: {
            Authorization: "Bearer " + store.state.user.token,
        },
      });
      
      if (!response.ok) {
        throw new Error('上传失败: ' + response.statusText);
      }
      
      const result = await response.json();
      console.log('上传成功:', result);
      uploadSuccess.value = true;
      // 启动进度条动画（2秒内从0%到100%）
        progress.value = 0;
        progressInterval = setInterval(() => {
            progress.value += 5; // 每100ms增加5%
            if (progress.value >= 100) {
            clearInterval(progressInterval);
            if (result.studentId === store.state.user.id) {
                router.push({ name: 'update_new_password' });
            }
            }
        }, 100); // 每100ms更新一次
      
    } catch (err) {
      console.error('上传错误:', err);
      errorMessage.value = err.message;
      uploadError.value = true;
    } finally {
      uploading.value = false;
    }
  };
  
  // 生命周期钩子
  onMounted(() => {
    initCamera();
  });
  
  onBeforeUnmount(() => {
    stopCamera();
    if (progressInterval) clearInterval(progressInterval); // 防止内存泄漏
  });
  </script>
  
  <style scoped>
  .camera-upload-container {
    max-width: 600px;
    margin: 0 auto;
    padding: 20px;
    text-align: center;
  }
  
  .camera-box {
    width: 100%;
    background-color: #f8f9fa;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  .camera-view {
    width: 100%;
    max-height: 500px;
    object-fit: contain;
  }
  
  .button-group {
    margin-top: 15px;
  }
  
  .alert {
    margin-top: 15px;
  }
  </style>