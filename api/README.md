# RanpakTrak API

## Secrets - Azure Key Vault
Secrets are stored in Azure Key Vault.

Install the latest [Azure CLI](https://azcliprod.blob.core.windows.net/msi/azure-cli-2.51.0-x64.msi)

Login using `az login` with your LBi account.

Secrets should now be pulled when the app is run.

## Dev Server
Run `./start-dev-server.sh` to start the dev server. This will start the server on port 8080. For Windows users, you can use Git Bash or similar to run the script.

## Debug

The included .vscode/launch.json file contains a debug configuration for the server, connecting on port 5005.

After starting the server, open the Run and Debug tab in VS Code and run `Debug (Attach) - Remote`.

## Run Liquibase Scripts
`./run-liquibase.sh`

## Generate MyBatis Database Beans
`./run-mybatis`
