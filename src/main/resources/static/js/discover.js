document.addEventListener('DOMContentLoaded', function () {
  const content = document.getElementById('discoverContent');
  if (!content) return; // not on discover page

  let currentPerfumeId = null;
  let selectedRating = 0;

  // wire tab buttons on the page
  document.querySelectorAll('[data-mode]').forEach(btn => {
    btn.addEventListener('click', (e) => {
      e.preventDefault();
      const mode = btn.getAttribute('data-mode');
      // visually mark active
      document.querySelectorAll('[data-mode]').forEach(b=>b.classList.remove('active'));
      btn.classList.add('active');
      loadDiscover(mode);
    });
  });

  // initial load
  loadDiscover('recommended');

  function loadDiscover(mode) {
    const content = document.getElementById('discoverContent');
    content.innerHTML = '<div class="text-center py-5">Loading…</div>';
    fetch(`/api/discover?mode=${mode}&limit=8`)
      .then(r => r.json())
      .then(renderList)
      .catch(err => {
        content.innerHTML = `<div class="alert alert-danger">Error loading suggestions</div>`;
        console.error(err);
      });
  }

  function renderList(items) {
    if (!items || items.length === 0) {
      document.getElementById('discoverContent').innerHTML = '<div class="p-4">No suggestions found. Try <a href="/perfumes/add">adding a perfume</a> or change mode.</div>';
      return;
    }
    const grid = document.createElement('div');
    grid.className = 'row g-3';

    items.forEach(p => {
      const col = document.createElement('div');
      col.className = 'col-md-6';
      col.innerHTML = `
        <div class="card h-100">
          <div class="row g-0">
            <div class="col-4 bg-light d-flex align-items-center justify-content-center" style="min-height:120px">
              <div class="text-muted">No image</div>
            </div>
            <div class="col-8">
              <div class="card-body">
                <h5 class="card-title">${escapeHtml(p.name)}</h5>
                <h6 class="card-subtitle mb-2 text-muted">${escapeHtml(p.brand||'')}</h6>
                <p class="card-text small">${escapeHtml(p.shortDescription||'')}</p>
                <p class="card-text"><small class="text-muted">${(p.tags||[]).join(', ')}</small></p>
                <div>
                  <button class="btn btn-sm btn-primary add-btn" data-id="${p.id}" ${p.inCollection? 'disabled': ''}>${p.inCollection? 'In collection':'Add'}</button>
                  <button class="btn btn-sm btn-outline-secondary ms-2 details-btn" data-id="${p.id}" data-name="${escapeHtml(p.name)}" data-brand="${escapeHtml(p.brand||'')}">Details</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      `;
      grid.appendChild(col);
    });

    content.innerHTML = '';
    content.appendChild(grid);

    // wire add buttons
    document.getElementById('discoverContent').querySelectorAll('.add-btn').forEach(btn => {
      btn.addEventListener('click', function () {
        const id = this.getAttribute('data-id');
        fetch('/api/collection', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ perfumeId: Number(id) })
        }).then(r => {
          if (r.ok) {
            this.textContent = 'In collection';
            this.disabled = true;
            showToast('Added to your collection');
          } else if (r.status === 401) {
            showToast('Please log in to add to your collection', 'warning');
          } else if (r.status === 409) {
            r.json().then(data => {
              showToast(data.error || 'Perfume already in your collection', 'warning');
            });
          } else {
            showToast('Could not add', 'danger');
          }
        }).catch(err => {
          console.error(err);
          showToast('Network error', 'danger');
        });
      });
    });

    // wire details buttons
    document.getElementById('discoverContent').querySelectorAll('.details-btn').forEach(btn => {
      btn.addEventListener('click', function () {
        const id = this.getAttribute('data-id');
        const name = this.getAttribute('data-name');
        const brand = this.getAttribute('data-brand');
        showDetailsModal(id, name, brand);
      });
    });
  }

  function showDetailsModal(perfumeId, name, brand) {
    currentPerfumeId = perfumeId;
    selectedRating = 0;
    
    // Update modal header
    document.getElementById('detailsTitle').textContent = name;
    document.getElementById('detailsName').textContent = name;
    document.getElementById('detailsBrand').textContent = brand;
    
    // Reset form
    document.getElementById('selectedRating').value = '0';
    document.getElementById('commentInput').value = '';
    document.getElementById('charCount').textContent = '0';
    resetStarButtons();
    
    // Load reviews
    loadReviews(perfumeId);
    
    // Show modal
    const modal = new bootstrap.Modal(document.getElementById('detailsModal'));
    modal.show();
  }

  function loadReviews(perfumeId) {
    fetch(`/api/perfumes/${perfumeId}/reviews`)
      .then(r => r.json())
      .then(data => {
        const reviews = data.reviews || [];
        const avgRating = data.averageRating || 0;
        const totalReviews = data.totalReviews || 0;
        
        // Update rating display
        document.getElementById('avgRating').textContent = avgRating.toFixed(1);
        document.getElementById('reviewCount').textContent = totalReviews;
        
        if (totalReviews === 0) {
          document.getElementById('ratingText').textContent = 'No reviews yet';
        } else {
          const stars = '★'.repeat(Math.round(avgRating)) + '☆'.repeat(5 - Math.round(avgRating));
          document.getElementById('ratingText').textContent = `${stars} (${totalReviews} reviews)`;
        }
        
        // Render reviews
        renderReviews(reviews);
      })
      .catch(err => {
        console.error('Error loading reviews:', err);
        document.getElementById('reviewsList').innerHTML = '<div class="alert alert-danger">Error loading reviews</div>';
      });
  }

  function renderReviews(reviews) {
    const reviewsList = document.getElementById('reviewsList');
    
    if (reviews.length === 0) {
      reviewsList.innerHTML = '<div class="text-muted">No reviews yet. Be the first to review!</div>';
      return;
    }
    
    reviewsList.innerHTML = reviews.map(r => `
      <div class="card mb-2 p-3">
        <div class="d-flex justify-content-between align-items-start">
          <div>
            <h6 class="mb-1">${escapeHtml(r.username)}</h6>
            <div class="mb-1">
              <small class="text-warning">${'★'.repeat(r.rating)}${'☆'.repeat(5 - r.rating)}</small>
            </div>
            <p class="mb-0 text-muted">${escapeHtml(r.comment || '(No comment)')}</p>
          </div>
          <small class="text-muted">${formatDate(r.createdAt)}</small>
        </div>
      </div>
    `).join('');
  }

  function formatDate(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const diff = now - date;
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    
    if (days === 0) return 'Today';
    if (days === 1) return 'Yesterday';
    if (days < 7) return `${days} days ago`;
    if (days < 30) return `${Math.floor(days / 7)} weeks ago`;
    return date.toLocaleDateString();
  }

  // Star rating interaction
  document.querySelectorAll('.star-btn').forEach(btn => {
    btn.addEventListener('click', function() {
      selectedRating = parseInt(this.getAttribute('data-rating'));
      document.getElementById('selectedRating').value = selectedRating;
      resetStarButtons();
      for (let i = 1; i <= selectedRating; i++) {
        document.querySelector(`.star-btn[data-rating="${i}"]`).classList.add('active');
        document.querySelector(`.star-btn[data-rating="${i}"]`).classList.remove('btn-outline-warning');
        document.querySelector(`.star-btn[data-rating="${i}"]`).classList.add('btn-warning', 'text-dark');
      }
    });
  });

  function resetStarButtons() {
    document.querySelectorAll('.star-btn').forEach(btn => {
      btn.classList.remove('active', 'btn-warning', 'text-dark');
      btn.classList.add('btn-outline-warning');
    });
  }

  // Comment input character counter
  document.getElementById('commentInput').addEventListener('input', function() {
    document.getElementById('charCount').textContent = this.value.length;
  });

  // Submit review
  document.getElementById('submitReviewBtn').addEventListener('click', function() {
    const rating = parseInt(document.getElementById('selectedRating').value);
    const comment = document.getElementById('commentInput').value;
    
    if (rating === 0) {
      showToast('Please select a rating', 'warning');
      return;
    }
    
    if (!comment.trim()) {
      showToast('Please add a comment', 'warning');
      return;
    }
    
    fetch(`/api/perfumes/${currentPerfumeId}/reviews`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ rating, comment })
    }).then(r => {
      if (r.ok) {
        showToast('Review posted successfully!', 'success');
        document.getElementById('commentInput').value = '';
        document.getElementById('selectedRating').value = '0';
        resetStarButtons();
        document.getElementById('charCount').textContent = '0';
        loadReviews(currentPerfumeId);
      } else if (r.status === 401) {
        showToast('Please log in to post a review', 'warning');
      } else {
        showToast('Could not post review', 'danger');
      }
    }).catch(err => {
      console.error(err);
      showToast('Network error', 'danger');
    });
  });

  function showToast(message, variant='success') {
    const el = document.createElement('div');
    el.className = `toast align-items-center text-bg-${variant} border-0 position-fixed p-2`;
    el.style.zIndex = 9999;
    el.style.right = '20px';
    el.style.bottom = '20px';
    el.innerHTML = `<div class="d-flex"><div class="toast-body">${escapeHtml(message)}</div><button type="button" class="btn-close btn-close-white ms-2 me-1"></button></div>`;
    document.body.appendChild(el);
    const close = el.querySelector('.btn-close');
    close.addEventListener('click', ()=> el.remove());
    setTimeout(()=> el.remove(), 4000);
  }

  function escapeHtml(str) {
    if (!str) return '';
    return str.replaceAll('&','&amp;').replaceAll('<','&lt;').replaceAll('>','&gt;');
  }
});
