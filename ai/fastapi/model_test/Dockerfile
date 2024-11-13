# 베이스 이미지
FROM bley1217/scv-fast:latest

# 작업 디렉토리 설정
WORKDIR /app

# 시스템 패키지 업데이트 및 캐시 정리
RUN apt-get update && \
    apt-get install -y --no-install-recommends && \
    rm -rf /var/lib/apt/lists/*

# 의존성을 먼저 설치하여 Docker 레이어 캐시 활용
COPY requirements.txt .

RUN --mount=type=cache,target=/root/.cache/pip \
    pip install -r requirements.txt --no-cache-dir

# 소스 코드 복사
COPY . .

# 포트 설정
EXPOSE 8002

# FastAPI 애플리케이션 실행
CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8002"]