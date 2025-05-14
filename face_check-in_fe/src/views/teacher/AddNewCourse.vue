<template>
    <content-field>
        新建课程
        <div class="row justify-content-md-center" style="margin-top: 10px;">
        <div class="col-3">
            <form @submit.prevent="handleSubmit">
                <div class="mb-3">
                    <label class="form-label">学校名称</label>
                    <input v-model="form.schoolName" type="text" class="form-control" id="school_name">
                </div>
                <div class="mb-3">
                    <label class="form-label">课程名称</label>
                    <input v-model="form.courseName" type="text" class="form-control" id="course_name">
                </div>
                <div class="mb-3">
                    <label class="form-label">班级名称</label>
                    <input v-model="form.className"  type="text" class="form-control" id="class_name">
                </div>
                <div class="mb-3 form-check">
                    <label class="form-label" for="exampleCheck1">导入学生名单(excel, 注意表格中包含"学号"和"姓名"属性列)</label>
                    <input ref="fileInput" class="form-control" type="file" @change="handleFileChange" accept=".xlsx, .xls, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">
                </div>
                <button type="submit" class="btn btn-primary">Submit</button>
            </form>
        </div>
        </div>
    </content-field>
</template>

<script>
import ContentField from '@/components/ContentField.vue';
import { ref } from 'vue';
import $ from 'jquery';
import { useStore } from 'vuex';

export default {
    components: {
      ContentField  
    },
    setup() {
        const store = useStore();
        const form = ref({
            schoolName: '',
            courseName: '',
            className: ''
        })
        const fileInput = ref(null);
        const selectedFile = ref(null);
        const handleFileChange = (e) => {
            selectedFile.value = e.target.files[0];
        };

        const handleSubmit = () => {
            if (!selectedFile.value) {
                alert('请选择文件');
                return;
            }
            const formData = new FormData();
            formData.append('schoolName', form.value.schoolName);
            formData.append('courseName', form.value.courseName);
            formData.append('className', form.value.className);
            formData.append('teacherId', store.state.user.id);
            formData.append('studentList', selectedFile.value);
            
            $.ajax({
                url:"http://127.0.0.1:3007/course/add/",
                type:'POST',
                data:formData,
                processData:false,
                contentType:false,
                headers:{
                    Authorization: "Bearer " + store.state.user.token,
                },
                success:function(response){
                    alert(response);
                },
                error:function(xhr){
                    // 更详细的错误处理
                    let errorMsg = "请求失败";
                    if (xhr.responseJSON && xhr.responseJSON.message) {
                        errorMsg = xhr.responseJSON.message;
                    } else if (xhr.responseText) {
                        errorMsg = xhr.responseText;
                    }
                    alert(errorMsg);
                }
            })
        }
        
        return {
            form,
            fileInput,
            selectedFile,
            handleFileChange,
            handleSubmit,
        };
    }
}
</script>

<style scoped>
    
</style>