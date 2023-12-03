import { unsafeCSS, registerStyles } from '@vaadin/vaadin-themable-mixin/register-styles';

import vaadinButtonCss from 'themes/demo-sync-vs-async/components/vaadin-button.css?inline';


if (!document['_vaadintheme_demo-sync-vs-async_componentCss']) {
  registerStyles(
        'vaadin-button',
        unsafeCSS(vaadinButtonCss.toString())
      );
      
  document['_vaadintheme_demo-sync-vs-async_componentCss'] = true;
}

if (import.meta.hot) {
  import.meta.hot.accept((module) => {
    window.location.reload();
  });
}

