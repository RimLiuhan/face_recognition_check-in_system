<template>
    <content-field>
        <div class="search-bar" style="margin-bottom: 20px; padding: 15px; background: #f8f9fa; border-radius: 5px;">
          <div class="row g-3">
              <div class="col-md-3">
                  <div class="input-group">
                      <span class="input-group-text">课程名</span>
                      <input type="text" class="form-control" v-model="searchParams.course" placeholder="输入课程名">
                  </div>
              </div>
              <div class="col-md-3">
                  <div class="input-group">
                      <span class="input-group-text">学校</span>
                      <input type="text" class="form-control" v-model="searchParams.schoolName" placeholder="输入学校">
                  </div>
              </div>
              <div class="col-md-2">
                  <div class="input-group">
                      <span class="input-group-text">班级</span>
                      <input type="text" class="form-control" v-model="searchParams.major" placeholder="输入班级">
                  </div>
              </div>
              <div class="col-md-2">
                  <div class="input-group">
                      <span class="input-group-text">教师</span>
                      <input type="text" class="form-control" v-model="searchParams.teacher" placeholder="输入教师">
                  </div>
              </div>
              <div class="col-md-2 d-flex align-items-center">
                  <button class="btn btn-primary me-2" @click="searchCourses">查询</button>
                  <button class="btn btn-secondary" @click="resetSearch">重置</button>
              </div>
          </div>
      </div>

        <div class="card" style="background-color:black; color: white;">
            <div class="row">
                <div class="col-3">课程名</div>
                <div class="col-2">学校 - 班级</div>
                <div class="col-2">教师</div>
                <div class="col-2 ms-auto">班级详情</div>
                <div class="col-2 ms-auto">操作</div>
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
    let allCourses = ref([]);
    const store = useStore();

    // 查询参数
    const searchParams = ref({
      course: '',
      schoolName: '',
      major: '',
      teacher: ''
    });

    const getAllCourses = () => {
        $.ajax({
            url: "http://127.0.0.1:3007/administrator/courselist/",
            type: "GET",
            headers: {
                Authorization: "Bearer " + store.state.user.token,
            },
            success(resp) {
                allCourses.value = resp;
                courses.value = resp;
            }
        }) 
    }

    // 查询课程
    const searchCourses = () => {
      if (!searchParams.value.course && !searchParams.value.schoolName && 
          !searchParams.value.major && !searchParams.value.teacher) {
        courses.value = allCourses.value;
        return;
      }

      courses.value = allCourses.value.filter(course => {
        return (
          (!searchParams.value.course || course.course.includes(searchParams.value.course)) &&
          (!searchParams.value.schoolName || course.schoolName.includes(searchParams.value.schoolName)) &&
          (!searchParams.value.major || course.major.includes(searchParams.value.major)) &&
          (!searchParams.value.teacher || course.teacher.includes(searchParams.value.teacher))
        );
      });
    };

    // 重置查询
    const resetSearch = () => {
      searchParams.value = {
        course: '',
        schoolName: '',
        major: '',
        teacher: ''
      };
      courses.value = allCourses.value;
    };

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
        deleteCourse,
        searchCourses,
        resetSearch,
        searchParams
    }
  }
    
}
</script>

<style scoped>
    .card{
        margin-bottom: 10px;
    }
</style>