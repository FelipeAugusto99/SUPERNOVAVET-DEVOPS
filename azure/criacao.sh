#!/bin/bash

# ============================================================
# SUPERNOVA VET - CRIACAO DA INFRAESTRUTURA AZURE
# Arquitetura: ACR + ACI
# ============================================================

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
echo "Criando Resource Group..."

az group create \
  --name $RESOURCE_GROUP \
  --location $LOCATION

echo ""
echo "Registrando provider Microsoft.ContainerInstance..."

az provider register \
  --namespace Microsoft.ContainerInstance

echo ""
echo "Criando Azure Container Registry..."

az acr create \
  --resource-group $RESOURCE_GROUP \
  --name $ACR_NAME \
  --sku Basic \
  --location $LOCATION

echo ""
echo "Realizando login no ACR..."

az acr login \
  --name $ACR_NAME

echo ""
echo "Gerando imagem Docker da API..."

docker build \
  -t $IMAGE_NAME .

echo ""
echo "Criando tag da imagem..."

docker tag \
  "$IMAGE_NAME:latest" \
  "$ACR_SERVER/$IMAGE_NAME:$IMAGE_TAG"

echo ""
echo "Enviando imagem para o ACR..."

docker push \
  "$ACR_SERVER/$IMAGE_NAME:$IMAGE_TAG"

echo ""
echo "Criando PostgreSQL no Azure Container Instances..."

az container create \
  --resource-group $RESOURCE_GROUP \
  --name $DB_CONTAINER \
  --image postgres:16 \
  --location $LOCATION \
  --os-type Linux \
  --cpu 1 \
  --memory 1.5 \
  --ports 5432 \
  --ip-address Public \
  --dns-name-label $DB_DNS \
  --environment-variables \
    POSTGRES_DB=supernova \
    POSTGRES_USER=postgres \
  --secure-environment-variables \
    POSTGRES_PASSWORD=$DB_PASSWORD

echo ""
echo "Habilitando credenciais administrativas do ACR..."

az acr update \
  --name $ACR_NAME \
  --admin-enabled true

echo ""
echo "Obtendo credenciais do ACR..."

ACR_USERNAME=$(az acr credential show \
  --name $ACR_NAME \
  --query username \
  --output tsv)

ACR_PASSWORD=$(az acr credential show \
  --name $ACR_NAME \
  --query "passwords[0].value" \
  --output tsv)

echo ""
echo "Criando API no Azure Container Instances..."

az container create \
  --resource-group $RESOURCE_GROUP \
  --name $API_CONTAINER \
  --image "$ACR_SERVER/$IMAGE_NAME:$IMAGE_TAG" \
  --location $LOCATION \
  --os-type Linux \
  --cpu 1 \
  --memory 1.5 \
  --ports 8080 \
  --ip-address Public \
  --dns-name-label $API_DNS \
  --registry-login-server $ACR_SERVER \
  --registry-username $ACR_USERNAME \
  --registry-password $ACR_PASSWORD \
  --environment-variables \
    DB_HOST="$DB_DNS.$LOCATION.azurecontainer.io" \
    DB_PORT=5432 \
    DB_NAME=supernova \
    DB_USERNAME=postgres \
  --secure-environment-variables \
    DB_PASSWORD=$DB_PASSWORD

echo ""
echo "=========================================="
echo "STATUS DOS CONTAINERS"
echo "=========================================="

az container show \
  --resource-group $RESOURCE_GROUP \
  --name $DB_CONTAINER \
  --query "{nome:name,status:instanceView.state,fqdn:ipAddress.fqdn}" \
  --output table

az container show \
  --resource-group $RESOURCE_GROUP \
  --name $API_CONTAINER \
  --query "{nome:name,status:instanceView.state,fqdn:ipAddress.fqdn}" \
  --output table

echo ""
echo "Swagger:"
echo "http://$API_DNS.$LOCATION.azurecontainer.io:8080/swagger-ui/index.html"

echo ""
echo "Infraestrutura criada com sucesso."