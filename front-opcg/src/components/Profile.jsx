import { useState, useEffect } from 'react';
import api from '../api/axiosConfig';

function Profile() {
    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(true);
    const [message, setMessage] = useState('');

    useEffect(() => {
        fetchProfileStats();
    }, []);

    const fetchProfileStats = async () => {
        const userId = localStorage.getItem('user_id');
        try {
            const response = await api.get(`/collection/${userId}/profile-stats`);
            console.log("DONNÉES REÇUES DE JAVA :", response.data);
            setStats(response.data);
        } catch (err) {
            console.error(err);
            setMessage("Impossible de charger le profil.");
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <h3 style={{ textAlign: 'center', marginTop: '50px' }}>Chargement du profil... ⏳</h3>;
    if (!stats) return <p style={{ textAlign: 'center', color: '#e74c3c' }}>{message || "Erreur de chargement."}</p>;

    return (
        <div style={{ padding: '30px', maxWidth: '900px', margin: '0 auto', color: 'white' }}>

            <div style={{
                display: 'flex', alignItems: 'center', gap: '30px',
                backgroundColor: '#2c3e50', padding: '30px', borderRadius: '20px',
                boxShadow: '0 8px 16px rgba(0,0,0,0.3)', marginBottom: '30px'
            }}>
                <div style={{
                    width: '100px', height: '100px', borderRadius: '50%',
                    backgroundColor: '#f1c40f', display: 'flex', justifyContent: 'center',
                    alignItems: 'center', fontSize: '50px', border: '4px solid white',
                    boxShadow: '0 4px 10px rgba(0,0,0,0.3)'
                }}>
                    🏴‍☠️
                </div>
                <div style={{ textAlign: 'left' }}>
                    <h2 style={{ margin: '0 0 5px 0', fontSize: '32px', color: '#f1c40f' }}>{stats.username}</h2>
                    <p style={{ margin: 0, color: '#bdc3c7', fontSize: '18px' }}>Rang : Collectionneur Émérite</p>
                    <p style={{ margin: '5px 0 0 0', color: '#27ae60', fontWeight: 'bold' }}>🪙 {stats.coins} pièces en poche</p>
                </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '20px', marginBottom: '40px' }}>

                <div style={{ backgroundColor: '#1e272e', padding: '20px', borderRadius: '15px', borderLeft: '5px solid #3498db', textAlign: 'center' }}>
                    <span style={{ fontSize: '30px' }}>📦</span>
                    <h4 style={{ margin: '10px 0 5px 0', color: '#bdc3c7' }}>Boosters Ouverts</h4>
                    <p style={{ margin: 0, fontSize: '24px', fontWeight: 'bold' }}>{stats.totalBoostersOpened}</p>
                </div>

                <div style={{ backgroundColor: '#1e272e', padding: '20px', borderRadius: '15px', borderLeft: '5px solid #2ecc71', textAlign: 'center' }}>
                    <span style={{ fontSize: '30px' }}>🃏</span>
                    <h4 style={{ margin: '10px 0 5px 0', color: '#bdc3c7' }}>Cartes Uniques</h4>
                    <p style={{ margin: 0, fontSize: '24px', fontWeight: 'bold' }}>{stats.uniqueCardsOwned} / {stats.totalCardsInGame}</p>
                </div>

                <div style={{ backgroundColor: '#1e272e', padding: '20px', borderRadius: '15px', borderLeft: '5px solid #f1c40f', textAlign: 'center' }}>
                    <span style={{ fontSize: '30px' }}>📊</span>
                    <h4 style={{ margin: '10px 0 5px 0', color: '#bdc3c7' }}>Complétion Globale</h4>
                    <p style={{ margin: 0, fontSize: '24px', fontWeight: 'bold', color: '#f1c40f' }}>{stats.globalCompletionRatio}%</p>
                    <div style={{ width: '100%', height: '6px', backgroundColor: '#2c3e50', borderRadius: '3px', marginTop: '10px', overflow: 'hidden' }}>
                        <div style={{ width: `${stats.globalCompletionRatio}%`, height: '100%', backgroundColor: '#f1c40f' }}></div>
                    </div>
                </div>

            </div>

            <div style={{ backgroundColor: '#2c3e50', padding: '30px', borderRadius: '20px', boxShadow: '0 8px 16px rgba(0,0,0,0.3)', textAlign: 'center' }}>
                <h3 style={{ margin: '0 0 20px 0', color: '#f1c40f', fontSize: '22px' }}>⭐ Carte Coup de Cœur</h3>

                {stats.favoriteVariantId ? (
                    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '15px' }}>
                        <img
                            src={stats.favoriteCardImageUrl}
                            alt={stats.favoriteCardName}
                            style={{ width: '220px', borderRadius: '15px', boxShadow: '0 10px 20px rgba(0,0,0,0.5)', transition: 'transform 0.3s' }}
                            onMouseEnter={(e) => e.currentTarget.style.transform = 'scale(1.05)'}
                            onMouseLeave={(e) => e.currentTarget.style.transform = 'scale(1)'}
                        />
                        <p style={{ margin: 0, fontSize: '18px', fontWeight: 'bold' }}>{stats.favoriteCardName}</p>
                    </div>
                ) : (
                    <div style={{ padding: '30px', border: '2px dashed #7f8c8d', borderRadius: '15px', color: '#bdc3c7' }}>
                        <p style={{ margin: '0 0 10px 0' }}>Tu n'as pas encore choisi de carte préférée.</p>
                        <p style={{ margin: 0, fontSize: '14px', color: '#95a5a6' }}>(Pour en ajouter une, il te suffira de cliquer sur un bouton "Définir comme favori" depuis ton classeur d'inventaire !)</p>
                    </div>
                )}
            </div>

        </div>
    );
}

export default Profile;