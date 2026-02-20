# 🎓 EduPredict - AI-Powered Student Performance Dashboard

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Python](https://img.shields.io/badge/Python-3.x-3776AB?style=for-the-badge&logo=python&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-Aiven_Cloud-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Render](https://img.shields.io/badge/Deployed_on-Render-46E3B7?style=for-the-badge&logo=render&logoColor=white)

EduPredict is a full-stack academic surveillance and prediction platform designed to help educators identify at-risk students before they fall behind. By combining a robust Spring Boot backend with a Python-based machine learning algorithm, the system actively analyzes student GPA and attendance data to predict future academic performance and categorize students by risk level.

## 🚀 Live Demo
**URL:** [https://edupredict-ai-swd1.onrender.com](https://edupredict-ai-swd1.onrender.com)

*(Note: The application is hosted on a free Render tier and may take 30-50 seconds to spin up if it has been idle.)*

**Test Credentials:**
* **Username:** `admin`
* **Password:** `admin123`

## ✨ Key Features
* **AI-Driven Risk Analysis:** Seamlessly passes Java data to a local Python ML script via command-line arguments, retrieving JSON predictions to calculate accurate risk categorizations (Low, Medium, High).
* **Bulk Prediction Engine:** One-click integration to run predictions on the entire student database simultaneously.
* **Cloud Database Integration:** Fully integrated with an Aiven-hosted MySQL cloud database for persistent, secure remote storage.
* **Dynamic Dashboard:** Real-time data visualization showing current student metrics and AI-generated insights.
* **Automated Data Seeding:** Built-in `DatabaseLoader` intelligently checks for empty databases and automatically provisions admin accounts and sample data upon first launch.

## 🛠️ Technical Architecture
* **Backend:** Java 17, Spring Boot, Spring Data JPA, Hibernate, Spring Security.
* **AI/Scripting:** Python 3 (custom prediction modeling and clamping algorithms).
* **Database:** MySQL (Hosted on Aiven Cloud).
* **Deployment:** Dockerized and automatically deployed via Render CI/CD.

## 💻 Local Setup Instructions

If you wish to run this project locally, ensure you have **Java 17**, **Maven**, and **Python 3** installed on your machine.

**1. Clone the repository**
```bash
git clone [https://github.com/pujan-X/edupredict-ai.git](https://github.com/pujan-X/edupredict-ai.git)
cd edupredict-ai
