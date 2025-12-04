# 🌐 How to Get Your Application URL

## Current Situation

**Right now**: Your application is running **locally** on `localhost:8080`
- ✅ Works on your computer
- ❌ Not accessible from the internet
- ❌ Cannot be used as submission URL

**What you need**: Application running on **Google Cloud Compute Engine (E2)** with a **public IP address**

---

## 📍 Where to Find/Create the Application URL

### Option 1: If You Already Have a Google Cloud E2 Instance

1. **Go to Google Cloud Console**
   - Visit: https://console.cloud.google.com/compute/instances

2. **Find Your Instance**
   - Look for your Compute Engine instance (VM)
   - Check the **"External IP"** column

3. **Your Application URL Will Be:**
   ```
   http://YOUR_EXTERNAL_IP:8080
   ```
   Example: `http://34.123.45.67:8080`

4. **Test It:**
   ```bash
   curl http://YOUR_EXTERNAL_IP:8080/api/health
   ```

---

### Option 2: If You DON'T Have an E2 Instance Yet (Need to Create One)

#### Step 1: Create Compute Engine Instance

1. **Go to Compute Engine**
   - Visit: https://console.cloud.google.com/compute/instances
   - Click **"Create Instance"**

2. **Configure Instance:**
   - **Name**: `securebank-backend` (or any name)
   - **Machine Type**: `e2-medium` (or `e2-small` for testing)
   - **Region**: `us-central1` (same as your Cloud SQL)
   - **Boot Disk**: 
     - OS: **Ubuntu 22.04 LTS**
     - Size: 20 GB (minimum)

3. **Firewall:**
   - ✅ Check **"Allow HTTP traffic"**
   - ✅ Check **"Allow HTTPS traffic"**
   - ⚠️ **IMPORTANT**: Also need to allow port 8080
     - Go to **"Firewall"** section
     - Click **"Create firewall rule"**
     - Name: `allow-8080`
     - Ports: `8080`
     - Source IP ranges: `0.0.0.0/0` (or specific IPs)

4. **Click "Create"**

5. **Get Public IP:**
   - After creation, find your instance
   - Copy the **"External IP"** address
   - This is your application URL base: `http://EXTERNAL_IP:8080`

---

#### Step 2: Deploy Your Application

**Method A: SSH and Deploy Directly**

1. **SSH into Your Instance:**
   ```bash
   # From Google Cloud Console, click "SSH" button next to your instance
   # OR use gcloud CLI:
   gcloud compute ssh securebank-backend --zone=us-central1-a
   ```

2. **Install Java 17:**
   ```bash
   sudo apt update
   sudo apt install openjdk-17-jdk -y
   java -version  # Verify installation
   ```

3. **Clone Your Repository:**
   ```bash
   git clone https://github.com/Vorobyevalx/CSCI4830AppBE.git
   cd CSCI4830AppBE
   git checkout Issue1
   ```

4. **Set Environment Variables:**
   ```bash
   export DB_HOST="107.178.210.174"
   export DB_NAME="securebankdb"
   export DB_USER="securebank"
   export DB_PASSWORD='Welcome2025!'
   ```

5. **Build and Run:**
   ```bash
   ./mvnw clean package -DskipTests
   java -jar -Dspring.profiles.active=prod target/securebank-backend-0.0.1-SNAPSHOT.jar
   ```

6. **Keep It Running:**
   - Use `screen` or `tmux` to keep it running after SSH disconnect:
   ```bash
   sudo apt install screen -y
   screen -S backend
   # Run your java command here
   # Press Ctrl+A then D to detach
   ```

**Method B: Build JAR Locally and Upload**

1. **Build JAR on Your Computer:**
   ```bash
   ./mvnw clean package -DskipTests
   ```

2. **Upload to Google Cloud:**
   ```bash
   gcloud compute scp target/securebank-backend-0.0.1-SNAPSHOT.jar securebank-backend:~/ --zone=us-central1-a
   ```

3. **SSH and Run:**
   ```bash
   gcloud compute ssh securebank-backend --zone=us-central1-a
   export DB_HOST="107.178.210.174"
   export DB_NAME="securebankdb"
   export DB_USER="securebank"
   export DB_PASSWORD='Welcome2025!'
   java -jar -Dspring.profiles.active=prod securebank-backend-0.0.1-SNAPSHOT.jar
   ```

---

#### Step 3: Update Cloud SQL Authorized Networks

**IMPORTANT**: Add your E2 instance's IP to Cloud SQL authorized networks!

1. **Get Your E2 Instance IP:**
   - From Compute Engine console, note the External IP

2. **Add to Cloud SQL:**
   - Go to: https://console.cloud.google.com/sql/instances/securebank-fraud-db/connections
   - Add network: `YOUR_E2_IP/32`
   - Save

---

#### Step 4: Test Your Application URL

```bash
# From your local computer or browser
curl http://YOUR_E2_EXTERNAL_IP:8080/api/health
```

**Expected Response:**
```json
{
  "service": "SecureBank Hub Backend",
  "version": "0.0.1-SNAPSHOT",
  "status": "UP",
  "timestamp": "..."
}
```

---

## ✅ Final Application URL Format

Once deployed, your URL will be:
```
http://YOUR_E2_EXTERNAL_IP:8080
```

**Example:**
```
http://34.123.45.67:8080
```

**Health Check Endpoint:**
```
http://34.123.45.67:8080/api/health
```

---

## 🚨 Common Issues

### Issue 1: Connection Refused
**Solution**: 
- Check firewall rules allow port 8080
- Verify application is running on the instance
- Check Cloud SQL authorized networks includes E2 IP

### Issue 2: Application Not Starting
**Solution**:
- Check Java is installed: `java -version`
- Verify environment variables are set
- Check logs for errors

### Issue 3: Database Connection Fails
**Solution**:
- Add E2 instance IP to Cloud SQL authorized networks
- Verify environment variables are correct
- Test connection from E2 instance: `nc -zv 107.178.210.174 3306`

---

## 📝 Quick Checklist

- [ ] Compute Engine instance created
- [ ] External IP address obtained
- [ ] Firewall rule for port 8080 created
- [ ] Java 17 installed on instance
- [ ] Application deployed to instance
- [ ] Environment variables set
- [ ] Application running
- [ ] E2 IP added to Cloud SQL authorized networks
- [ ] Health endpoint tested: `http://EXTERNAL_IP:8080/api/health`
- [ ] URL ready for submission: `http://EXTERNAL_IP:8080`

---

## 🎯 Summary

**Your Application URL = `http://YOUR_E2_EXTERNAL_IP:8080`**

To get it:
1. Create/Find Google Cloud Compute Engine instance
2. Get the External IP address
3. Deploy your Spring Boot application
4. Test: `curl http://EXTERNAL_IP:8080/api/health`
5. Use that URL for submission!

