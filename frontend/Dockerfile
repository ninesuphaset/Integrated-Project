FROM node:18 AS build-stage
WORKDIR /app
COPY . .
RUN npm install
RUN npm run build -- --base=/kp2/


FROM nginx:alpine
COPY --from=build-stage /app/dist /usr/share/nginx/html
COPY default.conf /etc/nginx/conf.d/default.conf