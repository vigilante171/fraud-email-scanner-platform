import os
import pandas as pd
from datasets import load_dataset


BASE_DIR = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
DATA_DIR = os.path.join(BASE_DIR, "data")
OUTPUT_PATH = os.path.join(DATA_DIR, "phishing_email_dataset.csv")


def normalize_label(value):
    value_str = str(value).strip().lower()

    fraud_values = [
        "1",
        "phishing",
        "phishing email",
        "fraud",
        "fraud email",
        "malicious",
        "bad",
        "spam",
        "scam",
    ]

    safe_values = [
        "0",
        "safe",
        "safe email",
        "legitimate",
        "legitimate email",
        "ham",
        "benign",
        "normal",
    ]

    if value_str in fraud_values:
        return "fraud"

    if value_str in safe_values:
        return "safe"

    if "phishing" in value_str or "fraud" in value_str or "malicious" in value_str:
        return "fraud"

    if "safe" in value_str or "legitimate" in value_str or "ham" in value_str:
        return "safe"

    return "unknown"


def main():
    os.makedirs(DATA_DIR, exist_ok=True)

    print("Downloading dataset from Hugging Face...")
    dataset = load_dataset("zefang-liu/phishing-email-dataset")

    print(dataset)

    split_name = "train" if "train" in dataset else list(dataset.keys())[0]
    df = dataset[split_name].to_pandas()

    print("\nColumns found:")
    print(df.columns.tolist())

    print("\nRaw label distribution:")
    print(df["Email Type"].value_counts())

    clean_df = pd.DataFrame({
        "text": df["Email Text"].astype(str),
        "label": df["Email Type"].apply(normalize_label)
    })

    print("\nNormalized label distribution before filtering:")
    print(clean_df["label"].value_counts())

    clean_df = clean_df.dropna()
    clean_df = clean_df[clean_df["text"].str.strip() != ""]
    clean_df = clean_df[clean_df["label"].isin(["fraud", "safe"])]

    clean_df = clean_df.sample(frac=1, random_state=42).reset_index(drop=True)

    clean_df.to_csv(OUTPUT_PATH, index=False, encoding="utf-8")

    print(f"\nSaved cleaned dataset to: {OUTPUT_PATH}")
    print(f"Rows: {len(clean_df)}")
    print("\nFinal label distribution:")
    print(clean_df["label"].value_counts())


if __name__ == "__main__":
    main()