<template>
    <div>
      <h1>摄像头实时预览</h1>
      <video ref="video" autoplay></video>
      <canvas ref="canvas" style="display: none;"></canvas>
      <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    </div>
  </template>
  
  <script>
  import { ref, onMounted, onUnmounted } from 'vue';
  import { useStore } from 'vuex';
  
  export default {
    setup() {
      const video = ref(null); // 视频元素
      const canvas = ref(null); // 画布元素
      const errorMessage = ref(''); // 错误信息
      let intervalId = null; // 定时器 ID
      const store = useStore();
  
      // 启动摄像头
      const startCamera = async () => {
        try {
          const stream = await navigator.mediaDevices.getUserMedia({ video: true });
          video.value.srcObject = stream;
        } catch (err) {
          errorMessage.value = '无法访问摄像头，请确保已授予摄像头权限。';
          console.error('摄像头访问失败:', err);
        }
      };
  
      // 截取一帧并发送到后端
      const captureAndSendFrame = () => {
        const videoElement = video.value;
        const canvasElement = canvas.value;
        const context = canvasElement.getContext('2d');
  
        // 设置画布尺寸与视频一致
        canvasElement.width = videoElement.videoWidth;
        canvasElement.height = videoElement.videoHeight;
  
        // 将视频帧绘制到画布上
        context.drawImage(videoElement, 0, 0, canvasElement.width, canvasElement.height);
  
        // 将画布内容转换为 Base64 图像数据
        const imageData = canvasElement.toDataURL('image/jpeg');
  
        // 发送图像数据到后端
        sendImageToBackend(imageData);
      };
  
      // 发送图像数据到后端
      const sendImageToBackend = async (imageData) => {
        try {
          const response = await fetch('http://127.0.0.1:3007/camera/', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
              Authorization: "Bearer " + store.state.user.token,
            },
            body: JSON.stringify({ image: imageData }),
          });
  
          if (!response.ok) {
            throw new Error('后端请求失败');
          }
  
          const result = await response.json();
          console.log('图像上传成功:', result);
        } catch (err) {
          console.error('图像上传失败:', err);
        }
      };
  
      // 组件挂载时启动摄像头并开始定时截取
      onMounted(() => {
        startCamera();
        intervalId = setInterval(captureAndSendFrame, 2000); // 每 2 秒截取一帧
      });
  
      // 组件卸载时停止摄像头和定时器
      onUnmounted(() => {
        if (video.value.srcObject) {
          video.value.srcObject.getTracks().forEach(track => track.stop());
        }
        if (intervalId) {
          clearInterval(intervalId);
        }
      });
  
      return {
        video,
        canvas,
        errorMessage,
      };
    },
  };
  </script>
  
  <style scoped>
  video {
    width: 100%;
    max-width: 640px;
    border: 1px solid #ccc;
    border-radius: 8px;
  }
  
  .error {
    color: red;
    font-weight: bold;
  }
  </style>