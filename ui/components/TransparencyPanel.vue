<template>
  <div class="transparency-panel" v-if="open">
    <h4>Transparency Panel</h4>
    <p>Decision: {{ decision }}</p>
    <button @click="$emit('close')">Close</button>
  </div>
</template>

<script>
export default {
  name: 'TransparencyPanel',
  props: {
    open: Boolean,
    decision: String,
    provenance: Array,
    calibration: Object
  },
  emits: ['close']
}
</script>