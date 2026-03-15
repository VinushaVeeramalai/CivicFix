# 🏛 CivicFix — Crowdsourced Civic Issue Reporting System

[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0-green)](https://www.mongodb.com)
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

> *Empowering citizens to report, track, and resolve civic issues through AI-powered community collaboration.*

---

## 📌 What is CivicFix?

CivicFix is a full-stack web application that allows citizens to report civic problems such as potholes, broken streetlights, garbage overflow, water leaks, and sewage issues in their locality. The platform uses AI-based severity scoring, community upvoting, and an interactive heatmap to prioritize and track issues efficiently.

Citizens earn points for reporting and upvoting issues, creating a gamified engagement system that encourages active participation. Municipal administrators can manage, update, and resolve issues through a dedicated admin dashboard.

---

## 🎯 Problem Statement

Urban civic infrastructure in India faces critical challenges:

- ❌ No centralized platform for citizens to report local issues
- ❌ Long resolution times due to manual routing and lack of prioritization
- ❌ Low citizen engagement and participation in civic governance
- ❌ No transparency in issue tracking and resolution status
- ❌ Authorities lack data to identify high-priority problem zones
- ❌ No mechanism to escalate urgent issues automatically

**The result:** Potholes go unrepaired for months, streetlights stay broken, and garbage piles up — while citizens have no voice and authorities have no visibility.

---

## 💡 Solution

CivicFix addresses these challenges by providing:

1. **A citizen-facing reporting platform** with photo upload, GPS location, and category selection
2. **AI-powered severity scoring** that automatically classifies issues as Critical, High, Medium, or Low based on keywords
3. **Community upvoting** that democratically prioritizes issues
4. **Auto-escalation** that promotes issues to Critical status when they receive 10+ upvotes
5. **Interactive heatmap** showing problem zones across the city
6. **Admin dashboard** for municipal staff to track and resolve issues
7. **Points & leaderboard** to gamify citizen participation

---

## ✨ Key Features

### 🤖 AI Severity Scoring
When a citizen submits an issue, the system automatically analyzes the category and description text using keyword pattern matching to assign a severity level:
- 🔴 **Critical** — collapse, flood, fire, emergency, gas leak, electric hazard
- 🟠 **High** — pothole, broken, water leak, overflow, power cut, major damage
- 🟡 **Medium** — garbage, streetlight, road damage (default)
- 🟢 **Low** — minor, cosmetic, small, graffiti, noise

### 🔥 Auto-Escalation
Issues that receive **10 or more community upvotes** are automatically:
- Escalated to **Critical** severity
- Status changed to **In Progress**
This ensures high-impact issues get immediate attention without manual intervention.

### 🗺 Interactive Issue Heatmap
Built with **Leaflet.js + OpenStreetMap**, the heatmap shows:
- Color-coded markers by severity (red, orange, yellow, green, purple)
- Clickable popups with full issue details
- Real-time issue distribution across the city
- Issues without GPS appear scattered near city center

### ⭐ Points & Leaderboard
- **+10 points** for reporting an issue
- **+2 points** for upvoting an issue
- Top citizens ranked on the leaderboard in the admin dashboard
- Encourages consistent community participation

### 👥 Community Upvoting
- Any logged-in user can upvote or remove their upvote
- AJAX-powered — no page reload needed
- Upvote count updates in real time
- Prevents duplicate votes per user

### 📊 Admin Dashboard
- View all issues with severity, upvotes, status
- Update status (Pending → In Progress → Resolved) and severity
- View citizen leaderboard
- Stats overview: Total, Pending, In Progress, Resolved, Critical

---

## 🆚 How CivicFix Differs from Existing Apps

| Feature | CivicFix | Traditional Apps | Swachh Bharat App |
|---|---|---|---|
| AI Severity Scoring | ✅ Auto | ❌ Manual | ❌ Manual |
| Auto-Escalation | ✅ At 10 upvotes | ❌ No | ❌ No |
| Community Upvoting | ✅ Yes | ❌ No | ❌ No |
| Interactive Heatmap | ✅ Yes | ❌ No | ❌ No |
| Points & Leaderboard | ✅ Gamified | ❌ No | ❌ No |
| Real-time Updates | ✅ AJAX | ❌ Page reload | ❌ Page reload |
| GPS Location Capture | ✅ One click | ⚠️ Manual | ⚠️ Manual |
| Open Source | ✅ Yes | ❌ No | ❌ No |

---

## 🧠 Algorithm Used

### Severity Classification Algorithm

```
INPUT: category (String), description (String)
OUTPUT: severity (Critical | High | Medium | Low)

1. Combine category + description → lowercase text
2. Check for CRITICAL keywords:
   → collapse, flood, fire, danger, accident, emergency,
     electric shock, sewage overflow, burst pipe, gas leak,
     hazard, unsafe, injury, blocked road
   → IF found: return "Critical"

3. Check for HIGH keywords:
   → pothole, broken, no water, no electricity, overflow,
     damaged, large, deep, major, serious, urgent, leak
   → IF found: return "High"
   → ALSO if category is Pothole / Water Leak / Sewage: return "High"

4. Check for LOW keywords:
   → minor, small, slight, cosmetic, paint, sign,
     bench, graffiti, noise
   → IF found: return "Low"

5. DEFAULT by category:
   → Streetlight / Garbage → "Medium"
   → All others → "Medium"
```

### Auto-Escalation Algorithm

```
ON upvote(issueId, userId):
  IF userId already in issue.upvotedBy:
    → Remove userId, decrement upvotes
  ELSE:
    → Add userId, increment upvotes
    → Award +2 points to voter
  
  IF issue.upvotes >= 10 AND severity != "Critical":
    → Set severity = "Critical"
    → Set status = "In Progress"
    → Save to database
```

---

## 🏗 System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Browser (Client)                      │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │  Home    │  │  Report  │  │   Map    │  │  Admin   │   │
│  │  Page    │  │  Form    │  │ Heatmap  │  │Dashboard │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
│         HTML + CSS + JavaScript + Thymeleaf                  │
│         Leaflet.js (Map) | Fetch API (Upvotes)               │
└──────────────────────────┬──────────────────────────────────┘
                           │ HTTP Requests
┌──────────────────────────▼──────────────────────────────────┐
│                   Spring Boot Backend                        │
│  ┌──────────────────────────────────────────────────────┐   │
│  │                   Controllers                         │   │
│  │  AuthController  │  IssueController                  │   │
│  │  /login /register│  / /report /issues /map /admin    │   │
│  └──────────────────┬───────────────────────────────────┘   │
│                     │                                        │
│  ┌──────────────────▼───────────────────────────────────┐   │
│  │                   Services                            │   │
│  │         SeverityService (AI Keyword Scoring)          │   │
│  └──────────────────┬───────────────────────────────────┘   │
│                     │                                        │
│  ┌──────────────────▼───────────────────────────────────┐   │
│  │                 Repositories                          │   │
│  │      UserRepository  │  IssueRepository              │   │
│  │      (MongoRepository - Spring Data)                  │   │
│  └──────────────────┬───────────────────────────────────┘   │
└─────────────────────┼───────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                    MongoDB Database                          │
│         Collections: users | issues                         │
└─────────────────────────────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│               External Services                             │
│   OpenStreetMap (Map tiles) | Nominatim (Reverse Geocode)   │
└─────────────────────────────────────────────────────────────┘
```

### Data Flow
1. Citizen submits issue via report form
2. SeverityService analyzes keywords → assigns severity
3. Issue saved to MongoDB with GPS coordinates
4. Community upvotes via AJAX → auto-escalation logic runs
5. Map page fetches `/api/issues` JSON → renders colored markers
6. Admin updates status → citizens see real-time resolution

---

## 🛠 Tech Stack

### Backend
| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Core language |
| Spring Boot | 3.3.5 | Web framework |
| Spring Data MongoDB | 4.3.5 | Database ORM |
| Spring Security Crypto | 6.x | BCrypt password hashing |
| Apache Tomcat | 10.1 | Embedded web server |
| Maven | 3.9 | Build tool |

### Frontend
| Technology | Purpose |
|---|---|
| Thymeleaf | Server-side HTML templating |
| HTML5 + CSS3 | Structure and styling |
| Vanilla JavaScript | Interactivity and AJAX |
| Leaflet.js 1.9.4 | Interactive maps |
| OpenStreetMap | Free map tiles |
| Nominatim API | Reverse geocoding |

### Database
| Technology | Purpose |
|---|---|
| MongoDB 7.0 | NoSQL document database |
| Collections: users, issues | Data storage |

---

## 📁 Project Structure

```
CivicFix/
├── pom.xml
├── src/
│   └── main/
│       ├── java/com/civicfix/
│       │   ├── CivicFixApplication.java      # App entry point + admin seed
│       │   ├── model/
│       │   │   ├── User.java                 # User model
│       │   │   └── Issue.java                # Issue model
│       │   ├── repository/
│       │   │   ├── UserRepository.java       # MongoDB queries for users
│       │   │   └── IssueRepository.java      # MongoDB queries for issues
│       │   ├── controller/
│       │   │   ├── AuthController.java       # Login, register, logout
│       │   │   └── IssueController.java      # Issues CRUD + upvote + admin
│       │   ├── service/
│       │   │   └── SeverityService.java      # AI keyword severity scoring
│       │   └── config/
│       │       └── WebConfig.java            # Static resource config
│       └── resources/
│           ├── application.properties
│           ├── static/
│           │   ├── css/style.css
│           │   └── js/main.js
│           └── templates/
│               ├── home.html
│               ├── login.html
│               ├── register.html
│               ├── report.html
│               ├── issues.html
│               ├── map.html
│               ├── admin.html
│               └── profile.html
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.9+
- MongoDB 7.0 (running locally)

### Installation

```bash
# 1. Clone the repository
git clone https://github.com/VinushaVeeramalai/CivicFix.git
cd CivicFix

# 2. Start MongoDB service (Windows)
net start MongoDB

# 3. Run the application
mvn clean spring-boot:run

# 4. Open browser
http://localhost:8080
```

### Default Admin Credentials
```
Email:    admin@civicfix.com
Password: admin123
```

---

## 📈 Impact & Metrics

### Target Impact

| Metric | Without CivicFix | With CivicFix | Improvement |
|---|---|---|---|
| Issue Reporting Time | Hours (office visit) | 2 minutes (mobile) | **95% faster** |
| Issue Prioritization | Manual, days | AI, instant | **99% faster** |
| Citizen Participation | Very low | Gamified engagement | **10x increase** |
| Resolution Transparency | None | Real-time tracking | **Full visibility** |
| Problem Zone Identification | Manual surveys | Live heatmap | **Automated** |
| Critical Issue Detection | Weeks | Auto at 10 upvotes | **Near real-time** |

### Projected Benefits
- 💰 **Cost Reduction** — 40% reduction in manual issue routing costs
- ⏱ **Faster Resolution** — 60-70% faster response to critical issues
- 🏙 **Better Governance** — Data-driven decisions using heatmap analytics
- 👥 **Citizen Empowerment** — Democratic participation via upvoting
- 🌱 **Cleaner Cities** — Faster reporting → faster cleanup → healthier communities

---

## 🔮 Future Outlook

### Phase 2 Features (Planned)
- 📱 **Mobile App** — React Native app for Android and iOS
- 🔔 **Push Notifications** — Alert citizens when their reported issue is resolved
- 🤖 **Image AI Classification** — Auto-classify issue type from uploaded photo using ML
- 💬 **Comment System** — Citizens can comment on issues for more context
- 📊 **Analytics Dashboard** — Charts showing resolution rates, category trends
- 🏆 **Badges & Achievements** — Reward top contributors with digital badges
- 🌐 **Multi-language Support** — Tamil, Hindi, Telugu, Kannada
- 🔗 **Government API Integration** — Direct routing to municipal corporation systems
- 📧 **Email Notifications** — Updates sent to reporter when status changes
- 🗳 **Department Routing** — Auto-assign to PWD, WMD, ED based on category

### Phase 3 Vision
- AI-powered **predictive maintenance** — forecast which areas will develop issues
- **Blockchain-based** transparency for issue resolution audit trail
- Integration with **Smart City** infrastructure sensors
- **Citizen satisfaction scoring** after issue resolution

---

## 👩‍💻 Developer

**Vinusha Veeramalai**
- GitHub: [@VinushaVeeramalai](https://github.com/VinushaVeeramalai)

---

## 📄 License

This project is licensed under the MIT License.

---

*Built with ❤️ to make cities cleaner and smarter, one report at a time.* 🌿
