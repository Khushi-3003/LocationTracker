import React, { useState, useEffect } from 'react';
import './Navbar.css';

const navLinks = [
  { id: 'home',      label: 'Home' },
  { id: 'about',     label: 'About' },
  { id: 'education', label: 'Education' },
  { id: 'skills',    label: 'Skills' },
  { id: 'projects',  label: 'Projects' },
  { id: 'contact',   label: 'Contact' },
];

export default function Navbar({ activeSection }) {
  const [scrolled, setScrolled] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 50);
    window.addEventListener('scroll', onScroll);
    return () => window.removeEventListener('scroll', onScroll);
  }, []);

  const handleNav = (id) => {
    document.getElementById(id)?.scrollIntoView({ behavior: 'smooth' });
    setMenuOpen(false);
  };

  return (
    <nav className={`navbar ${scrolled ? 'scrolled' : ''}`}>
      <div className="nav-container">
        <div className="nav-logo" onClick={() => handleNav('home')}>
          <span className="logo-bracket">&lt;</span>
          <span className="logo-name">Manthan</span>
          <span className="logo-bracket">/&gt;</span>
        </div>

        <ul className={`nav-links ${menuOpen ? 'open' : ''}`}>
          {navLinks.map(link => (
            <li key={link.id}>
              <button
                className={`nav-link ${activeSection === link.id ? 'active' : ''}`}
                onClick={() => handleNav(link.id)}
              >
                {link.label}
              </button>
            </li>
          ))}
        </ul>

        <button
          className={`hamburger ${menuOpen ? 'open' : ''}`}
          onClick={() => setMenuOpen(!menuOpen)}
          aria-label="Toggle menu"
        >
          <span /><span /><span />
        </button>
      </div>
    </nav>
  );
}
