import './Pedido.css'
import { useLocation, useParams, useNavigate } from 'react-router-dom';
import { RestauranteResponse } from '../../interfaces/RestauranteResponse.model';
import { MenuResponse } from '../../interfaces/MenuResponse.model';
import { useState } from 'react';


const Pedido = () => {
    const [option, setOption] = useState(1)

    const location = useLocation();
    const restaurante = location.state?.restaurante as RestauranteResponse;
    const menu = location.state?.menu as MenuResponse;

    const [cantidades, setCantidades] = useState<{ [id: number]: number }>({});

    const seleccionados = Object.entries(cantidades)
        .filter(([_, cantidad]) => cantidad > 0)
        .map(([id, cantidad]) => ({
            productoId: Number(id),
            cantidad
    }));

    const incrementar = (id: number) => {
        setCantidades(prev => ({ ...prev, [id]: (prev[id] || 0) + 1 }));
    };

    const decrementar = (id: number) => {
        setCantidades(prev => ({ ...prev, [id]: Math.max((prev[id] || 0) - 1, 0) }));
    };

    console.log(menu)
    return (
        <div className='containerTotal-resgistro'>
            <div className='containerInfo'>
                <h4>Pedido en <strong>{restaurante.razonSocial}</strong></h4>
            
                <div className="radio-input">
                    <div className='containerRadioButton'>
                        <label>Delivery</label>
                        <input className="input yellow" type="radio" name="entrega" value={1} 
                            onChange={() => { setOption(1) }}
                        />
                    </div>
                    <div className='containerRadioButton'>
                        <label>Take Away </label>
                        <input className="input red" type="radio" name="entrega" value={2} 
                            onChange={() => { setOption(2) }}
                        />
                    </div>
                </div>
                {option == 1 ? 
                    <div>
                        {/*Ingresar aca direcciones de usuario en SELECT*/}
                    </div>
                    : <p>Retiro en local</p>}
            </div>
            

                <div className='productos-grid'>
                    {menu.productos.map(producto => (
                        <div key={producto.id} className="producto-item">
                            <span className="producto-nombre">{producto.nombre}</span>
                            <div className="contador">
                                <button onClick={() => decrementar(producto.id)}>-</button>
                                <span>{cantidades[producto.id] || 0}</span>
                                <button onClick={() => incrementar(producto.id)}>+</button>
                            </div>
                        </div>
                    ))}
                </div>
            
            <button>Seguir con el pedido</button>
        </div>
    );
};

export default Pedido;
