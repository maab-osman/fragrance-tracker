import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { perfumeAPI } from '../services/api';

interface EditPerfumeProps {
  user: any;
}

const EditPerfume: React.FC<EditPerfumeProps> = ({ user }) => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const [formData, setFormData] = useState({
    name: '',
    brand: '',
    fragranceNotes: [] as string[],
    season: '',
    occasion: '',
    description: '',
    collectionStatus: 'owned',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  const fragranceOptions = [
    'Floral',
    'Fruity',
    'Oriental',
    'Fresh',
    'Woody',
    'Spicy',
    'Herbal',
    'Citrus',
  ];

  useEffect(() => {
    const loadPerfume = async () => {
      try {
        const response = await perfumeAPI.getById(Number(id));
        setFormData(response.data);
      } catch (error) {
        setError('Failed to load perfume');
      } finally {
        setLoading(false);
      }
    };

    if (id) {
      loadPerfume();
    }
  }, [id]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleCheckboxChange = (note: string) => {
    setFormData((prev) => ({
      ...prev,
      fragranceNotes: prev.fragranceNotes.includes(note)
        ? prev.fragranceNotes.filter((n) => n !== note)
        : [...prev.fragranceNotes, note],
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);

    try {
      await perfumeAPI.update(Number(id), formData);
      navigate('/');
    } catch (error: any) {
      setError(error.response?.data?.message || 'Failed to update perfume');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <div className="text-center mt-5">Loading...</div>;

  return (
    <div className="row justify-content-center">
      <div className="col-md-8">
        <div className="card shadow">
          <div className="card-body">
            <h2 className="card-title mb-4">Edit Perfume</h2>
            {error && <div className="alert alert-danger">{error}</div>}

            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label className="form-label">Name *</label>
                <input
                  type="text"
                  className="form-control"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="mb-3">
                <label className="form-label">Brand *</label>
                <input
                  type="text"
                  className="form-control"
                  name="brand"
                  value={formData.brand}
                  onChange={handleChange}
                  required
                />
              </div>

              <div className="mb-3">
                <label className="form-label">Season *</label>
                <select
                  className="form-select"
                  name="season"
                  value={formData.season}
                  onChange={handleChange}
                  required
                >
                  <option value="">Select a season</option>
                  <option value="Spring">Spring</option>
                  <option value="Summer">Summer</option>
                  <option value="Fall">Fall</option>
                  <option value="Winter">Winter</option>
                </select>
              </div>

              <div className="mb-3">
                <label className="form-label">Occasion *</label>
                <select
                  className="form-select"
                  name="occasion"
                  value={formData.occasion}
                  onChange={handleChange}
                  required
                >
                  <option value="">Select an occasion</option>
                  <option value="Casual">Casual</option>
                  <option value="Formal">Formal</option>
                  <option value="Sport">Sport</option>
                  <option value="Evening">Evening</option>
                </select>
              </div>

              <div className="mb-3">
                <label className="form-label">Fragrance Notes</label>
                <div>
                  {fragranceOptions.map((note) => (
                    <div key={note} className="form-check">
                      <input
                        type="checkbox"
                        className="form-check-input"
                        id={note}
                        checked={formData.fragranceNotes.includes(note)}
                        onChange={() => handleCheckboxChange(note)}
                      />
                      <label className="form-check-label" htmlFor={note}>
                        {note}
                      </label>
                    </div>
                  ))}
                </div>
              </div>

              <div className="mb-3">
                <label className="form-label">Description</label>
                <textarea
                  className="form-control"
                  name="description"
                  value={formData.description}
                  onChange={handleChange}
                  rows={4}
                ></textarea>
              </div>

              <div className="mb-3">
                <label className="form-label">Collection Status</label>
                <select
                  className="form-select"
                  name="collectionStatus"
                  value={formData.collectionStatus}
                  onChange={handleChange}
                >
                  <option value="owned">Owned</option>
                  <option value="wishlist">Wishlist</option>
                  <option value="tried">Tried</option>
                </select>
              </div>

              <div className="d-flex gap-2">
                <button type="submit" className="btn btn-primary" disabled={submitting}>
                  {submitting ? 'Updating...' : 'Update Perfume'}
                </button>
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => navigate('/')}
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EditPerfume;
