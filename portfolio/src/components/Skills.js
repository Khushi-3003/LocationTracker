import React from 'react';
import './Skills.css';

const skills = [
  { name: 'HTML',       icon: '🌐', level: 90, color: '#e34f26' },
  { name: 'CSS',        icon: '🎨', level: 85, color: '#264de4' },
  { name: 'JavaScript', icon: '⚡', level: 78, color: '#f7df1e' },
  { name: 'Java',       icon: '☕', level: 75, color: '#007396' },
  { name: 'Python',     icon: '🐍', level: 72, color: '#3776ab' },
  { name: 'React',      icon: '⚛️', level: 70, color: '#61dafb' },
  { name: 'MySQL',      icon: '🗄️', level: 68, color: '#4479a1' },
  { name: 'Git',        icon: '🔧', level: 65, color: '#f05032' },
];

const categories = [
  { label: 'Frontend',  items: ['HTML', 'CSS', 'JavaScript', 'React'] },
  { label: 'Backend',   items: ['Java', 'Python'] },
  { label: 'Database',  items: ['MySQL'] },
  { label: 'Tools',     items: ['Git'] },
];

export default function Skills() {
  return (
    <section id="skills">
      <div className="container">
        <h2 className="section-title">My <span>Skills</span></h2>
        <p className="section-subtitle">Technologies I work with</p>

        <div className="skills-grid">
          {skills.map((skill) => (
            <div className="skill-card card" key={skill.name}>
              <div className="skill-icon" style={{ '--skill-color': skill.color }}>
                {skill.icon}
              </div>
              <h4 className="skill-name">{skill.name}</h4>
              <div className="skill-bar-wrap">
                <div
                  className="skill-bar"
                  style={{ '--level': `${skill.level}%`, '--color': skill.color }}
                />
              </div>
              <span className="skill-pct">{skill.level}%</span>
            </div>
          ))}
        </div>

        <div className="skill-categories">
          {categories.map((cat) => (
            <div className="category-pill" key={cat.label}>
              <span className="cat-label">{cat.label}:</span>
              <span className="cat-items">{cat.items.join(', ')}</span>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
