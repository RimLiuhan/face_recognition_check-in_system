<template>
    <content-field>
        <div style="background: #f8f9fa; border-radius: 5px; font-size: large;">课程列表</div>
    <div class="card" v-for="(course, index) in courses" :key="index">
        <div class="card-body">
            <div class="row">
                <div class="col-3">{{ course.course }}</div>
                <div class="col-2">{{ course.schoolName }} - {{ course.major }}</div>
                <div class="col-6 ms-auto">
                    <button style="margin-right: 5px;" type="button" class="btn btn-success" @click="check(course.major, course.schoolName)">
                        班级详情
                    </button>
                    <button style="margin-right: 5px;" type="button" class="btn btn-primary" @click="publish(course.major, course.schoolName)">
                        发布签到①
                    </button>
                    <button style="margin-right: 5px;" type="button" class="btn btn-info" @click="publish2(course.major, course.schoolName)">
                        发布签到②
                    </button>
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
import $ from 'jquery';
import { ref } from 'vue';
import { useStore } from 'vuex';
import router from '@/router';

export default {
    components: {
        ContentField
    }, 
    setup() {
        let courses = ref([]);
        const store = useStore();
        const getAllCourses = () => {
            $.ajax({
                url: "http://127.0.0.1:3007/teacher/courselist/",
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
        
        const publish = (major, schoolName) => {
            router.push({
                name: "camera",
                params: {
                    major,
                    schoolName
                }
            })
        }

        const publish2 = (major, schoolName) => {
            router.push({
                name: "uploadpicture",
                params: {
                    major,
                    schoolName
                }
            })
        }

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

        return {
            courses,
            publish,
            check,
            publish2,
            deleteCourse
        }
    }
}
</script>

<style scoped>
.card {
    margin-top: 7px;
}

</style>