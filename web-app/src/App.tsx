import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'

function App() {
  return (
    <Router>
      <div className="min-h-screen bg-background">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </div>
    </Router>
  )
}

function HomePage() {
  return (
    <div className="flex flex-col items-center justify-center min-h-screen p-4">
      <div className="card max-w-md w-full text-center">
        <h1 className="text-4xl font-bold text-primary mb-4">Calm Burst</h1>
        <p className="text-text-muted mb-6">Find your calm, one breath at a time</p>

        <div className="space-y-4">
          <button className="btn-primary w-full">
            Start Breathing Exercise
          </button>
          <button className="btn-secondary w-full">
            View Settings
          </button>
        </div>

        <div className="mt-8 p-4 bg-background rounded-lg">
          <p className="text-sm text-text-light">
            This is a placeholder home page. Routing is ready for additional pages.
          </p>
        </div>
      </div>
    </div>
  )
}

export default App
