import { AuthProvider } from './context/AuthContext.tsx';
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import 'bootstrap/dist/css/bootstrap.min.css';

import { initAxios } from './interceptors/axios.interceptor.tsx';

initAxios();

createRoot(document.getElementById('root')!).render(
  <AuthProvider>
    <App />
  </AuthProvider>
)
