# 1. 使用官方 OpenJDK 17 作为基础镜像
FROM openjdk:17-jdk-slim

# 2. 设定工作目录
WORKDIR /app

# 3. 复制 Spring Boot JAR 包（假设 `target` 目录下生成的 JAR 包）
COPY target/*.jar app.jar

# 4. 运行 Spring Boot 应用
CMD ["java", "-jar", "app.jar"]

# 5. 暴露 4000 端口
EXPOSE 4000
