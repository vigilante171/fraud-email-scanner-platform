from fastapi import APIRouter
from app.schemas.prediction_schema import EmailPredictionRequest, EmailPredictionResponse
from app.services.prediction_service import PredictionService

router = APIRouter(prefix="/api/ml", tags=["Machine Learning"])

prediction_service = PredictionService()


@router.get("/health")
def health_check():
    return {
        "status": "UP",
        "service": "ml-service",
        "modelVersion": prediction_service.MODEL_VERSION
    }


@router.post("/predict", response_model=EmailPredictionResponse)
def predict_email(request: EmailPredictionRequest):
    return prediction_service.predict(request)