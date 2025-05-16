import data from '../../utils/Restaurantes.json'
import Card from '../Card/Card.tsx'
import './HomeRestaurante.css'

const HomeRestaurantes = () => {
    return (
        <div className='containerCards'>
            <div className="parent">
                {data.restaurantes.map((restaurante, index) => (
                    <Card
                        key={index}
                        nombre={restaurante.nombre}
                        imagenUrl={restaurante.imagenUrl}
                        direccion={restaurante.direccion}
                    />
                ))}
            </div>
            </div>
            
    )
}

export default HomeRestaurantes

