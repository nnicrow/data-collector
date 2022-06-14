FROM gradle:jdk15 as builder
WORKDIR /app
COPY src ./src
COPY build.gradle.kts ./build.gradle.kts
RUN --mount=type=cache,target=./.gradle gradle clean test install