<template>
    <content-field>
        <div class="row">
            <div v-for="student in students" :key="student">{{ student }}</div>
        </div>
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
    
    const getStudentList = async () => {
        $.ajax({
          url: 'http://127.0.0.1:3007/getAllStudents/',
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

    return {
        students
    }
  }
}
</script>

<style scoped>
    
</style>