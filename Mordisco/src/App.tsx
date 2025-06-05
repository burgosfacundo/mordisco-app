import { useState } from 'react'
import { BrowserRouter, Route, Routes } from "react-router";
import Header from './components/Header/Header'
import Footer from './components/Footer/Footer'
import Home from './components/Home/Home'
import Login from './components/Login/Login'
import SignUp from './components/SignUp/SignUp'
import HomeRestaurantes from './components/HomeRestaurantes/HomeRestaurantes';
import FichaRestaurante from './components/FichaRestaurantes/FichaRestaurante';
import './App.css'
import { GlobalProvider } from './context/global.provider';
import Pedido from './components/Pedido/Pedido';


function App() {
  const [count, setCount] = useState(0)

  return (
    <GlobalProvider>
    <div className='containerApp'>
      <BrowserRouter>
        <Header/>
        <Routes>
          <Route path='/' element={<Home />} />
          <Route path='/login' element={<Login />} />
          <Route path='/signUp' element={<SignUp />} />
          <Route path="/dashboardUser" element={<HomeRestaurantes />} />
          <Route path="/ficha/:id" element={<FichaRestaurante />} />
          <Route path="/pedido/:id" element={<Pedido/>} />
        </Routes>
        <Footer />
      </BrowserRouter>
    </ div>
    </ GlobalProvider>
  )
}

export default App
