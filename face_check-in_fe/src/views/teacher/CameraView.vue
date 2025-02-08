<template>
    <div>
      <h1>摄像头实时预览</h1>
      <video ref="video" autoplay></video>
      <canvas ref="canvas" style="display: none;"></canvas>
      <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    </div>
    <content-field>
      <div class="row">
        <div class="col-6">
          签到成功
          <ul>
            <li v-for="student in checkedStudents" :key="student">{{ student }}</li>
          </ul>
        </div>
        <div class="col-6">
          未签到
          <ul>
            <li v-for="student in notCheckStudents" :key="student">{{ student }}</li>
          </ul>
        </div>
      </div>
    </content-field>
  </template>
  
  <script>
  import { ref, onMounted, onUnmounted } from 'vue';
  import { useStore } from 'vuex';
  import $ from 'jquery';
  import { useRoute } from 'vue-router';
  import ContentField from '@/components/ContentField.vue';
  
  export default {
  components: { ContentField },
    setup() {
      const video = ref(null); // 视频元素
      const canvas = ref(null); // 画布元素
      const errorMessage = ref(''); // 错误信息
      let intervalId = null; // 定时器 ID
      const store = useStore();
      const route = useRoute();
      const major = route.params.major;

      let notCheckStudents = ref([]);
      let checkedStudents = ref(new Set());
      let currentMessage = ref('请人脸正对摄像头');

      const getStudentList = async () => {
        $.ajax({
          url: 'http://127.0.0.1:3007/getAllStudents/',
          type: 'GET',
          headers: {
            Authorization: 'Bearer ' + store.state.user.token,
          },
          data: { major: major },
          success(response) {
            notCheckStudents.value = response;
          }
        })
      }
  
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
      const sendImageToBackend = (imageData) => {
          $.ajax({
          url: 'http://127.0.0.1:3007/camera/',
          type: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: 'Bearer ' + store.state.user.token,
          },
          data: JSON.stringify({ image: imageData }),
          success: function (response) {
            console.log('签到成功:', response);
            currentMessage.value = response;
            notCheckStudents.value = notCheckStudents.value.filter(student => student !== response);
            checkedStudents.value.add(response);
            console.log(checkedStudents.value);
          },
          error: function (xhr, status, error) {
            console.error('图像上传失败:', error);
            if (xhr.responseJSON && xhr.responseJSON.message) {
              console.error('错误信息:', xhr.responseJSON.message);
            }
          },
        });    
      };
  
      // 组件挂载时启动摄像头并开始定时截取
      onMounted(() => {
        startCamera();
        getStudentList();
        var startTime = new Date();
        intervalId = setInterval(function(){
          captureAndSendFrame()
          if (new Date() - startTime > 4000) {
            clearInterval(intervalId);
          }
        }, 5000); // 每 2 秒截取一帧
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
        notCheckStudents,
        checkedStudents,
        currentMessage
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