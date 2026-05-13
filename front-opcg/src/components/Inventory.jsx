import { useState, useEffect } from 'react';
import api from '../api/axiosConfig';

const availableSets = [
    {
        id: 'OP-01',
        name: 'Romance Dawn',
        image: '/op-01.png'
    }
];

function Inventory() {
    const [selectedSet, setSelectedSet] = useState(null);
    const [pokedex, setPokedex] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        if (selectedSet) {
            fetchPokedexData(selectedSet.id);
        }
    }, [selectedSet]);

    const fetchPokedexData = async (setId) => {
        setLoading(true);
        setError('');
        const userId = localStorage.getItem('user_id');

        try {
            const response = await api.get(`/library/sets/${setId}/user/${userId}`);

            const formattedPokedex = response.data.map((item, index) => {
                const variantData = item.variant || item.cardVariant;
                return {
                    cardNumber: variantData?.id || variantData?.cardNumber || (index + 1),
                    imageUrl: variantData?.imageUrl,
                    isOwned: item.isOwned !== undefined ? item.isOwned : item.owned,
                    quantity: item.quantity || 0
                };
            });

            setPokedex(formattedPokedex);

        } catch (err) {
            console.error(err);
            setError("Erreur lors du chargement du Pokédex.");
        } finally {
            setLoading(false);
        }
    };

    // Fonction pour refermer le classeur et revenir à la liste
    const backToSets = () => {
        setSelectedSet(null);
        setPokedex([]);
    };

    if (!selectedSet) {
        return (
            <div style={{ padding: '20px', textAlign: 'center', minHeight: '600px' }}>
                <h2>Choisis une extension à consulter</h2>

                <div style={{ display: 'flex', justifyContent: 'center', gap: '30px', marginTop: '40px' }}>
                    {availableSets.map((set) => (
                        <div
                            key={set.id}
                            onClick={() => setSelectedSet(set)}
                            style={{
                                backgroundColor: '#2c3e50',
                                padding: '20px',
                                borderRadius: '15px',
                                cursor: 'pointer',
                                transition: 'transform 0.2s, boxShadow 0.2s',
                                boxShadow: '0 8px 15px rgba(0,0,0,0.3)'
                            }}
                            onMouseEnter={(e) => { e.currentTarget.style.transform = 'translateY(-10px)'; e.currentTarget.style.boxShadow = '0 15px 25px rgba(241, 196, 15, 0.4)'; }}
                            onMouseLeave={(e) => { e.currentTarget.style.transform = 'translateY(0)'; e.currentTarget.style.boxShadow = '0 8px 15px rgba(0,0,0,0.3)'; }}
                        >
                            <img src={set.image} alt={set.name} style={{ width: '150px', borderRadius: '10px' }} />
                            <h3 style={{ margin: '15px 0 5px 0', color: '#ecf0f1' }}>{set.name}</h3>
                            <p style={{ margin: 0, color: '#f1c40f', fontWeight: 'bold' }}>{set.id}</p>
                        </div>
                    ))}
                </div>
            </div>
        );
    }

    if (loading) return <h3 style={{ textAlign: 'center', marginTop: '50px' }}>Ouverture du classeur... ⏳</h3>;
    if (error) return (
        <div style={{ textAlign: 'center', marginTop: '50px' }}>
            <h3 style={{ color: '#e74c3c' }}>{error}</h3>
            <button onClick={backToSets} style={{ padding: '10px 20px', marginTop: '20px', cursor: 'pointer', fontWeight: 'bold' }}>Retour aux extensions</button>
        </div>
    );

    const cardsOwnedCount = pokedex.filter(card => card.isOwned).length;
    const completionRatio = Math.round((cardsOwnedCount / pokedex.length) * 100) || 0;

    return (
        <div style={{ padding: '20px', textAlign: 'center' }}>

            {/* Barre de titre et bouton de retour */}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', maxWidth: '800px', margin: '0 auto' }}>
                <button
                    onClick={backToSets}
                    style={{ padding: '10px 20px', backgroundColor: '#95a5a6', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer', fontWeight: 'bold' }}
                >
                    ⬅ Retour
                </button>
                <h2 style={{ margin: 0 }}>Classeur : {selectedSet.name}</h2>
                <div style={{ width: '100px' }}></div> {/* Élément invisible pour garder le titre centré */}
            </div>

            {/* Jauge de progression */}
            <div style={{ margin: '30px auto', width: '50%', maxWidth: '400px' }}>
                <p style={{ color: '#bdc3c7', marginBottom: '5px', fontWeight: 'bold' }}>
                    Complétion : {cardsOwnedCount} / {pokedex.length} ({completionRatio}%)
                </p>
                <div style={{ width: '100%', height: '15px', backgroundColor: '#2c3e50', borderRadius: '10px', overflow: 'hidden' }}>
                    <div style={{ width: `${completionRatio}%`, height: '100%', backgroundColor: '#f1c40f', transition: 'width 0.5s' }}></div>
                </div>
            </div>

            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '20px', justifyContent: 'center', marginTop: '30px' }}>
                {pokedex.map((card, index) => (
                    <div key={index} style={{
                        width: '160px',
                        height: '224px',
                        backgroundColor: card.isOwned ? '#2c3e50' : '#1a2026',
                        border: card.isOwned ? 'none' : '2px dashed #34495e',
                        borderRadius: '10px',
                        position: 'relative',
                        boxShadow: card.isOwned ? '0 4px 8px rgba(0,0,0,0.5)' : 'none',
                        overflow: 'hidden',
                        display: 'flex',
                        justifyContent: 'center',
                        alignItems: 'center',
                        opacity: card.isOwned ? 1 : 0.6,
                        transition: 'transform 0.2s'
                    }}
                         onMouseEnter={(e) => { if(card.isOwned) e.currentTarget.style.transform = 'scale(1.05)' }}
                         onMouseLeave={(e) => { if(card.isOwned) e.currentTarget.style.transform = 'scale(1)' }}
                    >
                        {card.isOwned ? (
                            <>
                                <img
                                    src={card.imageUrl}
                                    alt={`Carte ${card.cardNumber}`}
                                    style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                                />
                                <div style={{
                                    position: 'absolute', bottom: '5px', right: '5px',
                                    backgroundColor: '#e74c3c', color: 'white', borderRadius: '50%',
                                    width: '30px', height: '30px', display: 'flex',
                                    justifyContent: 'center', alignItems: 'center',
                                    fontWeight: 'bold', border: '2px solid white'
                                }}>
                                    x{card.quantity}
                                </div>
                            </>
                        ) : (
                            <span style={{ fontSize: '20px', fontWeight: 'bold', color: '#465666', textAlign: 'center', padding: '10px' }}>
                {card.cardNumber}
              </span>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
}

export default Inventory;