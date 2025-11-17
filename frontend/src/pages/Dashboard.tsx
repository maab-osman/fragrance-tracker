import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { perfumeAPI } from '../services/api';

interface Perfume {
  id: number;
  name: string;
  brand: string;
  season: string;
  occasion: string;
  description: string;
  collectionStatus: string;
}

interface DashboardProps {
  user: any;
}

const Dashboard: React.FC<DashboardProps> = ({ user }) => {
  const [perfumes, setPerfumes] = useState<Perfume[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    loadPerfumes();
  }, []);

  const loadPerfumes = async () => {
    try {
      setLoading(true);
      const response = await perfumeAPI.getAll();
      setPerfumes(response.data);
    } catch (error: any) {
      setError('Failed to load perfumes');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!searchTerm) {
      loadPerfumes();
      return;
    }

    try {
      setLoading(true);
      const response = await perfumeAPI.search(searchTerm);
      setPerfumes(response.data);
    } catch (error) {
      setError('Search failed');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure?')) {
      try {
        await perfumeAPI.delete(id);
        loadPerfumes();
      } catch (error) {
        setError('Delete failed');
      }
    }
  };

  if (loading) return <div className="text-center mt-5">Loading...</div>;

  return (
    <div>
      <h1 className="mb-4">🌸 My Perfume Collection</h1>

      {error && <div className="alert alert-danger">{error}</div>}

      <form onSubmit={handleSearch} className="mb-4">
        <div className="input-group">
          <input
            type="text"
            className="form-control"
            placeholder="Search perfumes by name..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
          <button className="btn btn-outline-secondary" type="submit">
            Search
          </button>
          <button
            className="btn btn-outline-secondary"
            type="button"
            onClick={() => {
              setSearchTerm('');
              loadPerfumes();
            }}
          >
            Clear
          </button>
        </div>
      </form>

      {perfumes.length === 0 ? (
        <div className="alert alert-info">No perfumes yet. <Link to="/add">Add one!</Link></div>
      ) : (
        <div className="row">
          {perfumes.map((perfume) => (
            <div key={perfume.id} className="col-md-4 mb-4">
              <div className="card h-100 shadow-sm">
                <div className="card-body">
                  <h5 className="card-title">{perfume.name}</h5>
                  <p className="card-text text-muted">{perfume.brand}</p>
                  <ul className="list-unstyled small">
                    <li><strong>Season:</strong> {perfume.season}</li>
                    <li><strong>Occasion:</strong> {perfume.occasion}</li>
                    <li><strong>Status:</strong> {perfume.collectionStatus}</li>
                  </ul>
                  <p className="card-text">{perfume.description}</p>
                </div>
                <div className="card-footer bg-light">
                  <Link to={`/edit/${perfume.id}`} className="btn btn-sm btn-primary">
                    Edit
                  </Link>
                  <button
                    className="btn btn-sm btn-danger ms-2"
                    onClick={() => handleDelete(perfume.id)}
                  >
                    Delete
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Dashboard;
