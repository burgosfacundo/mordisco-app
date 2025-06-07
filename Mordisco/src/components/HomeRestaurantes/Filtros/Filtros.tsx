import './Filtros.css';
import { useEffect, useState } from "react";
import Select from 'react-select';
import { RestauranteResponse } from "../../../interfaces/RestauranteResponse.model";
import { fetchRestaurantsByPromocion } from '../../../interceptors/axios/useApi'

type FiltroProps = {
    restaurante: RestauranteResponse[];
    onFilterChange: (filtered: RestauranteResponse[] | null) => void;
};

const Filtros = ({ restaurante, onFilterChange }: FiltroProps) => {
    const [promocion, setPromocion] = useState(false);
    const [selectedRestaurante, setSelectedRestaurante] = useState<any>(null);
    const [selectedCiudad, setSelectedCiudad] = useState<any>(null);

    const opcionesRestaurante = [
        { label: "Selecciona un restaurante", value: "" },
        ...restaurante
            .map(r => ({ label: r.razonSocial, value: r.razonSocial }))
            .filter((v,i,a) => a.findIndex(t => (t.value === v.value)) === i) // elimina duplicados
    ];
    const ciudadesUnicas = Array.from(
        new Set(restaurante.map(r => r.direccion.ciudad))
    );

    const opcionesCiudad = [
        { label: "Selecciona una ciudad", value: "" },
        ...ciudadesUnicas.map(ciudad => ({ label: ciudad, value: ciudad })),
    ];


    const handleChangeRestaurante = (selected: any) => {
        setSelectedRestaurante(selected);
        setSelectedCiudad(null);

        if (!selected || selected.value === "") {
            onFilterChange(null);
        } else {
            const resultado = restaurante.filter(r => r.razonSocial === selected.value);
            onFilterChange(resultado);
        }
        setPromocion(false);
    };

    const handleChangeCiudad = (selected: any) => {
        setSelectedCiudad(selected);
        setSelectedRestaurante(null);

        if (!selected || selected.value === "") {
            onFilterChange(null);
        } else {
            const resultado = restaurante.filter(r => r.direccion.ciudad === selected.value);
            onFilterChange(resultado);
        }
        setPromocion(false);
    };

    const handlePromocionChange = async () => {
        setSelectedRestaurante(null);
        setSelectedCiudad(null);

        if (!promocion) {
            try {
                const { call, controller } = fetchRestaurantsByPromocion();
                const response = await call;
                onFilterChange(response.data);
                setPromocion(true);
            } catch (error) {
                console.error("Error al cargar restaurantes con promoción", error);
                onFilterChange(null);
                setPromocion(false);
            }
        } else {
            onFilterChange(null);
            setPromocion(false);
        }
    };

    return (
        <div className="containerTotal-Filters">
            <Select
                options={opcionesRestaurante}
                isClearable
                onChange={handleChangeRestaurante}
                placeholder="Selecciona un restaurante"
                value={selectedRestaurante}
            />

            <Select
                options={opcionesCiudad}
                isClearable
                onChange={handleChangeCiudad}
                placeholder="Selecciona una ciudad"
                value={selectedCiudad}
            />

            <div className="radio-input-filtros">
                <label>
                    <input
                        type="radio"
                        name="promocion"
                        checked={promocion}
                        onChange={handlePromocionChange}
                    />
                    Con Promoción
                </label>
            </div>
        </div>
    );
};

export default Filtros;