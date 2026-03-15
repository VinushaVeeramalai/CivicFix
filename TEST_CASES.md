# 🧪 CivicFix — Test Cases Document

**Project:** CivicFix — Crowdsourced Civic Issue Reporting System  
**Developer:** Vinusha Veeramalai  
**Version:** 1.0.0  
**Test Environment:** localhost:8080 | MongoDB 7.0 | Java 17 | Spring Boot 3.3.5

---

## 📋 Test Summary

| Total Test Cases | Passed | Failed | Pass Rate |
|---|---|---|---|
| 35 | 35 | 0 | 100% |

---

## 1. User Registration Module

| TC ID | Test Scenario | Test Steps | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| TC01 | Successful user registration | 1. Go to http://localhost:8080/register 2. Enter Name: "Test User" 3. Enter Email: "test@gmail.com" 4. Enter Password: "test123" 5. Click Register | Account created successfully, redirected to /login with success message | Account created, redirected to login | ✅ Pass |
| TC02 | Registration with duplicate email | 1. Go to /register 2. Enter already registered email 3. Fill other fields 4. Click Register | Error message: "Email already registered" | Error message displayed | ✅ Pass |
| TC03 | Registration with empty fields | 1. Go to /register 2. Leave Name empty 3. Click Register | Form validation error, registration blocked | Browser validation prevents submission | ✅ Pass |
| TC04 | Registration with invalid email format | 1. Go to /register 2. Enter email: "notanemail" 3. Click Register | Browser shows invalid email error | Browser validation blocks submission | ✅ Pass |
| TC05 | Registration with optional phone field empty | 1. Go to /register 2. Fill Name, Email, Password 3. Leave Phone empty 4. Click Register | Account created successfully without phone | Account created successfully | ✅ Pass |

---

## 2. User Login Module

| TC ID | Test Scenario | Test Steps | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| TC06 | Successful user login | 1. Go to /login 2. Enter Email: "test@gmail.com" 3. Enter Password: "test123" 4. Click Login | Logged in successfully, redirected to home page | Redirected to home page | ✅ Pass |
| TC07 | Login with wrong password | 1. Go to /login 2. Enter correct email 3. Enter wrong password 4. Click Login | Error message: "Invalid email or password" | Error message displayed | ✅ Pass |
| TC08 | Login with unregistered email | 1. Go to /login 2. Enter unregistered email 3. Enter any password 4. Click Login | Error message: "Invalid email or password" | Error message displayed | ✅ Pass |
| TC09 | Login with empty fields | 1. Go to /login 2. Leave email empty 3. Click Login | Form validation prevents submission | Browser validation blocks submission | ✅ Pass |
| TC10 | Admin login | 1. Go to /login 2. Enter Email: "admin@civicfix.com" 3. Enter Password: "admin123" 4. Click Login | Logged in as admin, admin nav link visible | Admin logged in successfully | ✅ Pass |
| TC11 | User logout | 1. Login as any user 2. Click Logout | Session cleared, redirected to /login | Redirected to login page | ✅ Pass |

---

## 3. Report Issue Module

| TC ID | Test Scenario | Test Steps | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| TC12 | Report issue successfully | 1. Login 2. Go to /report 3. Enter Title: "Big pothole on Main Road" 4. Select Category: Pothole 5. Enter Description: "Large deep pothole causing accidents" 6. Enter Location: "Main Road, Coimbatore" 7. Click Submit | Issue created, redirected to /issues, +10 points awarded | Issue created, points awarded | ✅ Pass |
| TC13 | AI severity score — Critical | 1. Login 2. Go to /report 3. Enter description containing "flood" or "fire" 4. Submit | Severity automatically set to Critical | Severity = Critical | ✅ Pass |
| TC14 | AI severity score — High | 1. Login 2. Go to /report 3. Select Category: Pothole 4. Enter any description 5. Submit | Severity automatically set to High | Severity = High | ✅ Pass |
| TC15 | AI severity score — Low | 1. Login 2. Go to /report 3. Enter description containing "minor" or "small" 4. Submit | Severity automatically set to Low | Severity = Low | ✅ Pass |
| TC16 | Live AI severity preview | 1. Login 2. Go to /report 3. Type "flood" in description field | Severity hint shows "Critical" in real time below form | Real-time preview updates | ✅ Pass |
| TC17 | Report issue with photo | 1. Login 2. Go to /report 3. Fill all fields 4. Upload an image file 5. Submit | Issue created with image visible on card | Issue created with image | ✅ Pass |
| TC18 | Report issue with GPS location | 1. Login 2. Go to /report 3. Click GPS button 4. Allow browser location access | Latitude and longitude captured, location field auto-filled | Location captured successfully | ✅ Pass |
| TC19 | Report issue without login | 1. Without logging in 2. Go to /report | Redirected to /login page | Redirected to login | ✅ Pass |
| TC20 | Points awarded on report | 1. Login 2. Note current points 3. Report an issue | Points increase by 10 | Points increased by +10 | ✅ Pass |

---

## 4. Upvoting Module

| TC ID | Test Scenario | Test Steps | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| TC21 | Upvote an issue | 1. Login 2. Go to /issues 3. Click 👍 button on any issue | Upvote count increases by 1, button turns active, +2 points awarded | Count increased, points awarded | ✅ Pass |
| TC22 | Remove upvote | 1. Login 2. Upvote an issue 3. Click 👍 button again on same issue | Upvote count decreases by 1, button returns to normal | Count decreased | ✅ Pass |
| TC23 | Prevent duplicate upvote | 1. Login 2. Upvote an issue 3. Try clicking upvote again | Cannot upvote same issue twice, removes vote instead | Duplicate vote prevented | ✅ Pass |
| TC24 | Upvote without login | 1. Without logging in 2. Click upvote button | Toast message: "Login required" | Error toast shown | ✅ Pass |
| TC25 | Auto-escalation at 10 upvotes | 1. Login with multiple accounts 2. Upvote same issue 10 times total | Issue severity changes to Critical, status changes to In Progress automatically | Auto-escalated to Critical | ✅ Pass |
| TC26 | Points awarded on upvote | 1. Login 2. Note current points 3. Upvote an issue | Points increase by 2 | Points increased by +2 | ✅ Pass |

---

## 5. Issues Page & Filter Module

| TC ID | Test Scenario | Test Steps | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| TC27 | View all issues | 1. Go to /issues | All reported issues displayed ordered by upvotes | All issues displayed | ✅ Pass |
| TC28 | Filter by status — Pending | 1. Go to /issues 2. Click "Pending" filter chip | Only pending issues displayed | Pending issues filtered | ✅ Pass |
| TC29 | Filter by severity — Critical | 1. Go to /issues 2. Click "Critical" filter chip | Only critical issues displayed | Critical issues filtered | ✅ Pass |
| TC30 | Filter by category — Pothole | 1. Go to /issues 2. Click "Pothole" filter chip | Only pothole issues displayed | Pothole issues filtered | ✅ Pass |

---

## 6. Map / Heatmap Module

| TC ID | Test Scenario | Test Steps | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| TC31 | Map page loads | 1. Go to /map | Leaflet map loads with OpenStreetMap tiles and issue markers | Map loads with markers | ✅ Pass |
| TC32 | Marker colors by severity | 1. Go to /map 2. Observe marker colors | Critical=Red, High=Orange, Medium=Yellow, Low=Green, Resolved=Purple | Correct colors displayed | ✅ Pass |
| TC33 | Marker popup on click | 1. Go to /map 2. Click any marker | Popup shows issue title, location, category, severity, upvotes | Popup displays correctly | ✅ Pass |

---

## 7. Admin Dashboard Module

| TC ID | Test Scenario | Test Steps | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| TC34 | Admin update issue status | 1. Login as admin 2. Go to /admin 3. Find an issue 4. Change status dropdown to "Resolved" 5. Click Save | Issue status updated to Resolved | Status updated successfully | ✅ Pass |
| TC35 | Admin leaderboard view | 1. Login as admin 2. Go to /admin 3. Click "Users & Leaderboard" tab | Users displayed ranked by points with medals | Leaderboard displayed correctly | ✅ Pass |

---

## 8. End-to-End User Flow Test

| Step | Action | Expected Result | Status |
|---|---|---|---|
| 1 | Open http://localhost:8080 | Home page loads with CivicFix branding | ✅ Pass |
| 2 | Click Register | Registration form opens | ✅ Pass |
| 3 | Fill form and submit | Account created, redirected to login | ✅ Pass |
| 4 | Login with new account | Logged in, home page shows username | ✅ Pass |
| 5 | Click Report Issue | Report form opens | ✅ Pass |
| 6 | Fill form with "pothole" description | AI preview shows High severity | ✅ Pass |
| 7 | Submit report | Issue created, +10 points awarded | ✅ Pass |
| 8 | Go to Issues page | New issue visible with severity badge | ✅ Pass |
| 9 | Click upvote button | Count increases, +2 points awarded | ✅ Pass |
| 10 | Go to Map page | Issue marker visible on Coimbatore map | ✅ Pass |
| 11 | Login as admin | Admin nav link visible | ✅ Pass |
| 12 | Go to /admin | All issues and leaderboard visible | ✅ Pass |
| 13 | Update issue to Resolved | Status changes to Resolved | ✅ Pass |
| 14 | Check leaderboard | User points visible in ranking | ✅ Pass |

---

## 9. Negative / Edge Case Tests

| TC ID | Scenario | Expected Result | Status |
|---|---|---|---|
| E01 | Access /admin without login | Redirect to /login | ✅ Pass |
| E02 | Access /report without login | Redirect to /login | ✅ Pass |
| E03 | Access /admin as normal user | Redirect to /login | ✅ Pass |
| E04 | Submit report with empty title | Browser validation blocks | ✅ Pass |
| E05 | Register with empty email | Browser validation blocks | ✅ Pass |

---

*All test cases executed manually on localhost:8080*  
*Test Date: March 2026*  
*Tested By: Vinusha Veeramalai*
