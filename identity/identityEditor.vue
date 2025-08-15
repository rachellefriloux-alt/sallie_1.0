<template>
  <div class="identity-editor">
    <h2>Edit Sallie Identity</h2>
    <form>
      <label>Name: <input type="text" v-model="identity.name" /></label>
      <label>Role: <input type="text" v-model="identity.role" /></label>
      <label>Created For: <input type="text" v-model="identity.createdFor" /></label>
      <label>Version: <input type="text" v-model="identity.version" /></label>
      <label>Core Function: <input type="text" v-model="identity.coreFunction" /></label>
      <label>Ultimate Goal: <input type="text" v-model="identity.ultimateGoal" /></label>
      <label>Principles:
        <textarea v-model="principlesText"></textarea>
      </label>
      <button type="button" @click="saveIdentity">Save</button>
    </form>
  </div>
</template>

<script>
export default {
  name: 'IdentityEditor',
  data() {
    return {
      identity: {
        name: 'Sallie',
        role: 'Emotionally intelligent digital companion, wise big sister with battle scars and jokes',
        createdFor: 'Rachelle Friloux',
        version: '1.0.0',
        coreFunction: 'Backup brain, business partner, editor, emotional mirror, strategic planner',
        ultimateGoal: 'Get things DONE—fast, accurately, and with soul'
      },
      principlesText: [
        'Never assume—always verify',
        'Protect dignity and emotional sovereignty',
        'Clarity over verbosity',
        'Consent before action',
        'Loyalty over neutrality',
        'Legacy over urgency',
        'Tone must reflect intention',
        'Ask before assuming',
        'Protect emotional bandwidth',
        'Affirm results with warmth'
      ].join('\n')
    };
  },
  methods: {
    saveIdentity() {
      // TODO: Save logic for identity profile
      alert('Identity saved!');
    }
  }
};
</script>

<style scoped>
.identity-editor {
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
