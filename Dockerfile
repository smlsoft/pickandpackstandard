FROM tomcat:8.5-jre8-alpine

#RUN echo "Asia/Bangkok" > /etc/timezone && dpkg-reconfigure -f noninteractive tzdata

RUN rm -rf $CATALINA_HOME/webapps/* && \
apk add --no-cache tzdata && \
cp /usr/share/zoneinfo/Asia/Bangkok /etc/localtime && \
echo "Asia/Bangkok" > /etc/timezone && apk del tzdata
RUN rm -rf $CATALINA_HOME/conf/server.xml
RUN rm -rf $CATALINA_HOME/webapps/ROOT/index.jsp
ADD ./dockerconfig/server.xml $CATALINA_HOME/conf/server.xml
ADD ./target/* $CATALINA_HOME/webapps/