// Plugin Registry
export const pluginRegistry = {
  plugins: [],
  register(plugin) {
    this.plugins.push(plugin);
    console.log('Registered plugin:', plugin.id);
  },
  getAll() {
    return this.plugins;
  }
};