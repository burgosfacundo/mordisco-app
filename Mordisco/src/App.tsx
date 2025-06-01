import { useState } from 'react'
import { BrowserRouter, Route, Routes } from "react-router";
import Header from './components/Header/Header'
import Footer from './components/Footer/Footer'
import Home from './components/Home/Home'
import Login from './components/Login/Login'
import SignUp from './components/SignUp/SignUp'
import './App.css'


function App() {
  const [count, setCount] = useState(0)

  return (
    <div className='containerApp'>
      <BrowserRouter>
        <Header/>
        <SignUp/>
        <Footer />
      </BrowserRouter>
    </ div>
  )
}

export default App
