import { MenuResponse } from "../../interfaces/MenuResponse.model";
import './Menu.css'

type MenuProps = {
    menu: MenuResponse;
};

const Menu = ({ menu }: MenuProps) => {
    return (
            <div className="containerTotalMenu">
                    <div key={menu.id} className="containerMenu">
                        <h4>{menu.nombre}</h4>
                        {menu.productos.map((producto) => (
                            <div key={producto.id} className="product">
                                <img src={producto.imagen.url}  className="imgFood"/>
                                <div className="productoInfo">
                                    <p><strong>{producto.nombre}</strong></p>
                                    <p>{producto.descripcion}</p>
                                    <p>${producto.precio}</p>
                                </div>
                            </div>
                        ))}
                    </div>
            </div>
    );
};

export default Menu;
