import React from 'react'
import './Header.css'
import Logo from '../../assets/LogoMordisco.jpeg'

const Header = () => {
  return (
    <div className='containerHeader'>
      <img src={Logo} className='logoMordico'/>
      <section className='containerUsuario'>
        <button>Iniciar Sesion</button>
        <button>Registrarse</button>
      </section>
    </div>
  )
}

export default Header
