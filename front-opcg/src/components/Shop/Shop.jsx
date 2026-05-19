import { useState } from 'react';
import api from '../../api/axiosConfig';
import styles from './Shop.module.css';

const availableSets = [
    { id: 'OP-13', name: 'Carrying on his Will', image: '/op-13.png' },
    { id: 'OP-12', name: 'Legacy of the Master', image: '/op-12.png' },
    { id: 'OP-11', name: 'A Fist of Divine Speed', image: '/op-11.png' },
    { id: 'OP-10', name: 'Royal Blood', image: '/op-10.png' },
    { id: 'OP-09', name: 'The New Emperor', image: '/op-09.png' },
    { id: 'OP-08', name: 'Two Legends', image: '/op-08.png' },
    { id: 'OP-07', name: '500 Years in the Future', image: '/op-07.png' },
    { id: 'OP-06', name: 'Wings of the Captain', image: '/op-06.png' },
    { id: 'OP-05', name: 'Awakening of the New Era', image: '/op-05.png' },
    { id: 'OP-04', name: 'Kingdoms of Intrigue', image: '/op-04.png' },
    { id: 'OP-03', name: 'Pillars of Strength', image: '/op-03.png' },
    { id: 'OP-02', name: 'Paramount War', image: '/op-02.png' },
    { id: 'OP-01', name: 'Romance Dawn', image: '/op-01.png' }
];

function Shop({ coins, setCoins }) {
    const [selectedFilter, setSelectedFilter] = useState('all');
    const [loading, setLoading] = useState(false);
    const [pulledCards, setPulledCards] = useState(null);
    const [error, setError] = useState('');
    const [currentCardIndex, setCurrentCardIndex] = useState(0);

    const BOOSTER_PRICE = 500;

    const handleOpenBooster = async (setId) => {
        if (coins < BOOSTER_PRICE) {
            setError("Tu n'as pas assez de pièces !");
            return;
        }

        setLoading(true);
        setError('');
        const userId = localStorage.getItem('user_id');

        try {
            const response = await api.get(`/boosters/open/${setId}/${userId}`);

            const imagePromises = response.data.map((card) => {
                return new Promise((resolve) => {
                    const image = new Image();
                    image.src = card.imageUrl;
                    image.onload = resolve;
                    image.onerror = resolve;
                })
            })

            await Promise.all(imagePromises)

            setCoins(prevCoins => prevCoins - BOOSTER_PRICE);

            setPulledCards(response.data);
            setCurrentCardIndex(0);
        } catch (err) {
            setError(err.response?.data?.message || "Erreur lors de l'ouverture du booster.");
        } finally {
            setLoading(false);
        }
    };

    const handleBackToShop = () => {
        setPulledCards(null);
        setCurrentCardIndex(0);
    };

    if (pulledCards) {
        const isRevealing = currentCardIndex < pulledCards.length;

        if (isRevealing) {
            const currentCard = pulledCards[currentCardIndex];

            return (
                <div
                    className={styles.revealContainer}
                    onClick={() => setCurrentCardIndex(prev => prev + 1)}
                >
                    <h2 className={styles.title}>Carte {currentCardIndex + 1} / {pulledCards.length}</h2>
                    <p className={styles.subtitle}>(Clique n'importe où pour voir la suivante)</p>

                    <img
                        key={currentCardIndex}
                        src={currentCard.imageUrl}
                        alt={currentCard.card.name}
                        className={styles.singlePulledCard}
                    />
                </div>
            );
        } else {
            return (
                <div className={styles.container}>
                    <h2 className={styles.title}>Félicitations !</h2>
                    <p className={styles.subtitle}>Voici le résumé de ton booster.</p>

                    <div className={styles.pulledGrid}>
                        {pulledCards.map((variant, index) => (
                            <img
                                key={index}
                                src={variant.imageUrl}
                                alt={variant.card.name}
                                className={styles.pulledCard}
                            />
                        ))}
                    </div>

                    <button onClick={handleBackToShop} className={styles.backButton}>
                        ⬅ Retour à la boutique
                    </button>
                </div>
            );
        }
    }

    const displayedSets = selectedFilter === 'all'
        ? availableSets.slice(0, 3)
        : availableSets.filter(set => set.id === selectedFilter);

    return (
        <div className={styles.container}>
            <h1 className={styles.title}>Boutique de Boosters</h1>
            <p className={styles.subtitle}>Achète des boosters pour compléter ta collection !</p>

            {error && <p className={styles.error}>{error}</p>}

            {/* Le sélecteur de Boosters */}
            <div className={styles.controls}>
                <select
                    value={selectedFilter}
                    onChange={(e) => setSelectedFilter(e.target.value)}
                    className={styles.selectBox}
                >
                    <option value="all">Dernières Sorties (Nouveautés)</option>
                    <optgroup label="Toutes les extensions">
                        {availableSets.map(set => (
                            <option key={set.id} value={set.id}>
                                {set.id} - {set.name}
                            </option>
                        ))}
                    </optgroup>
                </select>
            </div>

            {/* La liste des Boosters */}
            <div className={styles.boosterGrid}>
                {displayedSets.map(set => (
                    <div key={set.id} className={styles.boosterCard}>
                        <img src={set.image} alt={set.name} className={styles.boosterImage} />
                        <h3 className={styles.boosterName}>{set.name}</h3>
                        <p className={styles.boosterId}>{set.id}</p>

                        <button
                            onClick={() => handleOpenBooster(set.id)}
                            disabled={loading || coins < BOOSTER_PRICE}
                            className={styles.buyButton}
                        >
                            {loading ? 'X' : `Ouvrir ($ 500)`}
                        </button>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default Shop;