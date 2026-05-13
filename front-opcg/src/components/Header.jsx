function Header({ username, coins, onLogout }) {
    return (
        <div style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            backgroundColor: '#2c3e50',
            padding: '15px 30px',
            borderBottom: '3px solid #f1c40f',
            boxShadow: '0 4px 6px rgba(0,0,0,0.2)'
        }}>
            <h1 style={{ margin: 0, fontSize: '24px', color: '#ecf0f1' }}>🏴‍☠️ OPCG</h1>

            {username ? (
                <div style={{ display: 'flex', alignItems: 'center', gap: '20px', fontSize: '18px', fontWeight: 'bold' }}>
                    <span style={{ color: '#ecf0f1' }}>👤 {username}</span>
                    <span style={{ color: '#f1c40f' }}>🪙 {coins}</span>

                    {/* LE NOUVEAU BOUTON ! */}
                    <button
                        onClick={onLogout}
                        style={{
                            padding: '8px 15px',
                            backgroundColor: '#e74c3c',
                            color: 'white',
                            border: 'none',
                            borderRadius: '5px',
                            cursor: 'pointer',
                            fontWeight: 'bold'
                        }}
                    >
                        Déconnexion
                    </button>
                </div>
            ) : (
                <div style={{ fontSize: '18px', color: '#bdc3c7' }}>Non connecté</div>
            )}
        </div>
    );
}

export default Header;