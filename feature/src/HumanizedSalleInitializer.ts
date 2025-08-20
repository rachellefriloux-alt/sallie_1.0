/*
Salle Persona Module: HumanizedSalleInitializer
Initializes the Humanized Salle features and registers them with the core system.
Follows Salle architecture, modularity, and privacy rules.
*/

import { HumanizedSallePlugin } from './HumanizedSallePlugin';
import { PluginRegistry } from '../../core/PluginRegistry';

export class HumanizedSalleInitializer {
  private plugin: HumanizedSallePlugin;
  
  constructor(private registry: PluginRegistry) {
    this.plugin = new HumanizedSallePlugin();
  }
  
  /**
   * Initialize and register the humanized features
   */
  initialize(): boolean {
    try {
      const registration = this.plugin.register();
      this.registry.registerPlugin(registration.id, registration.handlers);
      console.log('Humanized Salle features initialized successfully');
      return true;
    } catch (error) {
      console.error('Failed to initialize Humanized Salle features:', error);
      return false;
    }
  }
}
