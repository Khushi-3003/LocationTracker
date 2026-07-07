import React, { useState } from 'react';
import './Contact.css';

export default function Contact() {
  const [form, setForm] = useState({ name: '', email: '', message: '' });
  const [sent, setSent] = useState(false);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleSubmit = (e) => {
    e.preventDefault();
    setSent(true);
    setTimeout(() => setSent(false), 4000);
    setForm({ name: '', email: '', message: '' });
  };

  return (
    <section id="contact">
      <div className="container">
        <h2 className="section-title">Get In <span>Touch</span></h2>
        <p className="section-subtitle">I'd love to hear from you — let's connect!</p>

        <div className="contact-wrapper">
          {/* Info side */}
          <div className="contact-info">
            <h3>Let's Talk</h3>
            <p>
              Whether you have a project idea, want to collaborate, or just want to say hello —
              my inbox is always open!
            </p>

            <div className="contact-items">
              <div className="contact-item">
                <div className="ci-icon">📧</div>
                <div>
                  <div className="ci-label">Email</div>
                  <div className="ci-value">manthan@example.com</div>
                </div>
              </div>
              <div className="contact-item">
                <div className="ci-icon">📍</div>
                <div>
                  <div className="ci-label">Location</div>
                  <div className="ci-value">Bangalore, Karnataka</div>
                </div>
              </div>
              <div className="contact-item">
                <div className="ci-icon">🎓</div>
                <div>
                  <div className="ci-label">Institution</div>
                  <div className="ci-value">BMSIT, Bangalore</div>
                </div>
              </div>
            </div>

            <div className="social-links">
              <a href="#!" className="social-btn" aria-label="GitHub">
                <span>🐙</span> GitHub
              </a>
              <a href="#!" className="social-btn" aria-label="LinkedIn">
                <span>💼</span> LinkedIn
              </a>
            </div>
          </div>

          {/* Form side */}
          <div className="contact-form-wrap">
            {sent ? (
              <div className="success-msg">
                <div className="success-icon">🎉</div>
                <h3>Message Sent!</h3>
                <p>Thanks for reaching out. I'll get back to you shortly!</p>
              </div>
            ) : (
              <form className="contact-form" onSubmit={handleSubmit}>
                <div className="form-group">
                  <label htmlFor="name">Your Name</label>
                  <input
                    id="name"
                    name="name"
                    type="text"
                    value={form.name}
                    onChange={handleChange}
                    placeholder="Enter your name"
                    required
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="email">Email Address</label>
                  <input
                    id="email"
                    name="email"
                    type="email"
                    value={form.email}
                    onChange={handleChange}
                    placeholder="Enter your email"
                    required
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="message">Message</label>
                  <textarea
                    id="message"
                    name="message"
                    value={form.message}
                    onChange={handleChange}
                    placeholder="Write your message..."
                    rows={5}
                    required
                  />
                </div>
                <button type="submit" className="btn btn-primary submit-btn">
                  Send Message 🚀
                </button>
              </form>
            )}
          </div>
        </div>
      </div>
    </section>
  );
}
