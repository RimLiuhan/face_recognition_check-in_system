<template>
    <content-field>
        课程列表
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
                    <button type="button" class="btn btn-info" @click="publish2(course.major, course.schoolName)">
                        发布签到②
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
        return {
            courses,
            publish,
            check,
            publish2
        }
    }
}
</script>

<style scoped>
.card {
    margin-top: 7px;
}

</style>