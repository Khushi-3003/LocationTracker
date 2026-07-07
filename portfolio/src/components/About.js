import React from 'react';
import './About.css';

export default function About() {
  return (
    <section id="about">
      <div className="container">
        <h2 className="section-title">About <span>Me</span></h2>
        <p className="section-subtitle">A little bit about who I am</p>

        <div className="about-grid">
          <div className="about-card card">
            <div className="about-icon">🎓</div>
            <h3>Who am I?</h3>
            <p>
              I'm <strong>Manthan</strong>, currently pursuing my <strong>MCA (Master of Computer Applications)</strong> at
              BMS Institute of Technology &amp; Management, Bangalore. I'm passionate about software development,
              problem-solving, and creating impactful digital experiences.
            </p>
          </div>

          <div className="about-card card">
            <div className="about-icon">💡</div>
            <h3>What I Do?</h3>
            <p>
              I build web applications using modern technologies like React, Java, and Python.
              I love turning complex problems into elegant, user-friendly solutions — from academic portals
              to e-learning platforms.
            </p>
          </div>

          <div className="about-card card">
            <div className="about-icon">🚀</div>
            <h3>My Goal</h3>
            <p>
              To leverage my technical skills and academic knowledge to develop innovative software solutions
              that make a difference. I'm always learning, always growing, and always ready for the next challenge.
            </p>
          </div>
        </div>

        <div className="about-info-row">
          <div className="info-item">
            <span className="info-icon">📍</span>
            <span><strong>Location:</strong> Bangalore, Karnataka</span>
          </div>
          <div className="info-item">
            <span className="info-icon">🏫</span>
            <span><strong>Institution:</strong> BMSIT, Bangalore</span>
          </div>
          <div className="info-item">
            <span className="info-icon">📚</span>
            <span><strong>Degree:</strong> MCA (Pursuing)</span>
          </div>
          <div className="info-item">
            <span className="info-icon">💻</span>
            <span><strong>Focus:</strong> Full Stack Development</span>
          </div>
        </div>
      </div>
    </section>
  );
}
