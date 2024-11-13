# 🎵 Music Review App

Welcome to our music community! This platform lets music lovers share their thoughts, discover new artists, and connect with fellow enthusiasts.

## ✨ What's This All About?

Ever wanted to share your thoughts about that album that's been on repeat? Or find people who love the same obscure artist as you? That's exactly what we built this for! It's a space where you can:

- Write and share music reviews
- Connect with other music lovers
- Keep track of your favorite artists, albums, and songs
- Discover new music through community recommendations

## 🚀 Getting Started

### For Users

Just head over to our site, sign up, and you're ready to:
- Create your personalized profile
- Start following your favorite artists
- Share your music reviews
- Connect with other music enthusiasts

### For Developers

Want to run this locally? Here's how:

1. **Set Up Your Environment**
```bash
# Make sure you have:
- Java 11+
- Maven 3.6+
- PostgreSQL 12+
```

2. **Clone & Configure**
```bash
# Clone the repo
git clone https://github.com/mdaneri/musicboxd.git

# Set up your config
cp application.properties.example application.properties
# Edit application.properties with your details
```

3. **Run It!**
```bash
mvn clean install
mvn tomcat7:run
# Visit http://localhost:8080 and you're good to go!
```

## 🎨 Cool Features

### For Music Lovers
- **Personalized Profiles**: Make it yours with custom themes (dark, kawaii, forest, ocean, sepia)
- **Multi-language**: Use the app in English, Spanish, French, German, Italian, Portuguese, or Japanese
- **Social Features**: Follow other reviewers, comment on reviews, and build your music community

### For Reviewers
- Write detailed reviews
- Rate albums and songs
- Share your music journey
- Get notifications when people engage with your content

### For Moderators
- Keep the community vibrant and healthy
- Manage content quality
- Help curate artist and album information

## 🔧 Development Notes

### 🏗️ Project Structure
```
musicboxd/
├── 📂 interfaces         
│   ├── src/main/java     # Service interfaces
│   └── pom.xml          
│
├── 📂 models             
│   ├── src/main/java     # Domain entities
│   ├── src/test/java     # Entity unit tests
│   └── pom.xml
│
├── 📂 persistence        
│   ├── src/main/java     # Database layer implementation
│   ├── src/main/resources# Database scripts and configs
│   ├── src/test/java     # Repository tests
│   └── pom.xml
│
├── 📂 services           
│   ├── src/main/java     # Business logic implementation
│   ├── src/test/java     # Service tests
│   └── pom.xml
│
├── 📂 webapp             
│   ├── src/main/java     # Controllers and web config
│   ├── src/main/webapp   # JSP views, static resources
│   ├── src/test/java     # Controller tests
│   └── pom.xml
│
├── .gitignore
└── pom.xml              # Parent POM file
```

## 📜 License

This project is under the MIT License - see [LICENSE.md](LICENSE.md) for the full text.

---

Made with ❤️ by music lovers, for music lovers.

Remember: Every great musician was first a great listener! 🎧
