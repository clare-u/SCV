import os

ENVIRONMENT = os.getenv("ENVIRONMENT", "development")

if ENVIRONMENT == "production":
    CORS_CONFIG = {
        "allow_origins": [
            "http://localhost:3000",
            "http://k11a107.p.ssafy.io",
            "https://k11a107.p.ssafy.io"
        ],
        "allow_credentials": True,
        "allow_methods": ["*"],
        "allow_headers": [
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "X-Requested-With"
        ],
        "expose_headers": ["Content-Length", "Content-Range"],
        "max_age": 3600
    }

else:
    CORS_CONFIG = {
        "allow_origins": [
            "http://localhost:3000",
            "http://localhost:8080",
            "http://localhost.tiangolo.com",
            "https://localhost.tiangolo.com"
        ],
        "allow_credentials": True,
        "allow_methods": ["*"],
        "allow_headers": ["*"]
    }
