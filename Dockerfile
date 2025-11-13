# --- ETAPA 1: Construcción (Build) ---
# Usamos una imagen con Maven y Java 21 para compilar el proyecto
FROM maven:3.9-amazoncorretto-21 AS build
WORKDIR /app
COPY . .
# Compilamos generando el jar (saltando tests para rapidez en deploy)
RUN mvn clean package -DskipTests

# --- ETAPA 2: Ejecución (Runtime) ---
# Usamos tu imagen base preferida
FROM amazoncorretto:21-alpine-jdk
WORKDIR /app

# Instalamos herramientas necesarias para descomprimir la wallet
RUN apk add --no-cache unzip coreutils

# Copiamos el JAR generado en la etapa 1 (automáticamente busca el .jar)
COPY --from=build /app/target/*.jar app.jar

# Creamos el directorio donde vivirá la wallet
RUN mkdir -p /app/wallet

# --- SCRIPT DE INICIO ---
# Este script se ejecuta cada vez que arranca el contenedor:
# 1. Revisa si existe la variable WALLET_BASE64
# 2. Si existe, la decodifica y descomprime en /app/wallet
# 3. Lanza la aplicación apuntando TNS_ADMIN a esa carpeta
RUN echo '#!/bin/sh' > /app/entrypoint.sh && \
    echo 'echo "Iniciando configuración..."' >> /app/entrypoint.sh && \
    echo 'if [ -n "$WALLET_BASE64" ]; then' >> /app/entrypoint.sh && \
    echo '  echo "Decodificando Wallet Oracle..."' >> /app/entrypoint.sh && \
    echo '  echo $WALLET_BASE64 | base64 -d > /app/wallet/wallet.zip' >> /app/entrypoint.sh && \
    echo '  unzip -o /app/wallet/wallet.zip -d /app/wallet' >> /app/entrypoint.sh && \
    echo 'fi' >> /app/entrypoint.sh && \
    echo 'exec java -Djava.security.egd=file:/dev/./urandom -Doracle.net.tns_admin=/app/wallet -jar /app/app.jar' >> /app/entrypoint.sh && \
    chmod +x /app/entrypoint.sh

EXPOSE 8080

# Usamos el script como punto de entrada
ENTRYPOINT ["/app/entrypoint.sh"]