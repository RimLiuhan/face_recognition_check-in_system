  <template>
    <div>
      <h1>摄像头实时预览</h1>
      <h4>最好在光线适度的环境下使用, 如果背景光线较强会影响人脸的识别</h4>
      <div class="card">
        <div class="card-body">
          <video ref="video" autoplay></video>
          <canvas ref="canvas"></canvas>
        </div>
      </div>
      
      <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    </div>
    <content-field>
    <div class="row">
        <div class="col-6" style="border: 1px solid black; padding: 2px; border-radius: 8px;">
          签到成功
          <ul class="student-list">
            <li v-for="student in checkedStudents" :key="student">{{ student }}</li>
          </ul>
        </div>
        <div class="col-6" style="border: 1px solid black; padding: 2px; border-radius: 8px;">
          未签到
          <ul class="student-list">
            <li v-for="student in notCheckStudents" :key="student">{{ student }}</li>
          </ul>
        </div>
      </div>
    </content-field>
    <button type="button" class="btn btn-danger" @click="stopCheckIn">结束签到</button>
  </template>
  
  <script>
  import { ref, onMounted, onUnmounted } from 'vue';
  import { useStore } from 'vuex';
  import $ from 'jquery';
  import { useRoute } from 'vue-router';
  import ContentField from '@/components/ContentField.vue';
  import * as faceapi from 'face-api.js';
  
  export default {
    components: { ContentField },
    setup() {
      const video = ref(null); // 视频元素
      const stream = ref(null); // 保存摄像头流对象
      const canvas = ref(null); // 画布元素
      const errorMessage = ref('请人脸正对摄像头'); // 错误信息
      let intervalId = null; // 定时器 ID
      const store = useStore();
      const route = useRoute();
      const major = route.params.major;
      const schoolName = route.params.schoolName;
  
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
          data: { major, schoolName },
          success(response) {
            notCheckStudents.value = response;
          },
        });
      };
  
      // 启动摄像头
      const startCamera = async () => {
        try {
          const mediaStream = await navigator.mediaDevices.getUserMedia({ video: true });
          video.value.srcObject = mediaStream;
          stream.value = mediaStream;
        } catch (err) {
          errorMessage.value = '无法访问摄像头，请确保已授予摄像头权限。';
          console.error('摄像头访问失败:', err);
        }
      };


      // 关闭摄像头
      const stopCamera = () => {
        if (stream.value) {
          // 停止所有轨道（视频/音频）
          stream.value.getTracks().forEach(track => {
            track.stop(); // 停止单个轨道
          });
          
          // 清除视频元素的流引用
          if (video.value && video.value.srcObject) {
            video.value.srcObject = null;
          }
          
          stream.value = null; // 释放流对象
          console.log('摄像头已关闭');
        }
      };
  
      // 截取一帧并发送到后端
      const captureAndSendFrame = async () => {
        const videoElement = video.value;
        const canvasElement = canvas.value;
        const context = canvasElement.getContext('2d');
  
        // 设置画布尺寸与视频一致
        canvasElement.width = videoElement.videoWidth;
        canvasElement.height = videoElement.videoHeight;
  
        // 将视频帧绘制到画布上
        context.drawImage(videoElement, 0, 0, canvasElement.width, canvasElement.height);
  
        // 使用 face-api.js 检测人脸
        const detections = await faceapi.detectAllFaces(videoElement, new faceapi.TinyFaceDetectorOptions())
          .withFaceLandmarks()
          .withFaceDescriptors();
  
        // 如果有检测到人脸
        if (detections.length > 0) {
          // 将画布内容转换为 Base64 图像数据
          const imageData = canvasElement.toDataURL('image/jpeg');

          // 发送图像数据到后端
          sendImageToBackend(imageData)

          // 调整检测结果尺寸以匹配画布
          const resizedDetections = faceapi.resizeResults(detections, {
            width: canvasElement.width,
            height: canvasElement.height,
          });
  
          // 在画布上绘制人脸框
          faceapi.draw.drawDetections(canvasElement, resizedDetections);
          
        } else {
          errorMessage.value = '未检测到人脸，请正对摄像头。';
        }
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
            errorMessage.value = '签到成功: ' + response;
            currentMessage.value = response;
            notCheckStudents.value = notCheckStudents.value.filter((student) => student !== response);
            checkedStudents.value.add(response);
            console.log(checkedStudents.value);
          },
          error(resp) {
            console.log(resp.responseText);
            errorMessage.value = resp.responseText;
          },
        });
      };
  
      const loadModels = async () => {
        await faceapi.nets.tinyFaceDetector.loadFromUri('/weights');
        await faceapi.nets.faceLandmark68Net.loadFromUri('/weights');
        await faceapi.nets.faceRecognitionNet.loadFromUri('/weights');
        console.log('Models loaded successfully');
      };
  
      // 组件挂载时启动摄像头并开始定时截取
      onMounted(() => {
        loadModels().then(() => {
          startCamera();
        });
        getStudentList();
        intervalId = setInterval(function () {
          captureAndSendFrame();
        }, 1500); // 每 3 秒截取一帧
      });
  
      // 组件卸载时停止摄像头和定时器
      onUnmounted(() => {
        // if (video.value.srcObject) {
        //   video.value.srcObject.getTracks().forEach((track) => track.stop());
        // }
        stopCamera();
        if (intervalId) {
          clearInterval(intervalId);
        }
      });
  
      const stopCheckIn = () => {
        clearInterval(intervalId);
        stopCamera();
      };
  
      return {
        video,
        canvas,
        errorMessage,
        notCheckStudents,
        checkedStudents,
        currentMessage,
        stopCheckIn,
      };
    },
  };
  </script>
  
  <style scoped>
  video {
    width: 40%;
    max-width: 640px;
    border: 1px solid #ccc;
    border-radius: 8px;
    margin: 10px; /* 为视频和画布添加间距 */
    border: 1px solid #ccc; /* 为视频和画布添加边框，方便观察 */
  }
  
  .error {
    color: red;
    font-weight: bold;
  }
  
  button {
    margin-top: 5px;
  }

  .video-container {
    display: flex; /* 使用flex布局 */
    align-items: center; /* 垂直居中对齐 */
    justify-content: center; /* 水平居中对齐 */
  }

  canvas {
    width: 40%;
    margin: 10px; /* 为视频和画布添加间距 */
    border: 1px solid #ccc; /* 为视频和画布添加边框，方便观察 */
  }

  .student-list {
    display: grid;
    grid-template-columns: repeat(2, 1fr); /* 两列等宽 */
    gap: 10px;
    padding-left: 0;
    /* list-style: none; */
    list-style-position: inside; /* 原点与内容对齐 */
    padding-left: 0; /* 移除默认的左内边距 */
  }
  </style>