#!/bin/bash
# 
# Fixed variables
GITHUB_ENDPOINT=https://github.build.ge.com/api/v3/
# 
# Get Argumments
for ARGUMENT in "$@"
do
    KEY=$(echo $ARGUMENT | cut -f1 -d=)
    VALUE=$(echo $ARGUMENT | cut -f2 -d=)  
    case "$KEY" in
            SONAR_EXCLUSIONS)    SONAR_EXCLUSIONS=${VALUE} ;;
            *)  
    esac
done
# 
# Derived variables
SONAR_EXCLUSIONS="${SONAR_EXCLUSIONS}, utilities/**/*, sonar.sh"
GITHUB_ORG_REPO_Name=$(echo $CODEBUILD_SOURCE_REPO_URL | cut -d "/" -f4-)
GITHUB_REPO_NAME=$(basename $CODEBUILD_SOURCE_REPO_URL .git)
PR_NUM=$(echo $CODEBUILD_SOURCE_VERSION | grep -o -E '[0-9]+')
# 
# Dependencies
mkdir utilities && cd utilities
curl -O https://github.com/stedolan/jq/releases/download/jq-1.6/jq-linux64
mv -v ./jq-linux64 ./jq && chmod +x ./jq
curl -O https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-3.3.0.1492-linux.zip
unzip sonar-scanner-cli-3.3.0.1492-linux.zip
cd ..
#

# SonarQube scan
if [ $CODEBUILD_WEBHOOK_EVENT = PULL_REQUEST_CREATED ] || [ $CODEBUILD_WEBHOOK_EVENT = PULL_REQUEST_REOPENED ] || [ $CODEBUILD_WEBHOOK_EVENT = PULL_REQUEST_UPDATED ]; then
    ./utilities/sonar-scanner-3.3.0.1492-linux/bin/sonar-scanner \
        -Dsonar.host.url=${SONAR_HOST_URL} \
        -Dsonar.sources='./' \
        -Dsonar.login=${SONAR_LOGIN_TOKEN} \
        -Dsonar.github.oauth=${SONAR_GITHUB_OAUTH} \
        -Dsonar.github.endpoint=${GITHUB_ENDPOINT} \
        -Dsonar.exclusions="${SONAR_EXCLUSIONS}" \
        -Dsonar.projectKey=${GITHUB_REPO_NAME} \
        -Dsonar.projectName=${GITHUB_REPO_NAME} \
        -Dsonar.projectVersion=${CODEBUILD_RESOLVED_SOURCE_VERSION} \
        -Dsonar.github.pullRequest=$PR_NUM \
        -Dsonar.github.repository=${GITHUB_ORG_REPO_Name%.*} \
        -Dsonar.analysis.mode=preview \
        -Dsonar.verbose=true

elif [ $CODEBUILD_WEBHOOK_EVENT = PUSH ] || [ $DEVITO = 'true' ]; then
    ./utilities/sonar-scanner-3.3.0.1492-linux/bin/sonar-scanner \
        -Dsonar.host.url=${SONAR_HOST_URL} \
        -Dsonar.sources='./' \
        -Dsonar.login=${SONAR_LOGIN_TOKEN} \
        -Dsonar.github.oauth=${SONAR_GITHUB_OAUTH} \
        -Dsonar.github.endpoint=${GITHUB_ENDPOINT} \
        -Dsonar.exclusions="${SONAR_EXCLUSIONS}" \
        -Dsonar.projectKey=${GITHUB_REPO_NAME} \
        -Dsonar.projectName=${GITHUB_REPO_NAME} \
        -Dsonar.projectVersion=${CODEBUILD_RESOLVED_SOURCE_VERSION} \
        -Dsonar.github.repository=${GITHUB_ORG_REPO_Name%.*} \
        -Dsonar.verbose=true
    # qualityGate=$(curl -u "${SONAR_LOGIN_TOKEN}:" --url "${SONAR_HOST_URL}api/qualitygates/project_status?projectKey=${GITHUB_REPO_NAME}" | ./utilities/jq -r .projectStatus.'status')
    # echo $qualityGate 
    SOURCE_VERSION=$(echo $CODEBUILD_RESOLVED_SOURCE_VERSION | cut -c 1-7)
    sed -i "s/gitCommitHash/$SOURCE_VERSION/" template.yaml
else
    echo "Invalid WebHook Event."
    exit 1
fi
# 
# Clean up
rm -rf utilities/
