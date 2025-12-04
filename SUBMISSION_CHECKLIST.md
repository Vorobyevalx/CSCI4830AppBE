# 📋 Submission Checklist

## ✅ Required Items for Canvas Submission

### 1. ✅ Presentation Slides (PDF)
**Status**: Ready (you have all screenshots and content)
- [ ] Export slides to PDF
- [ ] Verify font size > 18 points
- [ ] Ensure 10-15 minute presentation length
- [ ] Check all screenshots are included

---

### 2. ⚠️ URL to Application Running on Cloud Server
**Status**: NEED TO SET UP

**What you need:**
- Google Cloud Compute Engine instance (E2) running your Spring Boot app
- Public IP address for the instance
- Application accessible via HTTP

**URL Format**: `http://YOUR_E2_IP:8080` or `https://YOUR_DOMAIN`

**To get this:**
1. Deploy your Spring Boot app to Google Cloud Compute Engine
2. Get the public IP of your E2 instance
3. Ensure port 8080 is open in firewall rules
4. Test: `curl http://YOUR_E2_IP:8080/api/health`

**Current Status**: 
- ✅ Cloud SQL is set up and working
- ⚠️ Need to deploy Spring Boot app to Compute Engine

---

### 3. ✅ URL to Version Control System
**Status**: READY

**GitHub Repository URL**: 
```
https://github.com/Vorobyevalx/CSCI4830AppBE
```

**Verification:**
- [ ] Repository is public (or accessible to instructors)
- [ ] All code is pushed to GitHub
- [ ] Branch `Issue1` exists with your work
- [ ] README.md is updated

**Quick Test:**
```bash
# Verify remote is correct
git remote -v

# Push any uncommitted changes
git add .
git commit -m "Final submission preparation"
git push origin Issue1
```

---

### 4. ⚠️ URL to Project Management System
**Status**: NEED TO IDENTIFY OR CREATE

**Options:**

**Option A: If you're using a project management tool:**
- Trello: Create a board and share the URL
- Jira: Share project URL
- Asana: Share project URL
- GitHub Projects: Use GitHub's built-in project management
- Notion: Share page URL

**Option B: Use GitHub Issues/Projects (Recommended - Free & Easy):**
1. Go to: https://github.com/Vorobyevalx/CSCI4830AppBE
2. Click "Projects" tab
3. Create a new project board
4. Add issues/tasks from your `gameplan.md`
5. Share the project URL

**Option C: Use gameplan.md as documentation:**
- If no formal PM tool, you can reference:
  - `gameplan.md` file in your repository
  - URL: `https://github.com/Vorobyevalx/CSCI4830AppBE/blob/Issue1/gameplan.md`

**Recommended**: Create a GitHub Project board (it's free and integrated)

---

## 📝 Summary

| Item | Status | URL/Notes |
|------|--------|----------|
| 1. Presentation Slides (PDF) | ✅ Ready | Export to PDF |
| 2. Application URL (Cloud Server) | ⚠️ Need Setup | Deploy to Google Cloud E2 |
| 3. Version Control URL | ✅ Ready | https://github.com/Vorobyevalx/CSCI4830AppBE |
| 4. Project Management URL | ⚠️ Need Setup | Create GitHub Project or use gameplan.md |

---

## 🚀 Quick Actions Needed

### Immediate:
1. **Deploy to Google Cloud E2** (for application URL)
2. **Set up Project Management** (GitHub Projects recommended)

### Before Submission:
1. Export slides to PDF
2. Test application URL works
3. Verify GitHub repository is accessible
4. Share project management URL

---

## 📧 Additional Submission

**Peer Evaluation Form**: 
- URL: https://forms.office.com/r/RViVZ58rpu
- Due: 11:59 PM on presentation day
- Submit separately from main assignment

---

## ✅ Final Checklist Before Submission

- [ ] Presentation PDF exported and ready
- [ ] Application running on cloud server (E2 instance)
- [ ] Application URL tested and working
- [ ] GitHub repository is public/accessible
- [ ] All code pushed to GitHub
- [ ] Project management URL ready
- [ ] All URLs tested and accessible
- [ ] Peer evaluation form submitted (on presentation day)

