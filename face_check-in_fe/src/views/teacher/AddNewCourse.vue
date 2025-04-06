<template>
    <content-field>
        新建课程
        <div class="row justify-content-md-center">
        <div class="col-3">
            <form @submit.prevent="handleSubmit">
                <div class="mb-3">
                    <label for="exampleInputEmail1" class="form-label">课程名称</label>
                    <input v-model="form.courseName" type="text" class="form-control" id="course_name">
                </div>
                <div class="mb-3">
                    <label for="exampleInputPassword1" class="form-label">班级名称</label>
                    <input v-model="form.className"  type="text" class="form-control" id="class_name">
                </div>
                <div class="mb-3 form-check">
                    <label class="form-label" for="exampleCheck1">导入学生名单(excel)</label>
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
                error:function(response){
                    alert(response);
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