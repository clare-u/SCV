FROM python:3.12.4-slim

WORKDIR /app

RUN apt-get update && rm -rf /var/lib/apt/lists/* 

COPY . .
RUN --mount=type=cache,target=/root/.cache/pip \
    pip install -r requirements.txt --no-cache-dir
