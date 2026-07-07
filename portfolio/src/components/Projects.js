import React, { useState } from 'react';
import './Projects.css';

const projects = [
  {
    title: 'Student Academic Portal',
    emoji: '🎓',
    desc: 'A comprehensive web portal for managing student academic activities — including attendance tracking, grade management, course enrollment, and faculty communication.',
    tech: ['React', 'Java', 'MySQL', 'Spring Boot'],
    color: '#6c63ff',
    features: ['Attendance Tracking', 'Grade Management', 'Course Enrollment', 'Faculty Portal'],
    status: 'Completed',
  },
  {
    title: 'E-Learning Application',
    emoji: '📚',
    desc: 'An interactive e-learning platform that enables students to access video lectures, take quizzes, track progress, and receive certifications upon completion.',
    tech: ['React', 'Python', 'Django', 'PostgreSQL'],
    color: '#f72585',
    features: ['Video Lectures', 'Live Quizzes', 'Progress Tracking', 'Certification'],
    status: 'Completed',
  },
  {
    title: 'Personal Portfolio',
    emoji: '🌐',
    desc: 'A modern, responsive personal portfolio website built with React showcasing projects, skills, education, and professional profile.',
    tech: ['React', 'CSS3', 'JavaScript', 'HTML5'],
    color: '#4cc9f0',
    features: ['Responsive Design', 'Dark Theme', 'Smooth Animations', 'Contact Form'],
    status: 'Live',
  },
];

export default function Projects() {
  const [hovered, setHovered] = useState(null);

  return (
    <section id="projects" className="projects-section">
      <div className="container">
        <h2 className="section-title">My <span>Projects</span></h2>
        <p className="section-subtitle">Things I've built with passion</p>

        <div className="projects-grid">
          {projects.map((proj, i) => (
            <div
              key={i}
              className={`project-card card ${hovered === i ? 'hovered' : ''}`}
              onMouseEnter={() => setHovered(i)}
              onMouseLeave={() => setHovered(null)}
              style={{ '--proj-color': proj.color }}
            >
              <div className="project-top">
                <div className="project-emoji">{proj.emoji}</div>
                <span className={`project-status ${proj.status === 'Live' ? 'live' : 'done'}`}>
                  {proj.status === 'Live' ? '🟢' : '✅'} {proj.status}
                </span>
              </div>

              <h3 className="project-title">{proj.title}</h3>
              <p className="project-desc">{proj.desc}</p>

              <div className="project-features">
                {proj.features.map((f) => (
                  <span className="feature-tag" key={f}>{f}</span>
                ))}
              </div>

              <div className="project-tech">
                {proj.tech.map((t) => (
                  <span className="tech-tag" key={t}>{t}</span>
                ))}
              </div>

              <div className="project-line" style={{ background: proj.color }} />
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
