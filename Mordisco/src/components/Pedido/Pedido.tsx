import './Pedido.css'
import { useLocation, useParams, useNavigate } from 'react-router-dom';
import { RestauranteResponse } from '../../interfaces/RestauranteResponse.model';
import { MenuResponse } from '../../interfaces/MenuResponse.model';
import { useState, useEffect } from 'react';
import { useGlobalContext } from '../../context/global.context';
import Select from 'react-select';
import { TipoEntrega } from '../../interfaces/TipoEntrega.model';
import { PedidoRequest } from '../../interfaces/PedidoRequest.model';
import { sendPedido } from '../../interceptors/axios/useApi';


const Pedido = () => {
    const [selectedDireccion, setSelectedDireccion] = useState<any>(null);
    const [precioTotal, setPrecioTotal] = useState(0);
    const [option, setOption] = useState(2)
    const { user } = useGlobalContext();

    const userId = user?.id;

  const navigate = useNavigate();

    const location = useLocation();
    const restaurante = location.state?.restaurante as RestauranteResponse;
    const menu = location.state?.menu as MenuResponse;

    const [cantidades, setCantidades] = useState<{ [id: number]: number }>({});

    const seleccionados = 
    Object.entries(cantidades)
        .filter(([_, cantidad]) => cantidad > 0)
        .map(([id, cantidad]) => ({
            productoId: Number(id),
            cantidad
    }));

    const opcionesDireccion = user?.direcciones.map((d) => ({
        value: d.id, 
        label: `${d.calle} ${d.numero}, ${d.ciudad}`
    })) || [];


    const incrementar = (id: number) => {
        setCantidades(prev => ({ ...prev, [id]: (prev[id] || 0) + 1 }));
    };

    const decrementar = (id: number) => {
        setCantidades(prev => ({ ...prev, [id]: Math.max((prev[id] || 0) - 1, 0) }));
    };

    useEffect(() => {
    const total = Object.entries(cantidades).reduce((acum, [id, cantidad]) => {
        const producto = menu.productos.find(p => p.id === Number(id));
        if (!producto) return acum;
        return acum + producto.precio * cantidad;
    }, 0);
    setPrecioTotal(total);
    }, [cantidades, menu.productos]);

const handleEnviarPedido = () => {
    const tipoEntrega = option === 1 ? TipoEntrega.DELIVERY : TipoEntrega.RETIRO_POR_LOCAL;

    const idDireccion =
        tipoEntrega === TipoEntrega.DELIVERY
            ? selectedDireccion?.value
            : restaurante.direccion.id;

    if (!idDireccion) {
        alert('Debe seleccionar una dirección.');
        return;
    }

    if (seleccionados.length === 0) {
        alert('Debe seleccionar al menos un producto.');
        return;
    }

    const pedido: PedidoRequest = {
        idCliente: userId,
        idRestaurante: restaurante.id,
        idDireccion,
        tipoEntrega,
        productos: seleccionados
    };

    const { call } = sendPedido(pedido);

    call
        .then((res) => {
            alert('Pedido enviado correctamente');
            navigate("/dashboardUser")
        })
        .catch((err) => {
            if (err.name !== 'CanceledError') {
                alert('Error al enviar el pedido');
            }
        });
};

    return (
        <div className='containerTotal-registro'>
            <div className='containerInfo-Pedido'>
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
                        <input className="input red" type="radio" name="entrega" value={2} defaultChecked
                            onChange={() => { setOption(2) }}
                        />
                    </div>
                </div>
                {option == 1 ? 
                    <div>
                        <Select
                            options={opcionesDireccion}
                            isClearable
                            placeholder="Selecciona una dirección"
                            value={selectedDireccion}
                            onChange={(opcion) => {
                                setSelectedDireccion(opcion);
                            }}/>
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
            
            <div className='containerFinal'>
                <div>
                    <p>Precio total: <strong>${precioTotal}</strong></p>
                    <p className='mensajePrecio'> El precio puede sufrir modificaciones al elegir delivery si llueve</p>
                </div>
                <button className='buttonPedido' onClick={handleEnviarPedido}>Confirmar pedido</button>
            </div>
            
        </div>
    );
};

export default Pedido;
