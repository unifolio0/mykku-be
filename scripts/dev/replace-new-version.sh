#!/bin/bash

PID=$(lsof -t -i:8080)

# 프로세스 종료
if [ -z "$PID" ]; then
  echo "No process is using port 8080."
else
  echo "Killing process with PID: $PID"
  kill -15 "$PID"

  # 직전 명령(프로세스 종료 명령)이 정상 동작했는지 확인
  if [ $? -eq 0 ]; then
    echo "Process $PID terminated successfully."
  else
    echo "Failed to terminate process $PID."
  fi
fi

JAR_FILE=$(ls /home/ubuntu/app/*.jar | head -n 1)

echo "Starting application..."

sudo nohup java \
    -Dspring.profiles.active=dev \
    -Duser.timezone=Asia/Seoul \
    -Dserver.port=8080 \
    -Ddd.service=mykku \
    -Ddd.env=dev \
    -jar "$JAR_FILE" > /dev/null 2>&1 &

echo "Application started. PID: $!"
echo "Logs are managed by Logback: /home/ubuntu/app/logs/mykku.log"
echo "To view logs: tail -f /home/ubuntu/app/logs/mykku.log"
