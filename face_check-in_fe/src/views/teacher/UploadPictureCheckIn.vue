<template>
    <content-field>
      <input 
        ref="fileInput" 
        class="form-control" 
        type="file" 
        id="formFile" 
        accept="image/*"
        @change="handleFileChange"
      >
      <button @click="upload" type="button" class="btn btn-primary">上传</button>
    </content-field>
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
  import ContentField from '@/components/ContentField.vue';
  import { useStore } from 'vuex';
  import { onMounted, ref } from 'vue';
  import { useRoute } from 'vue-router';
  import $ from 'jquery';
  
  export default {
    components: { ContentField },
    setup() {
      const fileInput = ref(null);
      const selectedFile = ref(null); // 新增：存储选择的文件
      const store = useStore();
      const route = useRoute();
      const major = route.params.major;
      const schoolName = route.params.schoolName;

      let notCheckStudents = ref([]);
      let checkedStudents = ref(new Set());
      let currentMessage = ref('请人脸正对摄像头');
  
      // 新增：处理文件选择变化
      const handleFileChange = (event) => {
        selectedFile.value = event.target.files[0];
      };
  
    //   const upload = () => {
    //     // 修改：使用selectedFile而不是直接访问DOM
    //     if (!selectedFile.value) {
    //       alert('请选择一张图片');
    //       return;
    //     }
  
    //     const file = selectedFile.value;
    //     // 检查文件大小 (10MB = 10 * 1024 * 1024 bytes)
    //     const maxSize = 10 * 1024 * 1024; // 10MB
    //     if (file.size > maxSize) {
    //       alert('图片大小不能超过10MB');
    //       selectedFile.value = null;
    //       if (fileInput.value) {
    //         fileInput.value.value = ''; // 清空文件输入
    //       }
    //       return;
    //     }
  
    //     const formData = new FormData();
    //     formData.append('photo', file, store.state.user.id + store.state.user.username + ".jpg");
    //     formData.append('major', major);
    //     formData.append('schoolName', schoolName);
  
    //     $.ajax({
    //       url: "http://127.0.0.1:3007/uploadPictureCheckIn/",
    //       type: 'POST',
    //       data: formData,
    //       processData: false,
    //       contentType: false,
    //       headers: {
    //         Authorization: "Bearer " + store.state.user.token,
    //       },
    //       success: function(response) {
    //         response.forEach(element => {
    //             checkedStudents.value.add(element);
    //             notCheckStudents.value = notCheckStudents.value.filter((student) => student !== element);
    //         });
    //         alert('上传成功');
    //         // 上传成功后清空选择
    //         selectedFile.value = null;
    //         if (fileInput.value) {
    //           fileInput.value.value = '';
    //         }
    //       },
    //       error: function(response) {
    //         alert(response.responseText || '上传失败');
    //       }
    //     });
    //   };
    const upload = async () => {
        if (!selectedFile.value) {
            alert('请选择一张图片');
            return;
        }

        const formData = new FormData();
        // formData.append('photo', selectedFile.value);
        formData.append('photo', selectedFile.value, 
    `${store.state.user.id}_${store.state.user.username}.jpg`);
        formData.append('major', major);
        formData.append('schoolName', schoolName);

        try {
            const response = await $.ajax({
            url: "http://127.0.0.1:3007/uploadPictureCheckIn/",
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            headers: { Authorization: "Bearer " + store.state.user.token }
            });

            // 处理响应（确保是数组）
            const newStudents = Array.isArray(response) ? response : [];
            
            // 更新状态
            newStudents.forEach(name => {
            checkedStudents.value.add(name);
            notCheckStudents.value = notCheckStudents.value.filter(n => n !== name);
            });

            alert(`成功识别 ${newStudents.length} 名学生`);
            
        } catch (error) {
            alert(error.responseJSON?.join(', ') || '识别失败');
        } finally {
            // 重置表单
            selectedFile.value = null;
            if (fileInput.value) fileInput.value.value = '';
        }
    };

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

      onMounted(() => {
        getStudentList();
      });
  
      return {
        fileInput,
        upload,
        handleFileChange, // 暴露handleFileChange到模板
        notCheckStudents,
        checkedStudents,
        currentMessage
      };
    }
  }
  </script>