FROM stngo/myoraclejava
WORKDIR /app/
RUN apt-get update && apt-get install -y net-tools iputils-ping telnetd dnsutils
COPY ./dist/historical-app ./historical
WORKDIR /app/historical/
RUN chmod +x *.sh
CMD ./start-historical-app.sh

EXPOSE 8888