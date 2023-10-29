FROM openjdk:8u191-jre-alpine3.9
# 设置时区为上海
RUN apk add tzdata && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone \
    && apk del tzdata

COPY ./video-server.jar /tmp/video-server.jar
EXPOSE 9002
ENTRYPOINT java -jar /tmp/video-server.jar