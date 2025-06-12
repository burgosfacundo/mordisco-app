import "./horario.css"

type Horario = {
        id: number;
        dia: string;
        horaApertura: string;
        horaCierre: string;
    };

    type HorarioProps = {
        horarios: Horario[];
    };

    const diasEnOrden = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"];

    function traducirDia(dia: string): string {
    const traducciones: Record<string, string> = {
        MONDAY: "Lunes",
        TUESDAY: "Martes",
        WEDNESDAY: "Miércoles",
        THURSDAY: "Jueves",
        FRIDAY: "Viernes",
        SATURDAY: "Sábado",
        SUNDAY: "Domingo",
    };
    return traducciones[dia.toUpperCase()] || dia;
}

const Horario = ({ horarios }: HorarioProps) => {
    const agrupado = horarios.reduce((acc: Record<string, Horario[]>, item) => {
        if (!acc[item.dia]) acc[item.dia] = [];
        acc[item.dia].push(item);
        return acc;
    }, {});

    return (
        <div className="containerHorario">
            <p style={{ paddingTop: '2%' }}>Horarios:</p>
        {diasEnOrden.map((dia) => {
            const horariosDia = agrupado[dia];
            if (!horariosDia) return null;

            return (
            <div className="divHorario" key={dia}>
                
                <strong>{traducirDia(dia)}:</strong>{" "}
                {horariosDia.map((h, index) => (
                    <span key={index}>
                        {h.horaApertura} - {h.horaCierre}
                        {index < horariosDia.length - 1 && ", "}
                    </span>
                ))}
                
            </div>
            );
        })}
        </div>
    );
    };

export default Horario;
