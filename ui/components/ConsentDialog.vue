<template>
  <div class="consent-dialog">
    <h3>Consent Required</h3>
    <p>New features detected. Grant consent to proceed?</p>
    <button @click="$emit('accept')">Accept</button>
    <button @click="$emit('decline')">Decline</button>
  </div>
</template>

<script>
export default {
  name: 'ConsentDialog',
  emits: ['accept', 'decline']
}
</script>

<style scoped>
.consent-dialog {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}
</style>