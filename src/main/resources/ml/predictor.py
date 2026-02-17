import sys
import json
import random

# 1. Read input data from Java (passed as command line arguments)
# Expected format: gpa, attendance_percentage, missed_classes
try:
    gpa = float(sys.argv[1])
    attendance = float(sys.argv[2])
    missed = int(sys.argv[3])
except:
    # Default values if something goes wrong
    gpa = 3.0
    attendance = 80.0
    missed = 2

# --- 2. THE "AI" LOGIC (Simulation) ---
# In real life, you would load a .pkl model here (e.g., sklearn)

risk_score = 0
prediction_text = ""

# Logic: Low attendance = High Risk
if attendance < 75:
    risk_score += 40

# Logic: Low GPA = High Risk
if gpa < 2.5:
    risk_score += 40
elif gpa < 3.0:
    risk_score += 20

# Logic: Missed classes penalties
risk_score += (missed * 5)

# Calculate Probability
fail_probability = min(risk_score, 99) # Max 99%
next_sem_gpa = gpa + random.uniform(-0.2, 0.4) # Random fluctuation
if next_sem_gpa > 4.0: next_sem_gpa = 4.0

# Generate Report
result = {
    "fail_probability": fail_probability,
    "predicted_next_gpa": round(next_sem_gpa, 2),
    "risk_level": "HIGH" if fail_probability > 50 else "LOW",
    "recommendation": "Needs immediate tutoring." if fail_probability > 50 else "Doing well, keep it up!"
}

# 3. Print JSON so Java can read it
print(json.dumps(result))