<template>
    <div class="container">
        <div class="card">
            <div class="card-body">
                <slot></slot>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name:"ContentField",

}
</script>

<style scoped>
.container {
    margin-top: 10px;
}
</style>