from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.api.ml_controller import router as ml_router

app = FastAPI(
    title="Fraud Email Scanner ML Service",
    description="Machine Learning microservice for fraud email prediction",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:4200",
        "http://localhost:8082",
        "http://localhost:8080"
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(ml_router)


@app.get("/")
def root():
    return {
        "service": "Fraud Email Scanner ML Service",
        "status": "running",
        "docs": "/docs"
    }