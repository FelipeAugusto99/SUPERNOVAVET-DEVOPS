#!/bin/bash

# ============================================================
# SUPERNOVA VET - REMOCAO DA INFRAESTRUTURA AZURE
# ============================================================

RESOURCE_GROUP="rg-supernovavet-devops"

echo "=========================================="
echo "SUPERNOVA VET - REMOCAO DA INFRAESTRUTURA"
echo "=========================================="

echo ""
echo "Removendo Resource Group: $RESOURCE_GROUP"

az group delete \
  --name $RESOURCE_GROUP \
  --yes \
  --no-wait

echo ""
echo "Solicitacao de exclusao enviada."
echo "Todos os recursos do projeto serao removidos junto com o Resource Group."