import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { RestauranteResponse } from '../../interfaces/RestauranteResponse.model';
import { fetchRestaurants } from '../../interceptors/axios/useApi';
import Card from '../Card/Card';
import { useApi } from '../../hooks/useFetch';
import './HomeRestaurante.css';
import Filtros from './Filtros/Filtros';

const HomeRestaurantes = () => {
  const { data, loading, error } = useApi<RestauranteResponse[], void>(
    fetchRestaurants,
    { autoFetch: true, params: undefined }
  );

  // filtro puede ser null (sin filtro) o array de restaurantes filtrados
  const [filtro, setFiltro] = useState<RestauranteResponse[] | null>(null);

  const navigate = useNavigate();

  if (loading) return <p>Cargando restaurantes...</p>;
  if (error) return <p>Error al cargar: {error.message}</p>;

  const handleCardClick = (restaurante: RestauranteResponse) => {
    navigate(`/ficha/${restaurante.id}`, { state: { restaurante } });
  };

  return (
    <div className="containerCards">
      {/* Pasamos data o [] para evitar undefined */}
      <Filtros restaurante={data ?? []} onFilterChange={setFiltro} />
      <div className="parent">
        {(filtro ?? data ?? []).map((restaurante) => (
          <div
            key={restaurante.id}
            onClick={() => handleCardClick(restaurante)}
            style={{ cursor: 'pointer' }}
          >
            <Card
              nombre={restaurante.razonSocial}
              imagenUrl={restaurante.logo.url}
              direccion={`${restaurante.direccion.calle} ${restaurante.direccion.numero}`}
            />
          </div>
        ))}
      </div>
    </div>
  );
};

export default HomeRestaurantes;