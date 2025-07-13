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

# 로그 디렉토리 생성
LOG_DIR="/home/ubuntu/app/logs"
mkdir -p "$LOG_DIR"

# 현재 시간으로 로그 파일명 생성
LOG_FILE="$LOG_DIR/mykku-$(date +%Y%m%d_%H%M%S).log"

echo "Starting application with log file: $LOG_FILE"

sudo nohup java \
    -Dspring.profiles.active=dev \
    -Duser.timezone=Asia/Seoul \
    -Dserver.port=8080 \
    -Ddd.service=debate-timer \
    -Ddd.env=dev \
    -jar "$JAR_FILE" > "$LOG_FILE" 2>&1 &

echo "Application started. PID: $!"
echo "Log file: $LOG_FILE"
echo "To view logs: tail -f $LOG_FILE"
