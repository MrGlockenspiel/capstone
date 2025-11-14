FROM node:18.0.0-alpine

ENV NODE_ENV production

WORKDIR /opt/app

COPY package.json .
COPY index.js .

EXPOSE 8080

RUN npm i

CMD node index.js