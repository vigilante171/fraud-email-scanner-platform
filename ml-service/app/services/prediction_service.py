import os
import joblib
from typing import List

from app.schemas.prediction_schema import EmailPredictionRequest, EmailPredictionResponse


class PredictionService:
    MODEL_VERSION = "tfidf-logistic-regression-v1"

    def __init__(self):
        base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

        self.model_path = os.path.join(base_dir, "models", "fraud_email_model.pkl")
        self.vectorizer_path = os.path.join(base_dir, "models", "tfidf_vectorizer.pkl")

        self.model = None
        self.vectorizer = None

        self.load_model()

    def load_model(self):
        if not os.path.exists(self.model_path):
            raise FileNotFoundError(f"Model file not found: {self.model_path}")

        if not os.path.exists(self.vectorizer_path):
            raise FileNotFoundError(f"Vectorizer file not found: {self.vectorizer_path}")

        self.model = joblib.load(self.model_path)
        self.vectorizer = joblib.load(self.vectorizer_path)

        print("ML model loaded successfully.")
        print(f"Model path: {self.model_path}")
        print(f"Vectorizer path: {self.vectorizer_path}")

    def predict(self, request: EmailPredictionRequest) -> EmailPredictionResponse:
        email_text = self.build_email_text(request)

        vectorized_text = self.vectorizer.transform([email_text])

        predicted_label = self.model.predict(vectorized_text)[0]

        fraud_probability = self.get_fraud_probability(vectorized_text)

        prediction = "FRAUD" if predicted_label == "fraud" else "SAFE"
        risk_level = self.calculate_risk_level(fraud_probability)

        reasons = self.generate_reasons(
            email_text=email_text,
            prediction=prediction,
            fraud_probability=fraud_probability
        )

        return EmailPredictionResponse(
            fraudProbability=round(float(fraud_probability), 4),
            prediction=prediction,
            riskLevel=risk_level,
            reasons=reasons,
            modelVersion=self.MODEL_VERSION
        )

    def build_email_text(self, request: EmailPredictionRequest) -> str:
        return f"""
        Sender: {request.sender}
        Subject: {request.subject}
        Body: {request.body}
        """.strip()

    def get_fraud_probability(self, vectorized_text) -> float:
        if not hasattr(self.model, "predict_proba"):
            prediction = self.model.predict(vectorized_text)[0]
            return 1.0 if prediction == "fraud" else 0.0

        probabilities = self.model.predict_proba(vectorized_text)[0]
        classes = list(self.model.classes_)

        if "fraud" not in classes:
            return 0.0

        fraud_index = classes.index("fraud")
        return float(probabilities[fraud_index])

    def calculate_risk_level(self, fraud_probability: float) -> str:
        if fraud_probability >= 0.75:
            return "HIGH"

        if fraud_probability >= 0.40:
            return "MEDIUM"

        return "LOW"

    def generate_reasons(
            self,
            email_text: str,
            prediction: str,
            fraud_probability: float
    ) -> List[str]:
        text = email_text.lower()
        reasons = []

        suspicious_keywords = [
            "urgent",
            "verify",
            "password",
            "login",
            "bank",
            "account",
            "suspended",
            "payment",
            "click",
            "confirm",
            "security alert",
            "limited time",
            "update",
            "credentials",
        ]

        suspicious_link_signals = [
            "http://",
            "https://",
            ".xyz",
            ".top",
            ".click",
            ".ru",
            "fake",
        ]

        for keyword in suspicious_keywords:
            if keyword in text:
                reasons.append(f"Suspicious term found: {keyword}")

        for signal in suspicious_link_signals:
            if signal in text:
                reasons.append(f"Suspicious link/domain signal found: {signal}")

        if prediction == "FRAUD":
            reasons.insert(
                0,
                f"ML model classified this email as fraud with {round(fraud_probability * 100, 2)}% probability"
            )
        else:
            reasons.insert(
                0,
                f"ML model classified this email as safe with {round((1 - fraud_probability) * 100, 2)}% confidence"
            )

        if not reasons:
            reasons.append("No strong suspicious pattern detected by the trained ML model")

        return reasons[:8]