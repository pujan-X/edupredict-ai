import sys
import json

def predict(gpa, attendance):
    try:
        # --- ML ALGORITHM LOGIC (Updated for 10.0 CGPA Scale) ---
        # 1. Base calculation
        predicted_gpa = gpa + ((attendance - 80) * 0.05)
        
        # 2. Add some "AI variance" based on patterns
        if attendance > 90 and gpa > 8.0:  # Updated to expect > 8.0 CGPA
            predicted_gpa += 0.4  # High performers boost
        elif attendance < 60:
            predicted_gpa -= 1.0  # Chronic absence penalty

        # 3. Clamp results between 0.0 and 10.0 (THIS WAS THE BUG!)
        predicted_gpa = max(0.0, min(10.0, predicted_gpa))
        
        # 4. Calculate Confidence Score
        confidence = 0.70 + (attendance / 500.0) 
        confidence = min(0.99, confidence)

        return {
            "prediction": round(predicted_gpa, 2),
            "confidence": round(confidence, 2),
            "status": "success"
        }

    except Exception as e:
        return {"status": "error", "message": str(e)}

if __name__ == "__main__":
    # Receive inputs from Java via Command Line Arguments
    if len(sys.argv) < 3:
        print(json.dumps({"status": "error", "message": "Missing arguments"}))
    else:
        current_gpa = float(sys.argv[1])
        attendance_pct = int(sys.argv[2])
        
        result = predict(current_gpa, attendance_pct)
        
        # Print JSON so Java can read it
        print(json.dumps(result))