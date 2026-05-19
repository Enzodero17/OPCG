import styles from './Header.module.css';

function Header({ coins, setView, handleLogout }) {
    return (
        <header className={styles.header}>

            {/* Logo */}
            <div onClick={() => setView('shop')} className={styles.logoContainer}>
                <img
                    src="/logo.png"
                    alt="Logo OPCG"
                    className={styles.logoImg}
                />
            </div>

            <div className={styles.rightSection}>

                {/* Nombre de pièces */}
                <div className={styles.coinsBadge}>
                    $ {coins}
                </div>

                {/* Bouton Profil */}
                <img
                    src="/avatar.png"
                    alt="Mon Profil"
                    onClick={() => setView('profile')}
                    className={styles.profileImg}
                    title="Mon Profil"
                />

                {/* Bouton Déconnexion */}
                <button onClick={handleLogout} className={styles.logoutBtn}>
                    Déconnexion
                </button>

            </div>
        </header>
    );
}

export default Header;