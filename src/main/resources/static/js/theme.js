// DATUM – delightful UI helpers
(function () {
  'use strict';

  // -------- Ripple effect --------
  document.addEventListener('click', function(e){
    const t = e.target.closest('.btn, .btn-brand, .btn-outline-brand');
    if(!t) return;
    const rect = t.getBoundingClientRect();
    const circle = document.createElement('span');
    const d = Math.max(rect.width, rect.height);
    circle.style.width = circle.style.height = d + 'px';
    circle.style.left = (e.clientX - rect.left - d/2) + 'px';
    circle.style.top  = (e.clientY - rect.top  - d/2) + 'px';
    circle.className = 'ripple';
    const prev = t.getElementsByClassName('ripple')[0];
    if (prev) prev.remove();
    t.appendChild(circle);
  });

  // -------- Password visibility toggle --------
  document.querySelectorAll('[data-toggle="password"]').forEach(btn => {
    btn.addEventListener('click', () => {
      const sel = btn.getAttribute('data-target');
      const input = document.querySelector(sel);
      if (!input) return;
      input.type = (input.type === 'password') ? 'text' : 'password';
      const icon = btn.querySelector('i');
      if (icon){ icon.classList.toggle('bi-eye'); icon.classList.toggle('bi-eye-slash'); }
    });
  });

  // -------- Caps lock hint --------
  const pwd = document.querySelector('#loginPassword');
  const capsHint = document.querySelector('#capsHint');
  if (pwd && capsHint){
    pwd.addEventListener('keyup', (e)=>{
      if (e.getModifierState && e.getModifierState('CapsLock')){
        capsHint.classList.add('caps-on');
      } else {
        capsHint.classList.remove('caps-on');
      }
    });
  }

  // -------- Strength meter --------
  const barsWrap = document.querySelector('.strength-bars');
  const label = document.querySelector('.strength-label');
  function scorePassword(pwd){
    let s = 0;
    if (!pwd) return s;
    if (pwd.length >= 8) s++;
    if (/[a-z]/.test(pwd) && /[A-Z]/.test(pwd)) s++;
    if (/\d/.test(pwd)) s++;
    if (/[^A-Za-z0-9]/.test(pwd)) s++;
    return Math.min(s, 4);
  }
  function setStrength(n){
    const cont = document.querySelector('.strength-wrap');
    if (!cont) return;
    cont.className = 'strength-wrap strength-' + n;
    if (label){
      label.textContent = ['','Weak','Okay','Good','Strong'][n];
    }
  }
  if (pwd && barsWrap){
    pwd.addEventListener('input', ()=> setStrength(scorePassword(pwd.value)));
    setStrength(scorePassword(pwd.value || ''));
  }

  // -------- Submit progress / disable --------
  const form = document.querySelector('form.needs-validation');
  if (form){
    form.addEventListener('submit', function(e){
      if (!form.checkValidity()){
        form.classList.add('was-validated');
        return; // stop; browser will show invalid feedback
      }
      const btn = form.querySelector('button[type="submit"]');
      if (btn){
        btn.classList.add('btn-progress');
        btn.setAttribute('aria-busy','true');
        const txt = btn.innerHTML;
        btn.dataset.original = txt;
        btn.innerHTML = '<span class="spinner"></span> Signing in…';
      }
    });
  }

  // -------- Shake on ?error --------
  if (new URLSearchParams(location.search).has('error')){
    const card = document.querySelector('.auth-card');
    if (card){ card.classList.add('shake'); }
  }

  // -------- Confetti on ?registered --------
  if (new URLSearchParams(location.search).has('registered')){
    confettiBurst();
  }
  function confettiBurst(){
    const count = 60;
    const colors = ['#1a73e8','#22d3ee','#22c55e','#f59e0b'];
    for (let i=0;i<count;i++){
      const c = document.createElement('i');
      c.style.position='fixed';
      c.style.top='-10px';
      c.style.left = (Math.random()*100)+'vw';
      c.style.width=c.style.height='8px';
      c.style.background=colors[Math.floor(Math.random()*colors.length)];
      c.style.transform='rotate('+(Math.random()*360)+'deg)';
      c.style.opacity='0.9';
      c.style.zIndex='9999';
      c.style.borderRadius='2px';
      c.style.pointerEvents='none';
      document.body.appendChild(c);
      const duration = 1200 + Math.random()*1200;
      const translateX = (Math.random()*2-1)*60;
      c.animate([
        { transform: c.style.transform+' translateY(0) translateX(0)', opacity: 1 },
        { transform: c.style.transform+` translateY(100vh) translateX(${translateX}px)`, opacity: .8 }
      ], { duration, easing:'cubic-bezier(.2,.8,.2,1)' }).onfinish = ()=> c.remove();
    }
  }

  // -------- Parallax tilt for hero panel --------
  const hero = document.querySelector('.auth-hero');
  if (hero){
    hero.addEventListener('mousemove', (e)=>{
      const r = hero.getBoundingClientRect();
      const x = (e.clientX - r.left)/r.width - .5;
      const y = (e.clientY - r.top)/r.height - .5;
      hero.style.transform = `perspective(1000px) rotateY(${x*6}deg) rotateX(${ -y*6 }deg)`;
    });
    hero.addEventListener('mouseleave', ()=>{
      hero.style.transform = 'none';
    });
  }
})();
