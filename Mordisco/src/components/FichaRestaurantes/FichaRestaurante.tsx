import { useLocation, useParams, useNavigate } from 'react-router-dom';
import "./FichaRestaurante.css"
import { RestauranteResponse } from '../../interfaces/RestauranteResponse.model.ts'
import { MenuResponse } from '../../interfaces/MenuResponse.model.ts';
import { fetchMenu } from '../../interceptors/axios/useApi.ts';
import { useApi } from '../../hooks/useFetch.ts';
import Horario from './Horario/Horario.tsx';
import Menu from '../Menu/Menu.tsx';

const FichaRestaurante = () => {
    const { id } = useParams<{ id: string }>();
    const location = useLocation();
    const restaurante = location.state?.restaurante as RestauranteResponse | undefined;

    const restauranteId = Number(id);

    const { data, loading, error, fetch } = useApi<MenuResponse, number>(
        fetchMenu,
        {
        autoFetch: true,
        params: restauranteId,
        }
    );

    function formatearFecha(fecha: string): string {
        const [anio, mes, dia] = fecha.split("-");
        return `${dia}-${mes}-${anio}`;
    }

    const navigate = useNavigate();

    const handlePedidoClick = () => {
        if (restaurante && data) {
            navigate(`/pedido/${restaurante.id}`, { state: { restaurante, menu: data } });
        }
    };

    if (loading) return <p>Cargando restaurante...</p>;
    if (error) return <p>Error al cargar: {error.message}</p>;

    if (!restaurante) return <p>Cargando restaurante...</p>;

    return (
        <div className='containerTotal'>
            <div className="containerRestaurant">
                <div className='containerWithFoto'>
                    <img src={restaurante.logo.url} className='imgRestaurant'/>
                    <div className="containerInfoRestaurant">
                        <h1>{restaurante.razonSocial}</h1>
                        <p>{restaurante.direccion.calle} {restaurante.direccion.numero}</p>
                    </div>
                </div>
                    <Horario horarios={restaurante.hoariosDeAtencion}/>

                <button className='button' onClick={() => handlePedidoClick(restaurante)}>Realizar Pedido</button>
            </div>

            <div className="containerPromocion">
                {restaurante.promociones.map((promocion) => (
                    <div key={promocion.id} className='promociones'>
                        <p>{promocion.descripcion}</p>
                        <p style={{fontSize: '12px'}}> (Valido desde: {formatearFecha(promocion.fechaInicio)} hasta: {formatearFecha(promocion.fechaFin)})</p>
                    </div>
                ))}
            </div>

            {data ? (<Menu menu={data} />) : (<p>Cargando menú...</p>)}

        </div>
    );
};

export default FichaRestaurante;
