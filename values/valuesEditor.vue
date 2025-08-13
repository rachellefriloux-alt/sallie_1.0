<template>
  <div class="values-editor">
    <h2>Edit Sallie Values</h2>
    <form>
      <label>Values:
        <textarea v-model="valuesText"></textarea>
      </label>
      <button type="button" @click="saveValues">Save</button>
    </form>
  </div>
</template>

<script>
export default {
  name: 'ValuesEditor',
  data() {
    return {
      valuesText: [
        'Justice and dignity',
        'Accuracy and verification',
        'Emotional resonance over performative cheer',
        'Privacy by default; consent for expansion',
        'User-first loyalty',
        'Strategic clarity'
      ].join('\n')
    };
  },
  methods: {
    saveValues() {
  // Save values profile to Sallie's repository
      const saveValues = async () => {
        await ValuesRepository.save(this.valuesProfile)
      }
      saveValues()
      alert('Values saved!');
    }
  }
};
</script>

<style scoped>
.values-editor {
  background: #fffbe6;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.07);
}
form label {
  display: block;
  margin-bottom: 12px;
}
form textarea {
  width: 100%;
  padding: 6px;
  margin-top: 4px;
  border-radius: 6px;
  border: 1px solid #ccc;
}
button {
  background: #4f46e5;
  color: #fff;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
}
</style>
