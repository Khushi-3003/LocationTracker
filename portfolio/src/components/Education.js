import React from 'react';
import './Education.css';

const education = [
  {
    degree: 'Master of Computer Applications (MCA)',
    institution: 'BMS Institute of Technology & Management',
    location: 'Bangalore, Karnataka',
    period: '2024 – Present',
    icon: '🎓',
    highlight: true,
    desc: 'Pursuing advanced studies in computer science, software engineering, and full-stack development.',
  },
  {
    degree: 'Bachelor of Computer Applications (BCA)',
    institution: 'Kundapura College',
    location: 'Kundapura, Karnataka',
    period: '2021 – 2024',
    icon: '🏛️',
    highlight: false,
    desc: 'Completed undergraduate degree with focus on programming, databases, and computer science fundamentals.',
  },
  {
    degree: 'PU Commerce',
    institution: 'Pre-University College',
    location: 'Sringeri, Karnataka',
    period: '2019 – 2021',
    icon: '📖',
    highlight: false,
    desc: 'Completed pre-university education with Commerce stream.',
  },
];

export default function Education() {
  return (
    <section id="education" className="education-section">
      <div className="container">
        <h2 className="section-title">My <span>Education</span></h2>
        <p className="section-subtitle">Academic journey that shaped my knowledge</p>

        <div className="timeline">
          {education.map((edu, i) => (
            <div key={i} className={`timeline-item ${edu.highlight ? 'featured' : ''}`}>
              <div className="timeline-dot">
                <span>{edu.icon}</span>
              </div>
              <div className="timeline-content card">
                {edu.highlight && <div className="featured-badge">Current</div>}
                <div className="edu-header">
                  <h3 className="edu-degree">{edu.degree}</h3>
                  <span className="edu-period">{edu.period}</span>
                </div>
                <p className="edu-institution">🏫 {edu.institution}</p>
                <p className="edu-location">📍 {edu.location}</p>
                <p className="edu-desc">{edu.desc}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
