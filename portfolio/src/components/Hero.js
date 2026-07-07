import React from 'react';
import './Hero.css';

export default function Hero() {
  const handleScroll = (id) => {
    document.getElementById(id)?.scrollIntoView({ behavior: 'smooth' });
  };

  return (
    <section id="home" className="hero">
      {/* Animated background orbs */}
      <div className="orb orb-1" />
      <div className="orb orb-2" />
      <div className="orb orb-3" />

      <div className="hero-content container">
        <div className="hero-text">
          <p className="hero-greeting">👋 Hello, I'm</p>
          <h1 className="hero-name">
            <span className="name-highlight">Manthan</span>
          </h1>
          <h2 className="hero-role">MCA Student &amp; Full Stack Developer</h2>
          <p className="hero-desc">
            A passionate developer from BMSIT Bangalore, building modern web applications
            and solving real-world problems through code.
          </p>
          <div className="hero-btns">
            <button className="btn btn-primary" onClick={() => handleScroll('projects')}>
              View Projects
            </button>
            <button className="btn btn-outline" onClick={() => handleScroll('contact')}>
              Contact Me
            </button>
          </div>

          <div className="hero-stats">
            <div className="stat">
              <span className="stat-num">3+</span>
              <span className="stat-label">Projects</span>
            </div>
            <div className="stat-divider" />
            <div className="stat">
              <span className="stat-num">5+</span>
              <span className="stat-label">Skills</span>
            </div>
            <div className="stat-divider" />
            <div className="stat">
              <span className="stat-num">3</span>
              <span className="stat-label">Degrees</span>
            </div>
          </div>
        </div>

        <div className="hero-avatar-wrap">
          <div className="avatar-ring" />
          <div className="avatar-circle">
            <span className="avatar-initials">M</span>
          </div>
          <div className="tech-badge badge-1">⚛️ React</div>
          <div className="tech-badge badge-2">☕ Java</div>
          <div className="tech-badge badge-3">🐍 Python</div>
        </div>
      </div>

      <div className="scroll-indicator" onClick={() => handleScroll('about')}>
        <span>Scroll Down</span>
        <div className="scroll-arrow" />
      </div>
    </section>
  );
}
