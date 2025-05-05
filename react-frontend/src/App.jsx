import { useState, useRef, useEffect } from 'react'
import ReactMarkdown from 'react-markdown'
import './App.css'

function App() {
  const [query, setQuery] = useState('')
  const responseRef = useRef('')
  const [version, setVersion] = useState(0)
  const [loading, setLoading] = useState(false)
  const scrollRef = useRef(null)
  const eventSourceRef = useRef(null)

  const handleStreaming = () => {
    if (!query.trim()) return

    setLoading(true)
    responseRef.current = ''
    setVersion(0)

    const url = `http://localhost:8080/chat/stream?query=${encodeURIComponent(query)}`
    const eventSource = new EventSource(url)
    eventSourceRef.current = eventSource

    eventSource.onmessage = (event) => {
      console.log('Received:', JSON.stringify(event.data))
      responseRef.current += event.data
      setVersion(v => v + 1)
    }

    eventSource.onerror = (err) => {
      console.error('Stream error:', err)
      eventSource.close()
      setLoading(false)
    }
  }

  // Cleanup EventSource on unmount or reload
  useEffect(() => {
    return () => {
      if (eventSourceRef.current) {
        eventSourceRef.current.close()
      }
    }
  }, [])

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight
    }
  }, [version])

  return (
    <div className="container">
      <h1>Confluence Wiki</h1>

      <div className="response" ref={scrollRef}>
        {loading ? <em>Loading...</em> : (
          <div className="markdown preserve-whitespace">
            <ReactMarkdown>{responseRef.current}</ReactMarkdown>
          </div>
        )}
      </div>

      <textarea
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="Ask your question..."
      />

      <div className="buttons">
        <button onClick={handleStreaming} disabled={loading}>Send</button>
      </div>
    </div>
  )
}

export default App
