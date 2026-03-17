import { useState } from 'react';
import api from '../api/axiosConfig';

function Login() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState(''); // Message de succès ou d'erreur

    // Clique sur "Se connecter"
    const handleLogin = async (e) => {
        e.preventDefault(); // Empêche la page de se recharger

        try {
            const response = await api.post('/auth/login', {
                email: email,
                password: password
            });

            // Si Java dit "OK 200"
            const token = response.data.token;
            const userId = response.data.userId;

            // On sauvegarde dans la mémoire du navigateur
            localStorage.setItem('jwt_token', token);
            localStorage.setItem('user_id', userId);

            setMessage('Connexion réussie ! Badge récupéré.');

        } catch (error) {
            // Si Java dit "403 Forbidden" ou "404 Not Found"
            setMessage('Erreur : Identifiants incorrects. ', error);
        }
    };

    return (
        <div style={{ marginTop: '40px' }}>
            <h2 style={{ textAlign: 'center' }}>Connexion</h2>

            <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', width: '300px', margin: '20px auto', gap: '15px' }}>
                <input
                    type="email"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    style={{ padding: '10px', borderRadius: '5px', border: 'none' }}
                />

                <input
                    type="password"
                    placeholder="Mot de passe"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                    style={{ padding: '10px', borderRadius: '5px', border: 'none' }}
                />

                <button type="submit" style={{ padding: '10px', cursor: 'pointer', backgroundColor: '#e74c3c', color: 'white', border: 'none', borderRadius: '5px', fontWeight: 'bold' }}>
                    Se connecter
                </button>
            </form>

            {/* On affiche le message seulement s'il y en a un */}
            {message && <p style={{ textAlign: 'center', fontWeight: 'bold' }}>{message}</p>}
        </div>
    );
}

export default Login;