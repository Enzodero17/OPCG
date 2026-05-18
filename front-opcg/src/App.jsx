import Login from "./components/Login.jsx";
import BoosterOpener from "./components/BoosterOpener.jsx";
import {useState} from "react";
import Header from "./components/Header.jsx";
import Inventory from "./components/Inventory.jsx";
import Missions from "./components/Mission.jsx";
import Profile from "./components/Profile.jsx";

function App() {

    const[username, setUsername] = useState(localStorage.getItem('username') || '');
    const[coins, setCoins] = useState(localStorage.getItem('coins') || '');
    const [currentView, setCurrentView] = useState('shop');

    // Quand on se déconnecte
    const handleLogout = () => {
        localStorage.clear();
        setUsername("");
        setCoins(0);
        setCurrentView("shop");
    }

    return (
        <div>
            <Header username={username} coins={coins} onLogout={handleLogout} />

            { !username && <Login setUsername={setUsername} setCoins={setCoins}/> }

            {username && (
                <>
                    <div style={{ display: 'flex', justifyContent: 'center', gap: '20px', margin: '30px 0' }}>
                        <button
                            onClick={() => setCurrentView('shop')}
                            style={{ padding: '10px 20px', cursor: 'pointer', fontWeight: 'bold', borderRadius: '5px', border: 'none', backgroundColor: currentView === 'shop' ? '#f1c40f' : '#95a5a6', color: '#1a1a1a' }}
                        >
                            🏪 Boutique
                        </button>
                        <button
                            onClick={() => setCurrentView('inventory')}
                            style={{ padding: '10px 20px', cursor: 'pointer', fontWeight: 'bold', borderRadius: '5px', border: 'none', backgroundColor: currentView === 'inventory' ? '#f1c40f' : '#95a5a6', color: '#1a1a1a' }}
                        >
                            🎒 Mon Inventaire
                        </button>
                        <button
                            onClick={() => setCurrentView('missions')}
                            style={{ padding: '10px 20px', cursor: 'pointer', fontWeight: 'bold', borderRadius: '5px', border: 'none', backgroundColor: currentView === 'missions' ? '#f1c40f' : '#95a5a6', color: '#1a1a1a' }}
                        >
                            Missions
                        </button>
                        <button
                            onClick={() => setCurrentView('profile')}
                            style={{ padding: '10px 20px', cursor: 'pointer', fontWeight: 'bold', borderRadius: '5px', border: 'none', backgroundColor: currentView === 'profile' ? '#f1c40f' : '#95a5a6', color: '#1a1a1a' }}
                        >
                            Mon Profil
                        </button>
                    </div>
                    {currentView === 'shop' && <BoosterOpener setCoins={setCoins} />}
                    {currentView === 'inventory' && <Inventory setCoins={setCoins} />}
                    {currentView === 'missions' && <Missions setCoins={setCoins} />}
                    {currentView === 'profile' && <Profile />}
                </>
            )}
        </div>
    )
}

export default App