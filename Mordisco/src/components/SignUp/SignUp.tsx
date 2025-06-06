import { useState } from 'react';
import './SignUp.css'
import { Formik, Form, Field } from 'formik' ;       
import * as Yup from 'yup' ;   
import { Link } from 'react-router'; 
import { useNavigate } from "react-router-dom";
import { signUp } from '../../interceptors/axios/useApi';

interface SignUpParams{
    nombre:string,
    apellido:string,
    telefono: string,
    email: string,
    password: string,
    rolId:number
}

const SignUp = () => {
        const [rol, setRol] = useState<number>(1);

            const navigate = useNavigate();
        
            const fetch = async (params: SignUpParams): Promise<string> => {
                const { call } = signUp(params);
                const response = await call;
                return response.data;
            };

        const SignupSchema = Yup.object().shape({
            nombre: Yup.string().max(50, 'El nombre debe tener maximo 50 caracteres').required('Obligatorio'),
            apellido: Yup.string().max(50, 'El apellido debe tener maximo 50 caracteres').required('Obligatorio'),
            telefono: Yup.string().max(12, 'El nombre debe tener maximo 12 caracteres').required('Obligatorio'),
            email: Yup.string().email('email mal ingresado').required('Obligatorio'),
            password: Yup.string().required('Obligatorio'),
            rol: Yup.number().oneOf([1, 2, 3], 'Rol inválido').required('El rol es obligatorio'),
            });

    return (
        <section className='containerTotal'>
            <div className='containerSignUp'>
                <h1 className='title'>Registrarse</h1>
                    <Formik
                    initialValues={{
                        nombre: '',
                        apellido:'',
                        telefono:'',
                        email: '',
                        password: '',
                        rol: 1
                    }}
                    validationSchema={SignupSchema}
                                onSubmit={async (values, { setSubmitting }) => {
                                    try {
                                        const result = await fetch({ nombre: values.nombre, apellido: values.apellido, telefono: values.telefono, email: values.email, password: values.password, rolId: values.rol });

                                        if (result) {
                                            navigate("/login");
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
                    {({ errors, touched, setFieldValue, values }) => (
                        <section className='containerSection'>
                        <div className='divText'>
                            {rol === 1 ? <div><h2 className='texto'> Encontrá tu plato favorito acá.</h2> <h2 className='texto'>¡Explorá, elegí y disfrutá! </h2></div> : 
                                    <h2 className='texto'> Gestioná tu restaurante online. ¡Armá tu menú y manejá pedidos fácil!</h2>}
                        </div>
                        
                        <Form className='containerForm'>

                            <div className="radio-input-signUp">
                                <div className='containerRadioButton'>
                                    <label>Usuario</label>
                                    <input className="input yellow" type="radio" name="rol" value={1} checked={values.rol === 1}
                                        onChange={() => {
                                        setFieldValue('rol', 1);
                                        setRol(1);
                                    }}
                                    />
                                </div>
                                <div className='containerRadioButton'>
                                    <label>Restaurante </label>
                                    <input className="input red" type="radio" name="rol" value={2} checked={values.rol === 2}
                                        onChange={() => {
                                        setFieldValue('rol', 2);
                                        setRol(2);
                                    }}
                                    />
                                </div>
                            </div>
                            <div className='formInfo'>
                                <section className='userInfo'>
                                    <div className='containerInput'>
                                        <label>Nombre*</label>
                                        <Field  name="nombre" className="input-text"/>
                                        {errors.nombre && touched.nombre ? <div className="error">{errors.nombre}</div> : null}
                                    </div>

                                    <div className='containerInput'>
                                        <label>Apellido*</label>
                                        <Field name="apellido"  className="input-text" />
                                        {errors.apellido && touched.apellido ? <div className="error">{errors.apellido}</div> : null}
                                    </div>
                                    
                                    <div className='containerInput'>
                                        <label>Telefono*</label>
                                        <Field name="telefono" className="input-text"/>
                                        {errors.telefono && touched.telefono ? <div className="error">{errors.telefono}</div> : null}
                                    </div>
                                </section>
                                <section className='loginInfo'>
                                    <div className='containerInput'>
                                        <label>Email*</label>
                                        <Field name="email" type="email" className="input-text" />
                                        {errors.email && touched.email ? <div className="error">{errors.email}</div> : null}
                                    </div>
                                    
                                    <div className='containerInput'>
                                        <label>Contraseña*</label>
                                        <Field name="password" className="input-text"/>
                                        {errors.password && touched.password ? <div className="error">{errors.password}</div> : null}
                                    </div>
                                </section>
                            </div>
                            <button type="submit" className="btn btn-danger">Registrarse</button>

                            <div className="containerRegistro">
                                <p className="cuenta">¿Ya tenes una cuenta?</p>
                                <Link to='/login' className='register-btn'>Haz click para volver atras</Link>
                            </div>
                        </Form>
                        </section>
                    )}
                    </Formik>
            </div>
        </section>
    )
}

export default SignUp