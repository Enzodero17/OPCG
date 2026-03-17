import { useState } from 'react';
import api from '../api/axiosConfig';

function BoosterOpener() {
    const [cards, setCards] = useState([]); // Les cartes du booster
    const [message, setMessage] = useState('');
    const [loading, setLoading] = useState(false); // Pour faire patienter le joueur pendant l'animation

    const openBooster = async () => {
        const userId = localStorage.getItem('user_id');

        if (!userId) {
            setMessage("Erreur : Tu dois te connecter d'abord !");
            return;
        }

        setLoading(true);
        setMessage('');
        setCards([]);

        try {
            const response = await api.get(`/boosters/open/OP-01/${userId}`);

            // On sauvegarde les cartes renvoyées par Java
            setCards(response.data);
            setMessage("Booster ouvert avec succès !");

        } catch (error) {
            setMessage("Erreur : Pas assez de pièces ou problème serveur.", error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ marginTop: '40px', textAlign: 'center' }}>
            <h2>La Boutique</h2>

            <button
                onClick={openBooster}
                disabled={loading}
                style={{
                    padding: '15px 30px',
                    fontSize: '18px',
                    cursor: loading ? 'not-allowed' : 'pointer',
                    backgroundColor: loading ? '#7f8c8d' : '#f1c40f',
                    color: '#1a1a1a',
                    border: 'none',
                    borderRadius: '8px',
                    fontWeight: 'bold',
                    marginTop: '20px',
                    boxShadow: '0 4px 6px rgba(0,0,0,0.3)'
                }}
            >
                {loading ? 'Déchirure de l\'emballage...' : 'Ouvrir un Booster OP-01 (500 pièces)'}
            </button>

            {message && <p style={{ marginTop: '20px', fontWeight: 'bold' }}>{message}</p>}

            {/* Le tapis de jeu où les cartes vont s'afficher */}
            <div style={{
                display: 'flex',
                flexWrap: 'wrap',
                gap: '15px',
                justifyContent: 'center',
                marginTop: '30px',
                padding: '20px'
            }}>
                {/* On boucle sur les cartes reçues pour les dessiner une par une */}
                {cards.map((variant, index) => (
                    <div key={index} style={{
                        width: '140px',
                        height: '200px',
                        backgroundColor: '#2c3e50',
                        border: '3px solid #ecf0f1',
                        borderRadius: '10px',
                        display: 'flex',
                        flexDirection: 'column',
                        justifyContent: 'center',
                        alignItems: 'center',
                        padding: '10px',
                        boxShadow: '0 4px 8px rgba(0,0,0,0.5)'
                    }}>
                        <span style={{ fontSize: '30px' }}>🃏</span>
                        <p style={{ fontSize: '12px', marginTop: '15px', textAlign: 'center', fontWeight: 'bold' }}>
                            {variant.card?.name || 'Nom Inconnu'}
                        </p>
                        <p style={{ fontSize: '11px', color: '#f1c40f', marginTop: '5px' }}>
                            {variant.rarity || 'Rareté Inconnue'}
                        </p>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default BoosterOpener;