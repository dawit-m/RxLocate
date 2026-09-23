/* Pharmacy workspace behaviour. Everything degrades gracefully: the server
   still handles every action, this file only makes the page feel faster. */
(() => {
  const $ = (selector, root = document) => root.querySelector(selector);
  const $$ = (selector, root = document) => [...root.querySelectorAll(selector)];

  const today = $('#today');
  if (today) today.textContent = new Date().toLocaleDateString('en-GB', { weekday: 'long', day: 'numeric', month: 'long' });

  const menu = $('#pharmacySidebar');
  const menuToggle = $('[data-mobile-menu-toggle]');
  const menuClosers = $$('[data-mobile-menu-close]');
  const setMenu = open => {
    document.body.classList.toggle('mobile-menu-open', open);
    menuToggle?.setAttribute('aria-expanded', String(open));
    if (open) $('.sidebar__nav a', menu)?.focus();
  };
  menuToggle?.addEventListener('click', () => setMenu(true));
  menuClosers.forEach(control => control.addEventListener('click', () => setMenu(false)));
  $$('.sidebar__nav a, .sidebar__foot a').forEach(link => link.addEventListener('click', () => setMenu(false)));
  document.addEventListener('keydown', event => { if (event.key === 'Escape') setMenu(false); });

  $$('[data-open-dialog]').forEach(button => button.addEventListener('click', () => {
    const dialog = document.getElementById(button.dataset.openDialog);
    if (dialog && typeof dialog.showModal === 'function') { dialog.showModal(); $('input', dialog)?.focus(); }
  }));
  $$('dialog').forEach(dialog => {
    dialog.addEventListener('click', event => { if (event.target === dialog) dialog.close(); });
    $$('[data-close-dialog]', dialog).forEach(button => button.addEventListener('click', () => dialog.close()));
  });

  const cost = $('#costPrice');
  const price = $('#price');
  if (cost && price) cost.addEventListener('input', () => { price.min = cost.value || 0; });

  const table = $('#inventoryTable');
  if (table) {
    const rows = $$('tbody tr', table), search = $('#inventorySearch'), group = $('#inventoryFilter'), empty = $('#inventoryEmpty');
    let mode = new URLSearchParams(window.location.search).get('filter') || 'all';
    const matches = row => {
      const name = $('.cell-name', row).textContent.toLowerCase();
      return (!search.value.trim() || name.includes(search.value.trim().toLowerCase())) && (mode === 'all' || (mode === 'expired' ? row.dataset.expired === 'true' : row.dataset.stock === mode));
    };
    const render = () => { let shown = 0; rows.forEach(row => { const ok = matches(row); row.hidden = !ok; if (ok) shown++; }); if (empty) empty.hidden = rows.length === 0 || shown > 0; };
    const setMode = next => { mode = next; $$('button', group).forEach(b => b.setAttribute('aria-pressed', String(b.dataset.filter === next))); render(); };
    if (!['all', 'low', 'out', 'expired'].includes(mode)) mode = 'all';
    setMode(mode); search.addEventListener('input', render); group.addEventListener('click', event => { const button = event.target.closest('button[data-filter]'); if (button) setMode(button.dataset.filter); });
  }

  const medicine = $('#medicineId'), quantity = $('#saleQuantity'), hint = $('#saleHint');
  if (medicine && quantity && hint) medicine.addEventListener('change', () => { const stock = medicine.selectedOptions[0]?.dataset.stock; if (stock) { quantity.max = stock; hint.textContent = stock + (Number(stock) === 1 ? ' unit' : ' units') + ' available.'; } else { quantity.removeAttribute('max'); hint.textContent = 'Pick a medicine to see how many are available.'; } });

  $$('form[data-confirm]').forEach(form => form.addEventListener('submit', event => { if (!window.confirm(form.dataset.confirm)) event.preventDefault(); }));
  const KEY = 'rxlocate:scroll';
  $$('.stepper form').forEach(form => form.addEventListener('submit', () => sessionStorage.setItem(KEY, String(window.scrollY))));
  const saved = sessionStorage.getItem(KEY);
  if (saved !== null) { sessionStorage.removeItem(KEY); window.scrollTo(0, Number(saved)); }
  $$('[data-autodismiss]').forEach(alert => setTimeout(() => { alert.style.transition = 'opacity .4s ease'; alert.style.opacity = '0'; setTimeout(() => alert.remove(), 400); }, 6000));
})();
