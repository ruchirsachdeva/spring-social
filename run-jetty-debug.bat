set MAVEN_OPTS=-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=5000,server=y,suspend=n -Xms256m -Xmx512m -XX:MaxPermSize=256m
call mvn -e clean jetty:run %1