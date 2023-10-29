FROM openjdk:8u191-jre-alpine3.9
# 设置时区为上海
RUN apk add tzdata && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone \
    && apk del tzdata

COPY ./vide-server.jar /tmp/vide-server.jar
EXPOSE 9002
ENTRYPOINT java -jar /tmp/vide-server.jar