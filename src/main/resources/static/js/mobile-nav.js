(() => {
  const toggle = document.querySelector('[data-site-nav-toggle]');
  const nav = document.getElementById('siteNav');
  if (!toggle || !nav) return;
  const close = () => { document.body.classList.remove('site-nav-open'); toggle.setAttribute('aria-expanded', 'false'); };
  toggle.addEventListener('click', () => { const open = !document.body.classList.contains('site-nav-open'); document.body.classList.toggle('site-nav-open', open); toggle.setAttribute('aria-expanded', String(open)); if (open) nav.querySelector('a')?.focus(); });
  nav.querySelectorAll('a').forEach(link => link.addEventListener('click', close));
  document.addEventListener('click', event => { if (!event.target.closest('.site-header')) close(); });
  document.addEventListener('keydown', event => { if (event.key === 'Escape') close(); });
})();
