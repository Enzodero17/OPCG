import Login from "./components/Login.jsx";
import BoosterOpener from "./components/BoosterOpener.jsx";

function App() {
  return (
      <div>
        <h1>Bienvenue sur OPCG ! 🏴‍☠️</h1>
        <Login />
          <hr style={{ margin: '40px auto', width: '80%', borderColor: '#333' }} />
          <BoosterOpener />
      </div>
  )
}

export default App