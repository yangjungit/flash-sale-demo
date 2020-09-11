FROM 172.16.33.69:5000/jdk8
VOLUME /tmp
COPY target/flash-sale-demo-0.0.1-SNAPSHOT.jar /opt/app/app.jar
COPY target/classes/application.yaml /opt/app/application.yaml
COPY target/classes/application-pro.yaml /opt/app/application-pro.yaml
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom -Duser.language=zh -Duser.region=CN -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai -Duser.country=CN -Do2o.profile=dev -Do2o.home=/opt/app -Xmx1024m -Xms1024m -Xmn512m -jar /opt/app/app.jar --spring.config.additional.location=application.yaml