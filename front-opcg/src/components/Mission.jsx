import { useState, useEffect } from 'react';
import api from '../api/axiosConfig';

function Missions({ setCoins }) {
    const [missions, setMissions] = useState([]);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState('');

    useEffect(() => {
        fetchMissions();
    }, []);

    const fetchMissions = async () => {
        setLoading(true);
        const userId = localStorage.getItem('user_id');
        try {
            const response = await api.get(`/missions/${userId}`);

            // On trie : Terminées, en cours, déjà réclamées
            const sortedMissions = response.data.sort((a, b) => {
                if (a.completed && !a.claimed && (!b.completed || b.claimed)) return -1;
                if (b.completed && !b.claimed && (!a.completed || a.claimed)) return 1;
                if (a.claimed && !b.claimed) return 1;
                if (b.claimed && !a.claimed) return -1;
                return 0;
            });

            setMissions(sortedMissions);
        } catch (err) {
            console.error(err);
            setMessage("Impossible de charger le journal de quêtes.");
        } finally {
            setLoading(false);
        }
    };

    const claimReward = async (missionId) => {
        const userId = localStorage.getItem('user_id');
        try {
            const response = await api.post(`/missions/${userId}/claim/${missionId}`);

            if (setCoins) {
                setCoins(response.data.newBalance);
            }
            localStorage.setItem('coins', response.data.newBalance);

            setMessage(`${response.data.message}`);

            fetchMissions();
        } catch (err) {
            setMessage("Erreur : " + (err.response?.data?.message || "Impossible de récupérer la récompense."));
        }
    };

    if (loading && missions.length === 0) {
        return <h3 style={{ textAlign: 'center', marginTop: '50px' }}>Chargement des quêtes... ⏳</h3>;
    }

    return (
        <div style={{ padding: '20px', maxWidth: '800px', margin: '0 auto', textAlign: 'center' }}>
            <h2 style={{ fontSize: '32px', marginBottom: '10px' }}>Journal de Quêtes</h2>
            <p style={{ color: '#bdc3c7', marginBottom: '30px' }}>Complète ces missions pour gagner des pièces d'or !</p>

            {message && (
                <div style={{ backgroundColor: '#2c3e50', border: '2px solid #f1c40f', color: 'white', padding: '10px 20px', borderRadius: '10px', display: 'inline-block', marginBottom: '20px', fontWeight: 'bold' }}>
                    {message}
                </div>
            )}

            {missions.length === 0 ? (
                <p>Aucune mission disponible pour le moment.</p>
            ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                    {missions.map((um) => {
                        const { mission, currentAmount, completed, claimed } = um;

                        // Calcul du pourcentage pour la jauge
                        const progressPercent = Math.min((currentAmount / mission.targetAmount) * 100, 100);

                        // Détermination du style selon l'état
                        let cardBg = '#2c3e50';
                        let borderStyle = 'none';
                        let opacity = 1;

                        if (claimed) {
                            opacity = 0.5;
                        } else if (completed) {
                            cardBg = '#34495e';
                            borderStyle = '2px solid #f1c40f';
                        }

                        return (
                            <div key={um.id} style={{
                                backgroundColor: cardBg, border: borderStyle, borderRadius: '15px',
                                padding: '20px', display: 'flex', alignItems: 'center',
                                justifyContent: 'space-between', opacity: opacity,
                                boxShadow: completed && !claimed ? '0 0 15px rgba(241, 196, 15, 0.3)' : '0 4px 6px rgba(0,0,0,0.3)',
                                transition: 'transform 0.2s',
                                transform: completed && !claimed ? 'scale(1.02)' : 'scale(1)'
                            }}>

                                <div style={{ textAlign: 'left', flex: '1' }}>
                                    <h3 style={{ margin: '0 0 5px 0', color: completed && !claimed ? '#f1c40f' : '#ecf0f1' }}>
                                        {mission.title}
                                    </h3>
                                    <p style={{ margin: '0 0 10px 0', color: '#bdc3c7', fontSize: '14px' }}>
                                        {mission.description}
                                    </p>

                                    <div style={{ width: '80%', height: '10px', backgroundColor: '#1a252f', borderRadius: '5px', overflow: 'hidden' }}>
                                        <div style={{ width: `${progressPercent}%`, height: '100%', backgroundColor: completed ? '#27ae60' : '#3498db', transition: 'width 0.5s' }}></div>
                                    </div>
                                    <p style={{ margin: '5px 0 0 0', fontSize: '12px', color: '#95a5a6', fontWeight: 'bold' }}>
                                        {currentAmount} / {mission.targetAmount}
                                    </p>
                                </div>

                                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', minWidth: '150px' }}>
                                    <span style={{ fontSize: '20px', fontWeight: 'bold', color: '#f1c40f', marginBottom: '10px' }}>
                                        💰 {mission.rewardCoins}
                                    </span>

                                    {claimed ? (
                                        <span style={{ color: '#7f8c8d', fontWeight: 'bold', padding: '10px 20px', backgroundColor: '#1a252f', borderRadius: '5px' }}>
                                            Terminé.
                                        </span>
                                    ) : completed ? (
                                        <button
                                            onClick={() => claimReward(mission.id)}
                                            style={{ padding: '10px 20px', backgroundColor: '#f39c12', color: 'white', border: 'none', borderRadius: '5px', fontWeight: 'bold', cursor: 'pointer', boxShadow: '0 4px 6px rgba(243, 156, 18, 0.4)' }}
                                        >
                                            Réclamer !
                                        </button>
                                    ) : (
                                        <span style={{ color: '#bdc3c7', fontWeight: 'bold', padding: '10px 20px', backgroundColor: '#34495e', borderRadius: '5px' }}>
                                            En cours...
                                        </span>
                                    )}
                                </div>

                            </div>
                        );
                    })}
                </div>
            )}
        </div>
    );
}

export default Missions;