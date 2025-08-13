<template>
  <div class="persona-editor">
    <h2>Edit Persona Profiles</h2>
    <form>
      <label>Persona Name:
        <input type="text" v-model="persona.name" />
      </label>
      <label>Tone:
        <input type="text" v-model="persona.tone" />
      </label>
      <label>Validation:
        <input type="text" v-model="persona.validation" />
      </label>
      <label>Fallback:
        <input type="text" v-model="persona.fallback" />
      </label>
      <label>Lexicon:
        <textarea v-model="lexiconText"></textarea>
      </label>
      <button type="button" @click="savePersona">Save</button>
    </form>
  </div>
</template>

<script>
export default {
  name: 'PersonaEditor',
  data() {
    return {
      persona: {
        name: '',
        tone: '',
        validation: '',
        fallback: ''
      },
      lexiconText: ''
    };
  },
  methods: {
    savePersona() {
      // TODO: Save logic for persona profile
      alert('Persona saved!');
    }
  }
};
</script>

<style scoped>
.persona-editor {
  background: #fffbe6;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.07);
}
form label {
  display: block;
  margin-bottom: 12px;
}
form input, form textarea {
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
