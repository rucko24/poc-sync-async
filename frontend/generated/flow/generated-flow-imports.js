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
  if (key === '80a125c5dfd0dc6a78a97601f4736319a6f12eb922f67632e0adb9caf5ebd036') {
    pending.push(import('./chunks/chunk-9061cd7961f40d56a358da6d87826f405f5dcc8a0ccbcde8697577b1e97a9d91.js'));
  }
  if (key === '6eb464607597ff523c7400004fc62fc989197e6d4cb582c9f3ff125137e9a295') {
    pending.push(import('./chunks/chunk-d40dd38581a8d534a99ff7d9954d3b6b215428552459ee7b664126574efa95cb.js'));
  }
  if (key === '831def49d49ef5ca9222ca2c20c6dade1f8f8634e54f4b67fd5e4345783e69d4') {
    pending.push(import('./chunks/chunk-ee4b742e37d9ada3a797b31b6d3f524ee2fa488fbb332284231f5cff2aa3e174.js'));
  }
  return Promise.all(pending);
}

window.Vaadin = window.Vaadin || {};
window.Vaadin.Flow = window.Vaadin.Flow || {};
window.Vaadin.Flow.loadOnDemand = loadOnDemand;