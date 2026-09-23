/* RxLocate finder — fast, calm discovery with touch-first contact actions. */
(() => {
  const input = document.getElementById('medicineSearch');
  const clear = document.querySelector('[data-clear-search]');
  const list = document.getElementById('resultsList');
  const filterGroup = document.getElementById('stockFilter');
  const sort = document.getElementById('sortResults');
  const emptyNote = document.getElementById('filterEmpty');
  const form = input?.closest('form');

  if (clear && input) {
    const syncClear = () => { clear.hidden = input.value.trim().length === 0; clear.setAttribute('aria-hidden', String(clear.hidden)); };
    input.addEventListener('input', syncClear);
    clear.addEventListener('click', () => { input.value = ''; input.focus(); syncClear(); });
    syncClear();
  }
  document.addEventListener('keydown', event => { if (event.key !== '/' || event.metaKey || event.ctrlKey || event.altKey) return; const tag = document.activeElement?.tagName?.toLowerCase(); if (['input','textarea','select'].includes(tag)) return; event.preventDefault(); input?.focus(); });

  /* On desktop, make the phone action reveal a useful contact card. On mobile, keep the native tel: action. */
  const isTouch = () => window.matchMedia('(hover: none), (max-width: 680px)').matches;
  const closePhones = except => document.querySelectorAll('.phone-popover').forEach(popover => { if (popover !== except) popover.hidden = true; });
  document.querySelectorAll('.search-phone-action').forEach(action => {
    action.addEventListener('click', event => {
      if (isTouch()) return;
      event.preventDefault();
      const popover = action.parentElement.querySelector('.phone-popover');
      if (!popover) return;
      closePhones(popover);
      popover.querySelector('strong').textContent = action.dataset.pharmacy || 'Pharmacy';
      const number = popover.querySelector('.phone-popover__number');
      number.textContent = action.dataset.phone || 'Phone number unavailable';
      number.href = `tel:${action.dataset.phone || ''}`;
      popover.hidden = false;
    });
  });
  document.querySelectorAll('.phone-popover__close').forEach(button => button.addEventListener('click', () => { button.closest('.phone-popover').hidden = true; }));
  document.addEventListener('click', event => { if (!event.target.closest('.search-luxury__actions')) closePhones(); });
  window.addEventListener('resize', () => { if (isTouch()) closePhones(); });

  if (!list || !filterGroup || !sort) return;
  const rows = [...list.children];
  let mode = 'all';
  const storedSort = window.sessionStorage.getItem('rxlocate-sort');
  if (storedSort && [...sort.options].some(option => option.value === storedSort)) sort.value = storedSort;
  const announce = message => { let region = document.getElementById('finderStatus'); if (!region) { region = document.createElement('p'); region.id = 'finderStatus'; region.className = 'sr-only'; region.setAttribute('role','status'); region.setAttribute('aria-live','polite'); list.parentElement?.insertBefore(region, list); } region.textContent = message; };
  const render = () => {
    rows.forEach(row => { row.hidden = mode !== 'all' && row.dataset.status !== mode; });
    const visible = rows.filter(row => !row.hidden);
    const by = {'price-low':(a,b)=>Number(a.dataset.price)-Number(b.dataset.price),'price-high':(a,b)=>Number(b.dataset.price)-Number(a.dataset.price),'stock-high':(a,b)=>Number(b.dataset.stock)-Number(a.dataset.stock),relevance:(a,b)=>rows.indexOf(a)-rows.indexOf(b)}[sort.value] || (()=>0);
    visible.sort(by).forEach(row => list.appendChild(row)); list.hidden = visible.length === 0; if (emptyNote) emptyNote.hidden = visible.length !== 0; window.sessionStorage.setItem('rxlocate-sort', sort.value); announce(visible.length === 0 ? 'No listings match this filter.' : `${visible.length} pharmacy listing${visible.length === 1 ? '' : 's'} shown.`);
  };
  filterGroup.addEventListener('click', event => { const button = event.target.closest('button[data-filter]'); if (!button) return; mode = button.dataset.filter; filterGroup.querySelectorAll('button').forEach(b => b.setAttribute('aria-pressed', String(b === button))); render(); });
  sort.addEventListener('change', render); render();
  form?.addEventListener('submit', () => { const value = input?.value.trim(); if (value) window.localStorage.setItem('rxlocate-last-search', value); });
})();
