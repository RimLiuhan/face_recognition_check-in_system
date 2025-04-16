import $ from 'jquery';

export default {
    state: {
        usertype: "",
        id: "",
        username: "",
        token:"",
        is_login: false,
    },
    getters: {
    },
    mutations: {
        updateUser(state, user) {
            state.id = user.id;
            state.usertype = user.usertype;
            state.username = user.username;
            state.is_login = user.is_login;
        },
        updateToken(state, token) {
            state.token = token;
        },
        logout(state) {
            state.usertype = '';
            state.id = '';
            state.username = '';
            state.token = '';
            state.is_login = false;
        }
    },
    actions: {
        login(context, data) {
            $.ajax({
                url: "http://127.0.0.1:3007/user/account/token/",
                type: "post",
                data: {
                  usertype: data.usertype,  
                  username: data.username,
                  password: data.password,
                  id: data.id
                },
                success(resp) {
                    if (resp.error_message === 'success') {
                        localStorage.setItem("jwt_token", resp.token);
                        context.commit("updateToken", resp.token);
                        data.success(resp);
                    } else {
                        data.error(resp);
                    }
                },
                error(resp) {
                  data.error(resp);
                }
              });
        },
        getinfo(context, data) {
            $.ajax({
                async: false,
                url: "http://127.0.0.1:3007/user/account/info/",
                type: "get",
                headers: {
                    Authorization: "Bearer " + context.state.token
                },
                success(resp) {
                    if (resp.error_message === 'success') {
                        console.log(resp);
                        context.commit("updateUser", {
                            ...resp,
                            is_login: true
                        })
                        data.success(); 
                    } else {
                        data.error();
                    }
                },
                error() {
                    data.error();
                }
            })
        },
        logout(context) {
            localStorage.removeItem("jwt_token");
            context.commit("logout");
        },
    },
    modules: {
    }
}