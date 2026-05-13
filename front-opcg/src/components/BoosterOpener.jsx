import { useState } from 'react';
import api from '../api/axiosConfig';

const availableBoosters = [
    {
        id: 'OP-01',
        name: 'Romance Dawn',
        price: 500,
        image: '/op-01.png'
    }
];

function BoosterOpener({ setCoins }) {
    const [view, setView] = useState('shop');
    const [selectedBooster, setSelectedBooster] = useState(null);
    const [showConfirm, setShowConfirm] = useState(false);

    const [cards, setCards] = useState([]);
    const [flippedCards, setFlippedCards] = useState([]);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState('');

    // Clic sur un booster
    const handleBoosterClick = (booster) => {
        setSelectedBooster(booster);
        setShowConfirm(true);
        setMessage('');
    };

    // Confirmation d'achat
    const confirmPurchase = async () => {
        const userId = localStorage.getItem('user_id');
        if (!userId) {
            setMessage("Erreur : Tu dois te connecter d'abord !");
            setShowConfirm(false);
            return;
        }

        setLoading(true);

        try {
            const response = await api.get(`/boosters/open/${selectedBooster.id}/${userId}`);

            // On débite l'argent
            setCoins((prevCoins) => {
                const newBalance = prevCoins - selectedBooster.price;
                localStorage.setItem('coins', newBalance);
                return newBalance;
            });

            // On prépare les cartes reçues
            setCards(response.data);
            setFlippedCards(new Array(response.data.length).fill(false));

            // On change d'écran
            setShowConfirm(false);
            setView('opening');

        } catch (error) {
            setMessage("Erreur : Pas assez de pièces ou problème serveur.");
            setShowConfirm(false);
        } finally {
            setLoading(false);
        }
    };

    // Retourner une carte
    const flipCard = (index) => {
        setFlippedCards((prev) => {
            const newFlipped = [...prev];
            newFlipped[index] = true;
            return newFlipped;
        });
    };

    // Tout retourner d'un coup
    const flipAll = () => {
        setFlippedCards(new Array(cards.length).fill(true));
    };

    // Revenir à la boutique
    const backToShop = () => {
        setView('shop');
        setCards([]);
        setSelectedBooster(null);
    };

    return (
        <div style={{ padding: '20px', textAlign: 'center', minHeight: '600px', position: 'relative' }}>

            {view === 'shop' && (
                <>
                    <h2>Boutique de Boosters</h2>
                    {message && <p style={{ color: '#e74c3c', fontWeight: 'bold' }}>{message}</p>}

                    <div style={{ display: 'flex', justifyContent: 'center', gap: '30px', marginTop: '40px' }}>
                        {availableBoosters.map((booster) => (
                            <div
                                key={booster.id}
                                onClick={() => handleBoosterClick(booster)}
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
                                <img src={booster.image} alt={booster.name} style={{ width: '200px', borderRadius: '10px' }} />
                                <h3 style={{ margin: '15px 0 5px 0', color: '#ecf0f1' }}>{booster.name}</h3>
                                <p style={{ margin: 0, color: '#f1c40f', fontSize: '18px', fontWeight: 'bold' }}>🪙 {booster.price}</p>
                            </div>
                        ))}
                    </div>
                </>
            )}

            {showConfirm && selectedBooster && (
                <div style={{
                    position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
                    backgroundColor: 'rgba(0,0,0,0.8)', display: 'flex',
                    justifyContent: 'center', alignItems: 'center', zIndex: 1000
                }}>
                    <div style={{
                        backgroundColor: '#34495e', padding: '40px', borderRadius: '15px',
                        boxShadow: '0 10px 30px rgba(0,0,0,0.5)', maxWidth: '400px'
                    }}>
                        <h3>Confirmer l'achat</h3>
                        <p>Veux-tu vraiment ouvrir un booster <strong>{selectedBooster.name} ({selectedBooster.id})</strong> pour <strong style={{ color: '#f1c40f' }}>{selectedBooster.price} pièces</strong> ?</p>

                        <div style={{ display: 'flex', gap: '20px', justifyContent: 'center', marginTop: '30px' }}>
                            <button
                                onClick={() => setShowConfirm(false)}
                                disabled={loading}
                                style={{ padding: '10px 20px', borderRadius: '5px', border: 'none', backgroundColor: '#95a5a6', color: 'white', cursor: 'pointer', fontWeight: 'bold' }}
                            >
                                Annuler
                            </button>
                            <button
                                onClick={confirmPurchase}
                                disabled={loading}
                                style={{ padding: '10px 20px', borderRadius: '5px', border: 'none', backgroundColor: '#27ae60', color: 'white', cursor: 'pointer', fontWeight: 'bold' }}
                            >
                                {loading ? 'Ouverture...' : 'Oui, ouvrir !'}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {view === 'opening' && (
                <>
                    <h2>Ouverture du Booster OP-01 🎉</h2>

                    <div style={{ margin: '20px 0', display: 'flex', gap: '20px', justifyContent: 'center' }}>
                        <button onClick={flipAll} style={{ padding: '10px 20px', backgroundColor: '#3498db', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer', fontWeight: 'bold' }}>
                            Tout retourner
                        </button>
                        <button onClick={backToShop} style={{ padding: '10px 20px', backgroundColor: '#e74c3c', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer', fontWeight: 'bold' }}>
                            Terminer et quitter
                        </button>
                    </div>

                    <div style={{ display: 'flex', flexWrap: 'wrap', gap: '20px', justifyContent: 'center', marginTop: '40px' }}>
                        {cards.map((variant, index) => {
                            const isFlipped = flippedCards[index];

                            return (
                                <div
                                    key={index}
                                    onClick={() => flipCard(index)}
                                    style={{
                                        width: '160px',
                                        height: '224px',
                                        cursor: isFlipped ? 'default' : 'pointer',
                                        perspective: '1000px'
                                    }}
                                >
                                    {/* Conteneur interne qui va tourner sur lui-même */}
                                    <div style={{
                                        width: '100%',
                                        height: '100%',
                                        position: 'relative',
                                        transition: 'transform 0.6s cubic-bezier(0.4, 0.2, 0.2, 1)',
                                        transformStyle: 'preserve-3d',
                                        transform: isFlipped ? 'rotateY(180deg)' : 'rotateY(0deg)'
                                    }}>

                                        {/* Le dos de la carte */}
                                        <div style={{
                                            position: 'absolute', width: '100%', height: '100%',
                                            backfaceVisibility: 'hidden', borderRadius: '10px',
                                            backgroundColor: '#1a252f', border: '3px solid #f1c40f',
                                            display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center',
                                            boxShadow: '0 6px 12px rgba(0,0,0,0.4)',
                                            backgroundImage: 'radial-gradient(#34495e 20%, transparent 20%), radial-gradient(#34495e 20%, transparent 20%)',
                                            backgroundSize: '10px 10px', backgroundPosition: '0 0, 5px 5px'
                                        }}>
                                            <div style={{ width: '60px', height: '60px', borderRadius: '50%', border: '3px solid #f1c40f', display: 'flex', justifyContent: 'center', alignItems: 'center', backgroundColor: '#2c3e50' }}>
                                                <span style={{ fontSize: '30px' }}>⚓</span>
                                            </div>
                                            <p style={{ color: '#f1c40f', fontWeight: 'bold', marginTop: '10px', letterSpacing: '2px' }}>OPCG</p>
                                        </div>

                                        {/* L'avant de la carte */}
                                        <div style={{
                                            position: 'absolute', width: '100%', height: '100%',
                                            backfaceVisibility: 'hidden', borderRadius: '10px',
                                            transform: 'rotateY(180deg)',
                                            overflow: 'hidden',
                                            boxShadow: '0 6px 12px rgba(0,0,0,0.4)',
                                            backgroundColor: '#2c3e50',
                                            display: 'flex', justifyContent: 'center', alignItems: 'center'
                                        }}>
                                            {variant.card?.imageUrl || variant.imageUrl ? (
                                                <img
                                                    src={variant.card?.imageUrl || variant.imageUrl}
                                                    alt="Carte dévoilée"
                                                    style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                                                />
                                            ) : (
                                                <div style={{ textAlign: 'center', padding: '10px' }}>
                                                    <span style={{ fontSize: '30px' }}>🃏</span>
                                                    <p style={{ fontSize: '12px', marginTop: '10px', fontWeight: 'bold' }}>{variant.card?.name}</p>
                                                    <p style={{ fontSize: '11px', color: '#f1c40f' }}>{variant.rarity}</p>
                                                </div>
                                            )}
                                        </div>

                                    </div>
                                </div>
                            );
                        })}
                    </div>
                </>
            )}
        </div>
    );
}

export default BoosterOpener;