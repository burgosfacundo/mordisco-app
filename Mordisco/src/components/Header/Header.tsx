import './Header.css';
import Logo from '../../assets/LogoHeader.png';
import { useNavigate, useLocation } from "react-router-dom";

const Header = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const path = location.pathname;

  const goToLogin = () => {
    navigate("/login");
  };

  const goToSignUp = () => {
    navigate("/signUp");
  };

  return (
    <div className='containerHeader'>
      <img src={Logo} className='logoMordico' alt='Logo Mordico'/>
      <section className='containerUsuario'>
        {path === "/" && (
          <>
            <button onClick={goToLogin}>Iniciar Sesión</button>
            <button onClick={goToSignUp}>Registrarse</button>
          </>
        )}
        {path === "/login" && (
          <button onClick={goToSignUp}>Registrarse</button>
        )}
        {path === "/signUp" && (
          <button onClick={goToLogin}>Iniciar Sesión</button>
        )}
        {path !== "/" && path !== "/login" && path !== "/signUp" && (
          <p className='mensajeRuta'></p>
        )}
      </section>
    </div>
  );
};

export default Header;
