/* Sign-in and registration: show/hide password. */
document.querySelectorAll('[data-toggle-password]').forEach(button => {
  const input = document.getElementById(button.dataset.togglePassword);
  if (!input) return;
  button.addEventListener('click', () => {
    const show = input.type === 'password';
    input.type = show ? 'text' : 'password';
    button.setAttribute('aria-pressed', String(show));
    button.setAttribute('aria-label', show ? 'Hide password' : 'Show password');
    button.querySelector('use').setAttribute('href', show ? '#i-eye-off' : '#i-eye');
  });
});
