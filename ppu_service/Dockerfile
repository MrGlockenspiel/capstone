FROM alpine:latest

RUN apk add --no-cache openjdk21-jdk openjdk21-jre make bash

WORKDIR /app

COPY src ./src
COPY Makefile ./

RUN make all

CMD ["make", "run"]
