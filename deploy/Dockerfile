# Imagem utilizada pela pipeline de CD.
# O .jar ja chega compilado e testado pelo CI (artefato publicado no Azure DevOps).
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

RUN useradd -m appuser

COPY app.jar app.jar

RUN chown appuser:appuser app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
