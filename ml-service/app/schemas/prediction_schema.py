from pydantic import BaseModel, EmailStr, Field
from typing import List


class EmailPredictionRequest(BaseModel):
    sender: EmailStr
    subject: str = Field(..., min_length=1)
    body: str = Field(..., min_length=1)


class EmailPredictionResponse(BaseModel):
    fraudProbability: float
    prediction: str
    riskLevel: str
    reasons: List[str]
    modelVersion: str