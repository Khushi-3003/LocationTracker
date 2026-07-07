import React from 'react';
import './Footer.css';

export default function Footer() {
  const year = new Date().getFullYear();
  const scrollTop = () => window.scrollTo({ top: 0, behavior: 'smooth' });

  return (
    <footer className="footer">
      <div className="footer-content container">
        <div className="footer-brand">
          <span className="footer-logo-bracket">&lt;</span>
          <span className="footer-logo-name">Manthan</span>
          <span className="footer-logo-bracket">/&gt;</span>
        </div>
        <p className="footer-text">
          MCA Student · Full Stack Developer · BMSIT, Bangalore
        </p>
        <p className="footer-copy">
          © {year} Manthan. Built with <span className="heart">❤️</span> using React.
        </p>
      </div>
      <button className="scroll-top-btn" onClick={scrollTop} aria-label="Scroll to top">
        ↑
      </button>
    </footer>
  );
}
