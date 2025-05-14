<template>
    管理员页面
    <content-field>
        <div class="card">
            <div class="row">
                <div class="col-3">课程名</div>
                <div class="col-2">学校 - 班级</div>
                <div class="col-2">教师</div>
                <div class="col-2 ms-auto">班级详情</div>
                <div class="col-2 ms-auto">删除</div>
            </div>
        </div>
        <div class="card" v-for="(course, index) in courses" :key="index">
            <div class="card-body">
                <div class="row">
                    <div class="col-3">{{ course.course }}</div>
                    <div class="col-2">{{ course.schoolName }} - {{ course.major }}</div>
                    <div class="col-2">{{ course.teacher }}</div>
                    <div class="col-2 ms-auto">
                        <button type="button" class="btn btn-success" @click="check(course.major, course.schoolName)">
                            班级详情
                        </button>
                    </div>
                    <div class="col-2 ms-auto">
                        <button type="button" class="btn btn-danger" @click="deleteCourse(course.course, course.schoolName, course.major, course.teacher)">
                            删除
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </content-field>
</template>

<script>
import ContentField from '@/components/ContentField.vue';
import { useStore } from 'vuex';
import { ref } from 'vue';
import $ from 'jquery';
import router from '@/router';

export default {
  components: { ContentField },
  setup() {
    let courses = ref([]);
    const store = useStore();

    const getAllCourses = () => {
        $.ajax({
            url: "http://127.0.0.1:3007/administrator/courselist/",
            type: "GET",
            headers: {
                Authorization: "Bearer " + store.state.user.token,
            },
            success(resp) {
                courses.value = resp;
            }
        }) 
    }

    getAllCourses();

    const check = (major, schoolName) => {
        router.push({
            name: "editclass",
            params: {
                major,
                schoolName
            }
        })
    }

    const deleteCourse = (course, schoolName, major, teacher) => {
        const isConfirmed = confirm(`确定要删除课程 ${course} (教师: ${teacher}, 学校: ${schoolName}, 班级: ${major})吗？`);
        
        if (!isConfirmed) {
            return; // If user cancels, do nothing
        }
        $.ajax({
            url: "http://127.0.0.1:3007/administrator/deleteCourse/",
            type: "POST",
            headers: {
                Authorization: "Bearer " + store.state.user.token,
            },
            data: {
                course,
                schoolName,
                major,
                teacher
            },
            success(resp) {
                if (resp === "success") {
                    alert("删除成功");
                    getAllCourses();
                }
                else alert(resp.error_message);
            },
            error(xhr, status, error) {
                // 添加AJAX请求失败的处理
                console.error("删除课程失败:", error);
                alert("删除失败: " + (xhr.responseJSON?.message || "服务器错误"));
            }
        })
    }

    return{
        courses,
        check,
        deleteCourse
    }
  }
    
}
</script>

<style scoped>
    .card{
        margin-bottom: 10px;
    }
</style>