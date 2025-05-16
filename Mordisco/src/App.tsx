import { useState } from 'react'
import { BrowserRouter, Route, Routes } from "react-router";
import Header from './components/Header/Header'
import Footer from './components/Footer/Footer'
import HomeRestaurantes from './components/HomeRestaurantes/HomeRestaurantes'
import './App.css'


function App() {
  const [count, setCount] = useState(0)

  return (
    <div className='containerApp'>
      <BrowserRouter>
        <Header/>
          <HomeRestaurantes/>
        <Footer />
      </BrowserRouter>
    </ div>
  )
}

export default App
