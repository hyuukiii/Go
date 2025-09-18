#!/bin/bash

echo "🚀 프로젝트 전체 수정 시작..."

# 1. Import 문 수정
echo "📋 Import 문 수정 중..."
find src/main/java -name "*.java" -type f -exec sed -i '' \
  -e 's/import Product\./import main.java.Product./g' \
  -e 's/import User\./import main.java.User./g' \
  -e 's/import BidsTransaction\./import main.java.BidsTransaction./g' \
  -e 's/import Notification\./import main.java.Notification./g' \
  -e 's/import Interest\./import main.java.Interest./g' \
  -e 's/import Mypage\./import main.java.Mypage./g' \
  -e 's/import Review\./import main.java.Review./g' \
  -e 's/import Ranking\./import main.java.Ranking./g' \
  -e 's/import Mainpage\./import main.java.Mainpage./g' \
  {} \;

# 2. DB URL 수정
echo "🔧 DB URL 수정 중..."
find src -name "*.java" -exec sed -i '' 's/1521:testdb/1521:xe/g' {} \;

# 3. Maven 재빌드
echo "🔨 Maven 빌드 중..."
mvn clean compile -U

echo "✅ 수정 완료!"
