<template>
    <nav class="navbar navbar-expand-lg bg-body-tertiary">
        <div class="container">
            <a class="navbar-brand" href="#">人脸识别签到系统</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarText" aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarText">
            <div class="navbar-text ms-auto">
                你好: {{ $store.state.user.username }}
            </div>
            </div>
        </div>
    </nav>
</template>

<script>
export default {
    setup() {
    
    }

}
</script>

<style scoped>
    
</style>