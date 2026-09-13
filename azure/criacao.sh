#!/bin/bash

# ============================================================
# SUPERNOVA VET - CRIACAO DA INFRAESTRUTURA AZURE
# Arquitetura: ACR + ACI
# ============================================================

# Encerra o script caso algum comando retorne erro
set -e

RESOURCE_GROUP="rg-supernovavet-devops"
LOCATION="brazilsouth"

ACR_NAME="acrsupernovavet563982"
ACR_SERVER="$ACR_NAME.azurecr.io"

DB_CONTAINER="aci-supernovavet-db"
DB_DNS="supernovavet-db-563982"

API_CONTAINER="aci-supernovavet-api"
API_DNS="supernovavet-api-563982"

IMAGE_NAME="supernovavet-api"
IMAGE_TAG="v1"


echo "=========================================="
echo "SUPERNOVA VET - CRIACAO DA INFRAESTRUTURA"
echo "=========================================="

echo ""
echo "Digite a senha do PostgreSQL:"
read -s DB_PASSWORD

echo ""
echo "Digite a senha do usuario administrador da aplicacao:"
read -s ADMIN_PASSWORD

echo ""
echo "Digite a senha do usuario veterinario da aplicacao:"
read -s VET_PASSWORD

echo ""
echo ""


# ============================================================
# 1. RESOURCE GROUP
# ============================================================

echo "Criando Resource Group..."

az group create \
  --name "$RESOURCE_GROUP" \
  --location "$LOCATION" \
  --output table


# ============================================================
# 2. PROVIDER MICROSOFT.CONTAINERINSTANCE
# ============================================================

echo ""
echo "Registrando provider Microsoft.ContainerInstance..."

az provider register \
  --namespace Microsoft.ContainerInstance

echo ""
echo "Aguardando registro do provider..."

while true
do
  PROVIDER_STATUS=$(az provider show \
    --namespace Microsoft.ContainerInstance \
    --query registrationState \
    --output tsv)

  echo "Status: $PROVIDER_STATUS"

  if [ "$PROVIDER_STATUS" = "Registered" ]; then
    break
  fi

  sleep 10
done


# ============================================================
# 3. AZURE CONTAINER REGISTRY
# ============================================================

echo ""
echo "Criando Azure Container Registry..."

az acr create \
  --resource-group "$RESOURCE_GROUP" \
  --name "$ACR_NAME" \
  --sku Basic \
  --location "$LOCATION" \
  --output table


# ============================================================
# 4. LOGIN NO ACR
# ============================================================

echo ""
echo "Realizando login no ACR..."

az acr login \
  --name "$ACR_NAME"


# ============================================================
# 5. BUILD DA API
# ============================================================

echo ""
echo "Gerando imagem Docker da API..."

docker build \
  -t "$IMAGE_NAME" .


# ============================================================
# 6. TAG DA IMAGEM
# ============================================================

echo ""
echo "Criando tag da imagem..."

docker tag \
  "$IMAGE_NAME:latest" \
  "$ACR_SERVER/$IMAGE_NAME:$IMAGE_TAG"


# ============================================================
# 7. PUSH PARA O ACR
# ============================================================

echo ""
echo "Enviando imagem para o ACR..."

docker push \
  "$ACR_SERVER/$IMAGE_NAME:$IMAGE_TAG"


# ============================================================
# 8. POSTGRESQL NO ACI
# ============================================================

echo ""
echo "Criando PostgreSQL no Azure Container Instances..."

az container create \
  --resource-group "$RESOURCE_GROUP" \
  --name "$DB_CONTAINER" \
  --image postgres:16 \
  --location "$LOCATION" \
  --os-type Linux \
  --cpu 1 \
  --memory 1.5 \
  --ports 5432 \
  --ip-address Public \
  --dns-name-label "$DB_DNS" \
  --environment-variables \
    POSTGRES_DB=supernova \
    POSTGRES_USER=postgres \
  --secure-environment-variables \
    POSTGRES_PASSWORD="$DB_PASSWORD" \
  --output table


# ============================================================
# 9. AGUARDAR POSTGRESQL
# ============================================================

echo ""
echo "Aguardando PostgreSQL iniciar..."

while true
do
  DB_STATUS=$(az container show \
    --resource-group "$RESOURCE_GROUP" \
    --name "$DB_CONTAINER" \
    --query instanceView.state \
    --output tsv)

  echo "Status PostgreSQL: $DB_STATUS"

  if [ "$DB_STATUS" = "Running" ]; then
    break
  fi

  sleep 10
done


# ============================================================
# 10. CREDENCIAIS DO ACR
# ============================================================

echo ""
echo "Habilitando credenciais administrativas do ACR..."

az acr update \
  --name "$ACR_NAME" \
  --admin-enabled true \
  --output none

echo ""
echo "Obtendo credenciais do ACR..."

ACR_USERNAME=$(az acr credential show \
  --name "$ACR_NAME" \
  --query username \
  --output tsv)

ACR_PASSWORD=$(az acr credential show \
  --name "$ACR_NAME" \
  --query "passwords[0].value" \
  --output tsv)


# ============================================================
# 11. API NO ACI
# ============================================================

echo ""
echo "Criando API no Azure Container Instances..."

az container create \
  --resource-group "$RESOURCE_GROUP" \
  --name "$API_CONTAINER" \
  --image "$ACR_SERVER/$IMAGE_NAME:$IMAGE_TAG" \
  --location "$LOCATION" \
  --os-type Linux \
  --cpu 1 \
  --memory 1.5 \
  --ports 8080 \
  --ip-address Public \
  --dns-name-label "$API_DNS" \
  --registry-login-server "$ACR_SERVER" \
  --registry-username "$ACR_USERNAME" \
  --registry-password "$ACR_PASSWORD" \
  --environment-variables \
    DB_HOST="$DB_DNS.$LOCATION.azurecontainer.io" \
    DB_PORT=5432 \
    DB_NAME=supernova \
    DB_USERNAME=postgres \
  --secure-environment-variables \
    DB_PASSWORD="$DB_PASSWORD" \
    ADMIN_PASSWORD="$ADMIN_PASSWORD" \
    VET_PASSWORD="$VET_PASSWORD" \
  --output table


# ============================================================
# 12. AGUARDAR API
# ============================================================

echo ""
echo "Aguardando API iniciar..."

while true
do
  API_STATUS=$(az container show \
    --resource-group "$RESOURCE_GROUP" \
    --name "$API_CONTAINER" \
    --query instanceView.state \
    --output tsv)

  echo "Status API: $API_STATUS"

  if [ "$API_STATUS" = "Running" ]; then
    break
  fi

  sleep 10
done


# ============================================================
# 13. STATUS FINAL
# ============================================================

echo ""
echo "=========================================="
echo "STATUS DOS CONTAINERS"
echo "=========================================="

echo ""
echo "PostgreSQL:"

az container show \
  --resource-group "$RESOURCE_GROUP" \
  --name "$DB_CONTAINER" \
  --query "{nome:name,status:instanceView.state,fqdn:ipAddress.fqdn}" \
  --output table

echo ""
echo "API:"

az container show \
  --resource-group "$RESOURCE_GROUP" \
  --name "$API_CONTAINER" \
  --query "{nome:name,status:instanceView.state,fqdn:ipAddress.fqdn}" \
  --output table


# ============================================================
# 14. ENDERECO DA APLICACAO
# ============================================================

echo ""
echo "=========================================="
echo "INFRAESTRUTURA CRIADA COM SUCESSO"
echo "=========================================="

echo ""
echo "Swagger:"
echo "http://$API_DNS.$LOCATION.azurecontainer.io:8080/swagger-ui/index.html"

echo ""
echo "Para visualizar os logs da API:"
echo "az container logs --resource-group $RESOURCE_GROUP --name $API_CONTAINER"

echo ""


# ============================================================
# 15. LIMPEZA DAS VARIAVEIS SENSIVEIS
# ============================================================

unset DB_PASSWORD
unset ADMIN_PASSWORD
unset VET_PASSWORD
unset ACR_PASSWORD