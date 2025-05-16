import './Card.css'

type CardProps = {
  nombre: string
  imagenUrl: string
  direccion: string
}

const Card = (card : CardProps) => {
        return (
        <div className='containerCard'>
            <h2 className='titulo'>{card.nombre}</h2>
            <img className='img' src={card.imagenUrl} alt={card.nombre} />
            <p className='direccion'>{card.direccion}</p>
        </div>
    )
}

export default Card
