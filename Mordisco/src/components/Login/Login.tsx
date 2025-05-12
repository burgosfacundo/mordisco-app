import "./Login.css"
import { Formik, Form, Field } from 'formik' ;       
import * as Yup from 'yup' ;    
import { Link } from 'react-router'; 

const Login = () => {
    const SignupSchema = Yup.object().shape({
        email: Yup.string().email('email mal ingresado').required('Obligatorio'),
        password: Yup.string().required('Obligatorio')
        });

    return (
        <section className='containerTotal'>
            <div className='containerLogin'>
                <h1>Login</h1>
                    <Formik
                    initialValues={{
                        email: '',
                        password: ''
                    }}
                    validationSchema={SignupSchema}
                    onSubmit={values => {
                        // same shape as initial values
                        console.log(values);
                    }}
                    >
                    {({ errors, touched }) => (
                        <Form className='containerForm'>
                            <div className='containerInput'>
                                <label>Email*</label>
                                <Field name="email" type="email" className="input" />
                                {errors.email && touched.email ? <div className="error">{errors.email}</div> : null}
                            </div>
                            
                            <div className='containerInput'>
                                <label>Contraseña*</label>
                                <Field name="password" className="input"/>
                                {errors.password && touched.password ? <div className="error">{errors.password}</div> : null}
                            </div>
                            
                        
                            <button type="submit" className="btn btn-danger">Iniciar Sesión</button>

                            <div className="containerRegistro">
                                <p className="cuenta">¿No tienes una cuenta?</p>
                                <Link to='' className='register-btn'>Haz click para registrarte</Link>
                            </div>
                        </Form>
                    )}
                    </Formik>
            </div>
        </section>
    )
}

export default Login
