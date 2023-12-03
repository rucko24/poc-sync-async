import '@vaadin/common-frontend/ConnectionIndicator.js';
import '@vaadin/polymer-legacy-adapter/style-modules.js';
import '@vaadin/vaadin-lumo-styles/color-global.js';
import '@vaadin/vaadin-lumo-styles/typography-global.js';
import '@vaadin/vaadin-lumo-styles/sizing.js';
import '@vaadin/vaadin-lumo-styles/spacing.js';
import '@vaadin/vaadin-lumo-styles/style.js';
import '@vaadin/vaadin-lumo-styles/vaadin-iconset.js';

const loadOnDemand = (key) => {
  const pending = [];
  if (key === '6eb464607597ff523c7400004fc62fc989197e6d4cb582c9f3ff125137e9a295') {
    pending.push(import('./chunks/chunk-d40dd38581a8d534a99ff7d9954d3b6b215428552459ee7b664126574efa95cb.js'));
  }
  if (key === '831def49d49ef5ca9222ca2c20c6dade1f8f8634e54f4b67fd5e4345783e69d4') {
    pending.push(import('./chunks/chunk-074ccc99459dceec1eeb268eb8c94220f8019a40779c1bedb661475483f8cf4f.js'));
  }
  if (key === '80a125c5dfd0dc6a78a97601f4736319a6f12eb922f67632e0adb9caf5ebd036') {
    pending.push(import('./chunks/chunk-0ab5b1a1a6d0d83adfad2b37fb60879dbe666f1aa094b345791e2580d01566d7.js'));
  }
  return Promise.all(pending);
}

window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;