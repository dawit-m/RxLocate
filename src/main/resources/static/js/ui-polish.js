/* RxLocate Phase 7 — lightweight interaction polish, no framework required. */
(() => {
  const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  const reveal = () => {
    const items = document.querySelectorAll('[data-reveal]');
    if (!items.length) return;
    if (reduceMotion || !('IntersectionObserver' in window)) {
      items.forEach((el) => el.classList.add('is-revealed'));
      return;
    }
    const observer = new IntersectionObserver((entries, obs) => {
      entries.forEach((entry) => {
        if (!entry.isIntersecting) return;
        entry.target.classList.add('is-revealed');
        obs.unobserve(entry.target);
      });
    }, { threshold: 0.12, rootMargin: '0px 0px -40px' });
    items.forEach((el) => observer.observe(el));
  };

  const closeOnEscape = () => {
    document.addEventListener('keydown', (event) => {
      if (event.key !== 'Escape') return;
      const dialog = document.querySelector('dialog[open]');
      if (dialog && typeof dialog.close === 'function') dialog.close();
    });
  };

  const confirmForms = () => {
    document.addEventListener('submit', (event) => {
      const form = event.target.closest('form[data-confirm]');
      if (!form) return;
      const message = form.dataset.confirm || 'Are you sure?';
      if (!window.confirm(message)) event.preventDefault();
    });
  };

  const markPageReady = () => {
    requestAnimationFrame(() => document.documentElement.classList.add('is-ready'));
  };

  reveal();
  closeOnEscape();
  confirmForms();
  markPageReady();
})();
