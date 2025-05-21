<template>
    <content-field>
      <div class="row search-bar" style="margin-bottom: 20px; padding: 15px; background: #f8f9fa; border-radius: 5px;">
        <div class="col-md-2">
          <div class="input-group">
            <span class="input-group-text">学号</span>
            <input type="text" class="form-control" v-model="searchParams.id" placeholder="输入学号">
          </div>
        </div>
        <div class="col-md-2">
          <div class="input-group">
            <span class="input-group-text">姓名</span>
            <input type="text" class="form-control" v-model="searchParams.username" placeholder="输入姓名">
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
            <span class="input-group-text">学校</span>
            <input type="text" class="form-control" v-model="searchParams.schoolName" placeholder="输入学校">
          </div>
        </div>
        <div class="col-md-4 mt-2">
          <button class="btn btn-primary me-2" @click="searchStudents">查询</button>
          <button class="btn btn-secondary" @click="resetSearch">重置</button>
        </div>
      </div>
      
      <table class="table table-dark table-striped">
        <thead>
          <tr>
            <th>学号</th>
            <th>姓名</th>
            <th>班级</th>
            <th>学校</th>
            <th>是否已登记人脸</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="student in students" :key="student.id">
            <td>{{ student.id }}</td>
            <td>{{ student.username }}</td>
            <td>{{ student.major }}</td>
            <td>{{ student.schoolName }}</td>
            <td>
              {{ !student.faceFeatures ? '未登记' : '已登记' }}
            </td>
            <td>
              <button class="btn btn-danger" @click="deleteStudent(student.id, student.username, student.major, student.schoolName)">删除学生</button>
            </td>
          </tr>
          <tr>
            <td><input class="text" v-model="newId"/></td>
            <td><input class="text" v-model="newUsername"/></td>
            <td>{{ major }}</td>
            <td>{{ schoolName }}</td>
            <td>{{ newFaceFeatures }}</td>
            <td><button class="btn btn-success" @click="addStudent">添加学生</button></td>
          </tr>
        </tbody>
      </table>
    </content-field>
</template>

<script>
import ContentField from '@/components/ContentField.vue';
import { ref } from 'vue';
import { useRoute } from 'vue-router';
import { useStore } from 'vuex';
import $ from 'jquery';

export default {
  components: { ContentField },
  setup() {
    let students = ref([]);
    let allStudents = ref([]);
  
    const store = useStore();
    const route = useRoute();
    const major = route.params.major;
    const schoolName = route.params.schoolName;

    let newId = ref('');
    let newUsername = ref('');
    let newFaceFeatures = "未登记";

    // 查询参数
    const searchParams = ref({
      id: '',
      username: '',
      major: '',
      schoolName: ''
    });

    
    const getStudentList = async () => {
        $.ajax({
          url: 'http://127.0.0.1:3007/getAllStudentsInfo/',
          type: 'GET',
          headers: {
            Authorization: 'Bearer ' + store.state.user.token,
          },
          data: { major, schoolName },
          success(response) {
            allStudents.value = response;
            students.value = response;
          },
        });
    };

    // 查询学生
    const searchStudents = () => {
      if (!searchParams.value.id && !searchParams.value.username && 
          !searchParams.value.major && !searchParams.value.schoolName) {
        students.value = allStudents.value;
        return;
      }

      students.value = allStudents.value.filter(student => {
        return (
          (!searchParams.value.id || student.id.toString().includes(searchParams.value.id)) &&
          (!searchParams.value.username || student.username.includes(searchParams.value.username)) &&
          (!searchParams.value.major || student.major.includes(searchParams.value.major)) &&
          (!searchParams.value.schoolName || student.schoolName.includes(searchParams.value.schoolName))
        )
      });
    };

    // 重置查询
    const resetSearch = () => {
      searchParams.value = {
        id: '',
        username: '',
        major: '',
        schoolName: '' 
      };
      students.value = allStudents.value;
    };

    getStudentList();

    const addStudent = () => {
      // 检查学号和姓名是否为空
      if (!newId.value.trim()) {
        alert("学号不能为空");
        return;
      }
      
      if (!newUsername.value.trim()) {
        alert("姓名不能为空");
        return;
      }
      
      $.ajax({
        url: 'http://127.0.0.1:3007/addNewStudent/',
        type: 'POST',
        headers: {
          Authorization: 'Bearer ' + store.state.user.token,
        },
        data: { newId:newId.value, newUsername:newUsername.value, major, schoolName },
        success(response) {
          if (response.error_message === "success")
            alert("添加成功");
          else alert(response.error_message);
          getStudentList();
        }
      })
      newId.value = '';
      newUsername.value = '';
    }

    const deleteStudent = (id, username, major, schoolName) => {
      const isConfirmed = confirm(`确定要删除学号为 ${id} (姓名: ${username}, 学校: ${schoolName}, 班级: ${major})的学生吗？`);
        
      if (!isConfirmed) {
          return; // If user cancels, do nothing
      }

      $.ajax({
        url: 'http://127.0.0.1:3007/deleteStudent/',
        type: 'POST',
        headers: {
          Authorization: 'Bearer ' + store.state.user.token,
        },
        data: { id },
        success(response) {
          if (response.error_message === "success")
            alert("删除成功");
          getStudentList();
        }
      })
    }

    return {
        students,
        newId,
        newUsername,
        major,
        schoolName,
        newFaceFeatures,
        addStudent,
        deleteStudent,
        allStudents,
        searchParams,
        resetSearch,
        searchStudents
    }
  }
}
</script>

<style scoped>
    .unregistered {
      background-color: purple;
      color: white; /* 可选：白色文字提高可读性 */
    }
</style>