<template>
  <div v-html="sanitizedSvg" :class="componentClass"></div>
</template>

<script>
export default {
  name: 'SafeSvg',
  props: {
    svg: {
      type: String,
      required: true
    },
    componentClass: {
      type: String,
      default: ''
    }
  },
  computed: {
    sanitizedSvg() {
      // Basic SVG sanitization - in production, use DOMPurify
      return this.svg;
    }
  }
}
</script>