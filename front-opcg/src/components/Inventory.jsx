import { useState, useEffect } from 'react';
import api from '../api/axiosConfig';
import CardModal from './CardModal';

const availableSets = [
    { id: 'OP-01', name: 'Romance Dawn', image: '/op-01.png' },
    { id: 'OP-02', name: 'Paramount War', image: '/op-02.png' },
    { id: 'OP-03', name: 'Pillars of Strength', image: '/op-03.png' },
    { id: 'OP-04', name: 'Kingdoms of Intrigue', image: '/op-04.png' },
    { id: 'OP-05', name: 'Awakening of the New Era', image: '/op-05.png' },
    { id: 'OP-06', name: 'Wings of the Captain', image: '/op-06.png' },
    { id: 'OP-07', name: '500 Years in the Future', image: '/op-07.png' },
    { id: 'OP-08', name: 'Two Legends', image: '/op-08.png' },
    { id: 'OP-09', name: 'The New Emperor', image: '/op-09.png' },
    { id: 'OP-10', name: 'Royal Blood', image: '/op-10.png' },
    { id: 'OP-11', name: 'Set OP-11', image: '/op-11.png' },
    { id: 'OP-12', name: 'Set OP-12', image: '/op-12.png' },
    { id: 'OP-13', name: 'Set OP-13', image: '/op-13.png' }
];

function Inventory({ setCoins }) {
    const [selectedSet, setSelectedSet] = useState(null);
    const [pokedex, setPokedex] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [selectedCard, setSelectedCard] = useState(null);
    const [isSellMode, setIsSellMode] = useState(false);
    const [sellCart, setSellCart] = useState({});
    const [sellMessage, setSellMessage] = useState('');

    useEffect(() => {
        if (selectedSet) {
            fetchPokedexData(selectedSet.id);
            setIsSellMode(false);
            setSellCart({});
            setSellMessage('');
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
                    quantity: item.quantity || 0,
                    fullCard: variantData?.card,
                    fullVariant: variantData
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

    const toggleSellMode = () => {
        setIsSellMode(!isSellMode);
        setSellCart({});
        setSellMessage('');
    }

    // On met toutes les cartes a un seul exemplaire
    const selectAllDuplicates = () => {
        const newCart = {};
        pokedex.forEach(item => {
            if (item.isOwned && item.quantity > 1) {
                newCart[item.fullVariant.id] = item.quantity - 1
            }
        })
        setSellCart(newCart);
    }

    const handleCardClick = (card) => {
        // Si on n'est pas en mode vente, on ouvre le Modal classique
        if (!isSellMode) {
            if (card.isOwned) setSelectedCard(card);
            return;
        }

        // Si on est en mode vente
        if (!card.isOwned || card.quantity <= 1) {
            return;
        }

        const variantId = card.fullVariant.id;
        const currentSellQty = sellCart[variantId] || 0;
        const maxSellable = card.quantity - 1;

        setSellCart(prev => {
            const newCart = { ...prev };
            if (currentSellQty < maxSellable) {
                // On ajoute 1 à la sélection
                newCart[variantId] = currentSellQty + 1;
            } else {
                // Si on a atteint le max et qu'on reclique, on annule la sélection de cette carte
                delete newCart[variantId];
            }
            return newCart;
        });
    };

    const confirmSell = async () => {
        const userId = localStorage.getItem('user_id');

        // On transforme le dictionnaire en liste
        const itemsToSell = Object.keys(sellCart).map(variantId => ({
            variantId: variantId,
            quantityToSell: sellCart[variantId]
        }));

        if (itemsToSell.length === 0) {
            return;
        }

        setLoading(true);
        try {
            const response = await api.post(`/collection/sell/${userId}`, itemsToSell);

            if (setCoins) {
                setCoins(response.data.newBalance);
            }
            localStorage.setItem('coins', response.data.newBalance);

            setSellMessage(`${response.data.message}`);
            setIsSellMode(false);
            setSellCart({});

            // On recharge le classeur
            fetchPokedexData(selectedSet.id);
        } catch (err) {
            setSellMessage("Erreur lors de la vente. " + (err.response?.data || ''));
            setLoading(false);
        }
    };

    if (!selectedSet) {
        return (
            <div style={{ padding: '20px', textAlign: 'center', minHeight: '600px' }}>
                <h2>Choisis une extension à consulter</h2>

                <div style={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'center', gap: '30px', marginTop: '40px' }}>                    {availableSets.map((set) => (
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
    const totalSelectedToSell = Object.values(sellCart).reduce((a, b) => a + b, 0);

    return (
        <div style={{ padding: '20px', textAlign: 'center', paddingBottom: isSellMode ? '100px' : '20px' }}>

            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', maxWidth: '800px', margin: '0 auto' }}>
                <button onClick={backToSets} style={{ padding: '10px 20px', backgroundColor: '#95a5a6', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer', fontWeight: 'bold' }}>
                    ⬅ Retour
                </button>
                <h2 style={{ margin: 0 }}>Classeur : {selectedSet.name}</h2>

                {/* LE BOUTON TOGGLE MODE VENTE */}
                <button
                    onClick={toggleSellMode}
                    style={{ padding: '10px 20px', backgroundColor: isSellMode ? '#e74c3c' : '#f39c12', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer', fontWeight: 'bold' }}
                >
                    {isSellMode ? '❌ Annuler la Vente' : '💰 Mode Vente'}
                </button>
            </div>

            {sellMessage && <p style={{ backgroundColor: '#27ae60', color: 'white', padding: '10px', borderRadius: '5px', display: 'inline-block', marginTop: '20px' }}>{sellMessage}</p>}

            <div style={{ margin: '30px auto', width: '50%', maxWidth: '400px' }}>
                <p style={{ color: '#bdc3c7', marginBottom: '5px', fontWeight: 'bold' }}>
                    Complétion : {cardsOwnedCount} / {pokedex.length} ({completionRatio}%)
                </p>
                <div style={{ width: '100%', height: '15px', backgroundColor: '#2c3e50', borderRadius: '10px', overflow: 'hidden' }}>
                    <div style={{ width: `${completionRatio}%`, height: '100%', backgroundColor: '#f1c40f', transition: 'width 0.5s' }}></div>
                </div>
            </div>

            {isSellMode && (
                <button onClick={selectAllDuplicates} style={{ marginBottom: '20px', padding: '10px 20px', backgroundColor: '#3498db', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer', fontWeight: 'bold' }}>
                    ✨ Sélectionner tous les doublons
                </button>
            )}

            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '20px', justifyContent: 'center', marginTop: '10px' }}>
                {pokedex.map((card, index) => {

                    const variantId = card.fullVariant?.id;
                    const sellQty = sellCart[variantId] || 0;
                    const isSelected = sellQty > 0;
                    const maxSellable = card.isOwned ? card.quantity - 1 : 0;

                    const cannotSell = isSellMode && maxSellable === 0;

                    return (
                        <div key={index}
                             onClick={() => handleCardClick(card)}
                             style={{
                                 width: '160px',
                                 height: '224px',
                                 backgroundColor: card.isOwned ? '#2c3e50' : '#1a2026',
                                 border: isSelected ? '4px solid #e74c3c' : (card.isOwned ? 'none' : '2px dashed #34495e'),
                                 borderRadius: '10px',
                                 position: 'relative',
                                 boxShadow: card.isOwned ? '0 4px 8px rgba(0,0,0,0.5)' : 'none',
                                 overflow: 'hidden',
                                 display: 'flex',
                                 justifyContent: 'center',
                                 alignItems: 'center',
                                 opacity: cannotSell ? 0.3 : (card.isOwned ? 1 : 0.6),
                                 transform: isSelected ? 'scale(0.95)' : 'scale(1)',
                                 cursor: cannotSell ? 'not-allowed' : 'pointer',
                                 transition: 'all 0.2s'
                             }}
                        >
                            {card.isOwned ? (
                                <>
                                    <img src={card.imageUrl} alt={`Carte ${card.cardNumber}`} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />

                                    <div style={{ position: 'absolute', bottom: '5px', right: '5px', backgroundColor: '#34495e', color: 'white', borderRadius: '50%', width: '30px', height: '30px', display: 'flex', justifyContent: 'center', alignItems: 'center', fontWeight: 'bold', border: '2px solid white' }}>
                                        x{card.quantity}
                                    </div>

                                    {isSelected && (
                                        <div style={{ position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)', backgroundColor: 'rgba(231, 76, 60, 0.95)', color: 'white', padding: '10px 20px', borderRadius: '20px', fontSize: '24px', fontWeight: 'bold', border: '2px solid white' }}>
                                            -{sellQty}
                                        </div>
                                    )}
                                </>
                            ) : (
                                <span style={{ fontSize: '20px', fontWeight: 'bold', color: '#465666' }}>{card.cardNumber}</span>
                            )}
                        </div>
                    );
                })}
            </div>

            {isSellMode && (
                <div style={{ position: 'fixed', bottom: 0, left: 0, right: 0, backgroundColor: '#2c3e50', padding: '20px', borderTop: '4px solid #e74c3c', display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '30px', zIndex: 1000, boxShadow: '0 -5px 15px rgba(0,0,0,0.5)' }}>
                    <div style={{ color: 'white', fontSize: '18px' }}>
                        Cartes à vendre : <strong style={{ color: '#f1c40f', fontSize: '24px' }}>{totalSelectedToSell}</strong>
                    </div>
                    <button
                        onClick={confirmSell}
                        disabled={totalSelectedToSell === 0}
                        style={{ padding: '15px 30px', backgroundColor: totalSelectedToSell === 0 ? '#7f8c8d' : '#27ae60', color: 'white', border: 'none', borderRadius: '5px', fontWeight: 'bold', fontSize: '18px', cursor: totalSelectedToSell === 0 ? 'not-allowed' : 'pointer', transition: 'background-color 0.2s' }}
                    >
                        CONFIRMER LA VENTE
                    </button>
                </div>
            )}

            {selectedCard && !isSellMode && (
                <CardModal
                    card={selectedCard.fullCard}
                    variant={selectedCard.fullVariant}
                    onClose={() => setSelectedCard(null)}
                />
            )}
        </div>
    );
}

export default Inventory;