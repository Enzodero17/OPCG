import React from 'react';

function CardModal({ card, variant, onClose }) {
    if (!card) return null;

    return (
        <div style={{
            position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
            backgroundColor: 'rgba(0,0,0,0.9)', display: 'flex',
            justifyContent: 'center', alignItems: 'center', zIndex: 2000,
            padding: '20px'
        }} onClick={onClose}>

            <div style={{
                backgroundColor: '#1c2630', color: 'white', borderRadius: '15px',
                display: 'flex', maxWidth: '900px', width: '100%', maxHeight: '90vh',
                overflow: 'hidden', border: '2px solid #f1c40f', position: 'relative'
            }} onClick={(e) => e.stopPropagation()}>

                {/* Bouton de fermeture */}
                <button onClick={onClose} style={{
                    position: 'absolute', top: '15px', right: '15px', background: 'none',
                    border: 'none', color: '#bdc3c7', fontSize: '24px', cursor: 'pointer'
                }}>✖</button>

                {/* GAUCHE : L'IMAGE */}
                <div style={{ flex: '1', backgroundColor: '#000', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '20px' }}>
                    <img src={variant?.imageUrl} alt={card.name} style={{ maxHeight: '100%', maxWidth: '100%', borderRadius: '10px', boxShadow: '0 0 20px rgba(0,0,0,0.5)' }} />
                </div>

                {/* DROITE : LES INFOS (Stats de ta base Java) */}
                <div style={{ flex: '1.2', padding: '40px', overflowY: 'auto', textAlign: 'left' }}>
                    <h2 style={{ color: '#f1c40f', marginBottom: '5px', fontSize: '28px' }}>{card.name}</h2>
                    <p style={{ color: '#bdc3c7', fontStyle: 'italic', marginBottom: '20px' }}>{card.id} • {card.rarity}</p>

                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px', marginBottom: '25px' }}>
                        <div style={{ backgroundColor: '#2c3e50', padding: '10px', borderRadius: '5px' }}>
                            <span style={{ color: '#f1c40f', display: 'block', fontSize: '12px' }}>COÛT / VIE</span>
                            <span style={{ fontSize: '18px', fontWeight: 'bold' }}>{card.cost !== null ? card.cost : card.life}</span>
                        </div>
                        <div style={{ backgroundColor: '#2c3e50', padding: '10px', borderRadius: '5px' }}>
                            <span style={{ color: '#f1c40f', display: 'block', fontSize: '12px' }}>PUISSANCE</span>
                            <span style={{ fontSize: '18px', fontWeight: 'bold' }}>{card.power || '0'}</span>
                        </div>
                        <div style={{ backgroundColor: '#2c3e50', padding: '10px', borderRadius: '5px' }}>
                            <span style={{ color: '#f1c40f', display: 'block', fontSize: '12px' }}>COULEUR</span>
                            <span style={{ fontSize: '16px' }}>{card.color}</span>
                        </div>
                        <div style={{ backgroundColor: '#2c3e50', padding: '10px', borderRadius: '5px' }}>
                            <span style={{ color: '#f1c40f', display: 'block', fontSize: '12px' }}>ATTRIBUT</span>
                            <span style={{ fontSize: '16px' }}>{card.attribute || '-'}</span>
                        </div>
                    </div>

                    <div style={{ marginBottom: '20px' }}>
                        <span style={{ color: '#f1c40f', display: 'block', fontSize: '12px', marginBottom: '5px' }}>TYPES</span>
                        <p style={{ fontSize: '14px' }}>{card.subTypes || '-'}</p>
                    </div>

                    <div style={{ borderTop: '1px solid #34495e', paddingTop: '20px' }}>
                        <span style={{ color: '#f1c40f', display: 'block', fontSize: '12px', marginBottom: '10px' }}>EFFET</span>
                        <p style={{ fontSize: '15px', lineHeight: '1.6', color: '#ecf0f1' }}>{card.effectText || 'Aucun effet.'}</p>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default CardModal;