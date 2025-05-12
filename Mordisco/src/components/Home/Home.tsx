import './Home.css'
import Delivery from "../../assets/delivery.png"

const Home = () => {
    return (
    <div className='containerHome'>
        <section className='banner'>
            <div className='textoPublicitario'>
                <p className='texto'>Explorá cientos de platos de tus locales favoritos, pedí en segundos y recibí en tu puerta. Rápido, fácil y sin moverte</p>
            </div>
            <img src={Delivery} className='imagen'/>
        </section>
    </div>
    )
}

export default Home
