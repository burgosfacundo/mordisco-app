import { useNavigate } from 'react-router-dom';
import { RestauranteResponse } from '../../interfaces/RestauranteResponse.model.ts'
import { fetchRestaurants } from '../../interceptors/axios/useApi.ts'
import Card from '../Card/Card.tsx'
import { useApi } from '../../hooks/useFetch.ts'
import './HomeRestaurante.css'

const HomeRestaurantes = () => {
    const { data, loading, error, fetch } = useApi<RestauranteResponse[], void>(
        fetchRestaurants,
        { autoFetch: true, params: undefined }
    );

    const navigate = useNavigate();

    if (loading) return <p>Cargando restaurantes...</p>;
    if (error) return <p>Error al cargar: {error.message}</p>;

    const handleCardClick = (restaurante: RestauranteResponse) => {
        navigate(`/ficha/${restaurante.id}`, { state: { restaurante } });
    };

    return (
        <div className='containerCards'>
        <div className="parent">
            {data?.map((restaurante) => (
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
    }

    export default HomeRestaurantes;

