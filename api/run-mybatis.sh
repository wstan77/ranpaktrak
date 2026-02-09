#!/bin/bash
# Fetch secrets from Azure Key Vault
# Pull VAULT_NAME with default
vault_name=$1
if [ -z "$vault_name" ]
then
    vault_name="ranpaktrak-secrets"
fi
echo "Using vault: $vault_name"
db_url=$(az keyvault secret show --name database-writer-url --vault-name $vault_name --query value -o tsv)
db_username=$(az keyvault secret show --name database-username --vault-name $vault_name --query value -o tsv)
db_password=$(az keyvault secret show --name database-password --vault-name $vault_name --query value -o tsv)

# Run Mybatis Generator with Maven
./mvnw resources:resources mybatis-generator:generate -Ddatabase.url=$db_url -Ddatabase.username=$db_username -Ddatabase.password=$db_password
