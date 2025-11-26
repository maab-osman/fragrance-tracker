/**
 * Admin Discovery Page Script
 * Allows admins to view catalog perfumes and delete them or their reviews
 */
document.addEventListener('DOMContentLoaded', function () {
  const content = document.getElementById('discoverContent');
  if (!content) return; // not on admin discover page

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
      .then(r => {
        if (!r.ok) {
          throw new Error(`HTTP ${r.status}: ${r.statusText}`);
        }
        return r.json();
      })
      .then(renderList)
      .catch(err => {
        console.error('Error loading discover:', err);
        content.innerHTML = `<div class="alert alert-danger"><strong>Error loading perfumes:</strong> ${err.message}</div>`;
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
          <button class="btn btn-sm btn-danger admin-delete-btn" data-id="${p.id}" title="Delete this perfume from catalog">
            <i class="bi bi-trash"></i> Delete
          </button>
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

    // Wire admin delete buttons
    document.getElementById('discoverContent').querySelectorAll('.admin-delete-btn').forEach(btn => {
      btn.addEventListener('click', function (e) {
        e.stopPropagation();
        const id = this.getAttribute('data-id');
        if (confirm('Are you sure you want to delete this perfume from the catalog? This cannot be undone.')) {
          deletePerfume(id);
        }
      });
    });

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

  function deletePerfume(perfumeId) {
    fetch(`/api/perfumes/${perfumeId}`, {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json' }
    }).then(r => {
      if (r.ok) {
        showToast('Perfume deleted from catalog', 'success');
        // Reload the current view
        const activeMode = document.querySelector('[data-mode].active');
        const mode = activeMode ? activeMode.getAttribute('data-mode') : 'recommended';
        loadDiscover(mode);
      } else {
        showToast('Failed to delete perfume', 'danger');
      }
    }).catch(err => {
      console.error(err);
      showToast('Error deleting perfume', 'danger');
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
        
        // Render reviews WITH ADMIN DELETE BUTTONS
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
          <div class="text-end">
            <small class="text-muted d-block mb-2">${formatDate(r.createdAt)}</small>
            <button class="btn btn-sm btn-danger delete-review-btn" data-review-id="${r.id}" title="Delete this review">
              Delete
            </button>
          </div>
        </div>
      </div>
    `).join('');
    
    // Wire delete review buttons
    document.querySelectorAll('.delete-review-btn').forEach(btn => {
      btn.addEventListener('click', function () {
        const reviewId = this.getAttribute('data-review-id');
        if (confirm('Delete this review?')) {
          deleteReview(reviewId);
        }
      });
    });
  }

  function deleteReview(reviewId) {
    fetch(`/api/reviews/${reviewId}`, {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json' }
    }).then(r => {
      if (r.ok) {
        showToast('Review deleted', 'success');
        // Reload reviews for current perfume
        if (currentPerfumeId) {
          loadReviews(currentPerfumeId);
        }
      } else {
        showToast('Failed to delete review', 'danger');
      }
    }).catch(err => {
      console.error(err);
      showToast('Error deleting review', 'danger');
    });
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

  // Utility functions (shared)
  function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  function showToast(msg, type = 'success') {
    // Simple toast implementation
    const toast = document.createElement('div');
    toast.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
    toast.style.top = '20px';
    toast.style.right = '20px';
    toast.style.zIndex = '9999';
    toast.innerHTML = `
      ${msg}
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 4000);
  }

  function resetStarButtons() {
    document.querySelectorAll('.star-btn').forEach(btn => btn.classList.remove('active'));
  }

  // Wire star buttons
  document.querySelectorAll('.star-btn').forEach(btn => {
    btn.addEventListener('click', function () {
      selectedRating = parseInt(this.getAttribute('data-rating'));
      document.getElementById('selectedRating').value = selectedRating;
      resetStarButtons();
      for (let i = 1; i <= selectedRating; i++) {
        document.querySelector(`[data-rating="${i}"]`).classList.add('active');
      }
    });
  });

  // Wire comment input counter
  document.getElementById('commentInput').addEventListener('input', function () {
    document.getElementById('charCount').textContent = this.value.length;
  });

  // Wire submit review button
  document.getElementById('submitReviewBtn').addEventListener('click', function () {
    const rating = parseInt(document.getElementById('selectedRating').value);
    const comment = document.getElementById('commentInput').value.trim();
    
    if (rating === 0) {
      showToast('Please select a rating', 'warning');
      return;
    }
    
    fetch(`/api/perfumes/${currentPerfumeId}/reviews`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ rating, comment })
    }).then(r => {
      if (r.ok) {
        showToast('Review submitted!', 'success');
        // Reset form and reload reviews
        document.getElementById('selectedRating').value = '0';
        document.getElementById('commentInput').value = '';
        document.getElementById('charCount').textContent = '0';
        resetStarButtons();
        loadReviews(currentPerfumeId);
      } else {
        showToast('Error submitting review', 'danger');
      }
    }).catch(err => {
      console.error(err);
      showToast('Network error', 'danger');
    });
  });
});
