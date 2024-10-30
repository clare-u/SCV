# 빌드 단계
FROM node:18-alpine AS build
WORKDIR /app

# 의존성 설치
COPY package.json yarn.lock ./
RUN yarn install

# 소스 코드 복사
COPY . .

# 빌드 전 의존성 재설치 (node_modules 상태 복구)
RUN yarn install

# 빌드 실행
RUN yarn build