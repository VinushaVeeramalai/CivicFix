(function () {
  'use strict';

  const CRITICAL_KEYWORDS = [
    'collapse', 'flood', 'fire', 'danger', 'accident', 'emergency', 'electric shock',
    'sewage overflow', 'burst pipe', 'gas leak', 'hazard', 'unsafe', 'injury', 'blocked road'
  ];
  const HIGH_KEYWORDS = [
    'pothole', 'broken', 'no water', 'no electricity', 'overflow', 'damaged',
    'large', 'deep', 'major', 'serious', 'urgent', 'leak'
  ];
  const LOW_KEYWORDS = [
    'minor', 'small', 'slight', 'cosmetic', 'paint', 'sign', 'bench', 'graffiti', 'noise'
  ];
  const HIGH_CATEGORIES = ['pothole', 'water leak', 'sewage'];
  const MEDIUM_CATEGORIES = ['streetlight', 'garbage'];

  function calculateSeverity(category, description) {
    const combined = ((category || '') + ' ' + (description || '')).toLowerCase();
    const catLower = (category || '').trim().toLowerCase();

    for (const kw of CRITICAL_KEYWORDS) {
      if (combined.includes(kw)) return 'Critical';
    }
    for (const kw of HIGH_KEYWORDS) {
      if (combined.includes(kw)) return 'High';
    }
    for (const kw of LOW_KEYWORDS) {
      if (combined.includes(kw)) return 'Low';
    }
    for (const c of HIGH_CATEGORIES) {
      if (catLower.includes(c)) return 'High';
    }
    for (const c of MEDIUM_CATEGORIES) {
      if (catLower.includes(c)) return 'Medium';
    }
    return 'Medium';
  }

  window.showToast = function (message, type) {
    type = type || 'info';
    const container = document.getElementById('toast-container') || (function () {
      const div = document.createElement('div');
      div.id = 'toast-container';
      div.className = 'toast-container';
      document.body.appendChild(div);
      return div;
    })();
    const toast = document.createElement('div');
    toast.className = 'toast ' + type;
    toast.textContent = message;
    container.appendChild(toast);
    setTimeout(function () {
      toast.remove();
    }, 4000);
  };

  window.upvote = function (issueId) {
    const btn = document.querySelector('.btn-upvote[data-id="' + issueId + '"]');
    if (!btn || btn.getAttribute('data-voted') === 'true') return;

    fetch('/issues/upvote/' + issueId, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }
    })
      .then(function (r) { return r.json(); })
      .then(function (data) {
        if (data.error) {
          showToast(data.error, 'error');
          return;
        }
        btn.setAttribute('data-voted', 'true');
        var countEl = btn.querySelector('.upvote-count') || btn.querySelector('span');
        if (countEl) countEl.textContent = data.upvotes;
        var severityBadge = btn.closest('.issue-card') && btn.closest('.issue-card').querySelector('.badge.severity');
        if (severityBadge) {
          severityBadge.textContent = data.severity;
          severityBadge.className = 'badge severity severity-' + (data.severity || '').toLowerCase().replace(' ', '-');
        }
        showToast('Upvoted! +2 points', 'success');
      })
      .catch(function () { showToast('Failed to upvote', 'error'); });
  };

  window.getLocation = function () {
    const locationInput = document.getElementById('location');
    const latInput = document.getElementById('latitude');
    const lngInput = document.getElementById('longitude');
    if (!navigator.geolocation) {
      showToast('Geolocation not supported', 'error');
      return;
    }
    navigator.geolocation.getCurrentPosition(
      function (pos) {
        const lat = pos.coords.latitude;
        const lng = pos.coords.longitude;
        if (latInput) latInput.value = lat;
        if (lngInput) lngInput.value = lng;
        fetch('https://nominatim.openstreetmap.org/reverse?format=json&lat=' + lat + '&lon=' + lng + '&zoom=18&addressdetails=1', {
          headers: { 'Accept': 'application/json' }
        })
          .then(function (r) { return r.json(); })
          .then(function (data) {
            if (data && data.display_name && locationInput) {
              locationInput.value = data.display_name;
            } else if (locationInput) {
              locationInput.value = lat.toFixed(6) + ', ' + lng.toFixed(6);
            }
            showToast('Location set', 'success');
          })
          .catch(function () {
            if (locationInput) locationInput.value = lat.toFixed(6) + ', ' + lng.toFixed(6);
            showToast('Address lookup failed; coordinates set', 'info');
          });
      },
      function () { showToast('Could not get location', 'error'); }
    );
  };

  function initImagePreview() {
    const input = document.getElementById('image');
    const preview = document.getElementById('imagePreview');
    if (!input || !preview) return;
    input.addEventListener('change', function () {
      preview.innerHTML = '';
      const file = input.files && input.files[0];
      if (!file || !file.type.match(/^image\//)) return;
      const reader = new FileReader();
      reader.onload = function (e) {
        const img = document.createElement('img');
        img.src = e.target.result;
        preview.appendChild(img);
      };
      reader.readAsDataURL(file);
    });
  }

  function initAutoHideAlerts() {
    document.querySelectorAll('.alert').forEach(function (el) {
      setTimeout(function () { el.style.display = 'none'; }, 4000);
    });
  }

  function initLiveSeverity() {
    const category = document.getElementById('category');
    const description = document.getElementById('description');
    const severityValue = document.getElementById('severityValue');
    if (!severityValue) return;

    function update() {
      const cat = category ? category.value : '';
      const desc = description ? description.value : '';
      const severity = calculateSeverity(cat, desc);
      severityValue.textContent = severity;
      severityValue.className = 'badge severity severity-' + severity.toLowerCase().replace(' ', '-');
    }

    if (category) category.addEventListener('input', update);
    if (category) category.addEventListener('change', update);
    if (description) description.addEventListener('input', update);
    update();
  }

  function initUpvoteButtons() {
    document.querySelectorAll('.btn-upvote').forEach(function (btn) {
      var id = btn.getAttribute('data-id');
      if (!id) return;
      btn.addEventListener('click', function () {
        if (btn.getAttribute('data-voted') === 'true') return;
        upvote(id);
      });
    });
  }

  document.addEventListener('DOMContentLoaded', function () {
    initImagePreview();
    initAutoHideAlerts();
    initLiveSeverity();
    initUpvoteButtons();

    var getLocationBtn = document.getElementById('getLocationBtn');
    if (getLocationBtn) getLocationBtn.addEventListener('click', getLocation);
  });
})();
