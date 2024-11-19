# 빌드 단계
FROM node:18-alpine AS build
WORKDIR /app

# 안정적인 yarn 버전 설치
RUN npm install -g yarn

# 의존성 설치
COPY package.json yarn.lock ./
RUN yarn install

# 소스 코드 복사
COPY . .

# 빌드 전 의존성 재설치 (node_modules 상태 복구)
RUN yarn install

# 빌드 실행
RUN yarn build

# 실행 단계
FROM node:18-alpine
WORKDIR /app

# 빌드 결과물과 필요한 파일들만 복사
COPY --from=build /app/next.config.mjs ./
COPY --from=build /app/public ./public
COPY --from=build /app/.next ./.next
COPY --from=build /app/node_modules ./node_modules
COPY --from=build /app/package.json ./

# 포트 설정
EXPOSE 3000

# 실행 명령어 설정
CMD ["yarn", "start"]