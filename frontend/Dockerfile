FROM node:18-alpine

WORKDIR /frontend_app

EXPOSE 5173

COPY package.json yarn.lock ./

RUN yarn install

COPY . .

CMD ["yarn", "dev"]
