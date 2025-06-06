import "./Login.css"
import { Formik, Form, Field } from 'formik' ;       
import * as Yup from 'yup' ;    
import { Link } from 'react-router'; 
import { login } from "../../interceptors/axios/useApi";
import { useNavigate } from "react-router-dom";
import * as jwt_decode from "jwt-decode";

interface LoginParams{
    email: string,
    password: string
}

interface TokenPayload {
        role: {
            id: number;
            nombre: string;
        };
    }

    interface UserLoginResponse {
    jwt: string
}

const Login = () => {

    const navigate = useNavigate();

    const fetch = async (params: LoginParams): Promise<UserLoginResponse> => {
        const { call } = login(params);
        const response = await call;
        return response.data;
    };

    const SignupSchema = Yup.object().shape({
        email: Yup.string().email('email mal ingresado').required('Obligatorio'),
        password: Yup.string().required('Obligatorio')
    });

    return (
        <section className='containerTotal'>
        <div className='containerLogin'>
            <h1>Login</h1>
            <Formik
            initialValues={{ email: '', password: '' }}
            validationSchema={SignupSchema}
            onSubmit={async (values, { setSubmitting }) => {
                try {
                    const result = await fetch({ email: values.email, password: values.password });
                    
                    if (result.jwt) {
                    localStorage.setItem("token", result.jwt);
                    const decoded = jwt_decode.jwtDecode<TokenPayload>(result.jwt);
                    
                    switch (decoded.role.id) {
                        case 2:
                        navigate("/dashboardUser");
                        break;
                        case 1:
                        navigate("/dashboardUser");
                        break;
                        case 3:
                        navigate("/homeLocales");
                        break;
                        default:
                        navigate("/login");
                        break;
                    }
                    } else {
                    alert("Login incorrecto");
                    }
                } catch (error) {
                    alert("Error en el login");
                } finally {
                    setSubmitting(false);
                }
                }}

            >
            {({ errors, touched, isSubmitting }) => (
                <Form className='containerForm-login'>
                <div className='containerInput-login'>
                    <label>Email*</label>
                    <Field name="email" type="email" className="inputLogin" />
                    {errors.email && touched.email ? <div className="error">{errors.email}</div> : null}
                </div>

                <div className='containerInput-login'>
                    <label>Contraseña*</label>
                    <Field name="password" type="password" className="inputLogin" />
                    {errors.password && touched.password ? <div className="error">{errors.password}</div> : null}
                </div>

                <button type="submit" className="btn btn-danger" disabled={isSubmitting}>
                    {isSubmitting ? "Ingresando..." : "Iniciar Sesión"}
                </button>

                <div className="containerRegistro">
                    <p className="cuenta">¿No tienes una cuenta?</p>
                    <Link to="/signUp" className='register-btn'>Haz click para registrarte</Link>
                </div>
                </Form>
            )}
            </Formik>
        </div>
        </section>
    );
};

export default Login;
