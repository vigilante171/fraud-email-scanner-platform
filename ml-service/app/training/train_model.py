import os
import joblib
import pandas as pd

from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import accuracy_score, classification_report, confusion_matrix
from sklearn.model_selection import train_test_split


BASE_DIR = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

DATASET_PATH = os.path.join(BASE_DIR, "data", "phishing_email_dataset.csv")
MODEL_DIR = os.path.join(BASE_DIR, "app", "models")

MODEL_PATH = os.path.join(MODEL_DIR, "fraud_email_model.pkl")
VECTORIZER_PATH = os.path.join(MODEL_DIR, "tfidf_vectorizer.pkl")


def train_model():
    print("Loading dataset...")
    df = pd.read_csv(DATASET_PATH)

    if "text" not in df.columns or "label" not in df.columns:
        raise ValueError("Dataset must contain 'text' and 'label' columns.")

    df = df.dropna(subset=["text", "label"])
    df["text"] = df["text"].astype(str)
    df["label"] = df["label"].astype(str).str.lower().str.strip()

    df = df[df["label"].isin(["fraud", "safe"])]

    print(f"Dataset size: {len(df)} rows")
    print("\nLabel distribution:")
    print(df["label"].value_counts())

    x = df["text"]
    y = df["label"]

    x_train, x_test, y_train, y_test = train_test_split(
        x,
        y,
        test_size=0.2,
        random_state=42,
        stratify=y
    )

    print("\nVectorizing text...")
    vectorizer = TfidfVectorizer(
        lowercase=True,
        stop_words="english",
        ngram_range=(1, 2),
        min_df=2,
        max_df=0.95,
        max_features=30000
    )

    x_train_vectorized = vectorizer.fit_transform(x_train)
    x_test_vectorized = vectorizer.transform(x_test)

    print("Training Logistic Regression model...")
    model = LogisticRegression(
        max_iter=2000,
        class_weight="balanced",
        solver="liblinear"
    )

    model.fit(x_train_vectorized, y_train)

    print("\nEvaluating model...")
    y_pred = model.predict(x_test_vectorized)

    accuracy = accuracy_score(y_test, y_pred)

    print("\nAccuracy:", round(accuracy, 4))
    print("\nClassification Report:")
    print(classification_report(y_test, y_pred))
    print("\nConfusion Matrix:")
    print(confusion_matrix(y_test, y_pred))

    os.makedirs(MODEL_DIR, exist_ok=True)

    print("\nSaving model and vectorizer...")
    joblib.dump(model, MODEL_PATH)
    joblib.dump(vectorizer, VECTORIZER_PATH)

    print(f"\nModel saved to: {MODEL_PATH}")
    print(f"Vectorizer saved to: {VECTORIZER_PATH}")


if __name__ == "__main__":
    train_model()