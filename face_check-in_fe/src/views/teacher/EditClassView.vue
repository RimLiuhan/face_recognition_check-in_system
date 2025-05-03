<template>
    <content-field>
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
            <td><button class="btn btn-danger" @click="deleteStudent(student.id)">删除学生</button></td>
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
    const store = useStore();
    const route = useRoute();
    const major = route.params.major;
    const schoolName = route.params.schoolName;

    let newId = ref('');
    let newUsername = ref('');
    let newFaceFeatures = "未登记";

    
    const getStudentList = async () => {
        $.ajax({
          url: 'http://127.0.0.1:3007/getAllStudentsInfo/',
          type: 'GET',
          headers: {
            Authorization: 'Bearer ' + store.state.user.token,
          },
          data: { major, schoolName },
          success(response) {
            students.value = response;
          },
        });
    };

    getStudentList();

    const addStudent = () => {
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

    const deleteStudent = (id) => {
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
        deleteStudent
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