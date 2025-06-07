import './Header.css';
import Logo from '../../assets/LogoHeader.png';
import { useNavigate, useLocation } from "react-router-dom";
import { useGlobalContext } from '../../context/global.context';

const Header = () => {
  const { setUser } = useGlobalContext();
  const navigate = useNavigate();
  const location = useLocation();
  const path = location.pathname;

  const goToLogin = () => {
    navigate("/login");
  };

  const goToSignUp = () => {
    navigate("/signUp");
  };

const finalizarSesion = () => {
  localStorage.removeItem('token');
  setUser(null);
  navigate("/");
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
          <button onClick={finalizarSesion}>Cerrar Sesión</button>
        )}
      </section>
    </div>
  );
};

export default Header;
