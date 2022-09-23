# ECS build and deployment pipeline template. For use within Customer Portals space.

## Overview

This project houses the CloudFormation Template and it's dependencies for provisioning a full end-to-end CI/CD Pipeline in AWS for a CloudFormation-based project. It leverages the AWS Code* service offerings and builds a unique pipeline for each App that uses it as the Parent CloudFormation Template.  An example project that consumes this is [av-cp-techpubs-dummy-service](https://github.build.ge.com/Portnostics/av-cp-techpubs-dummy-service).

## Prerequisites

To get started with this, a few things steps must be completed first:
1. Install Required Command Lint Tools
   - `awscli`
   - `aws-sam-cli`
2. IAM Permissions Created in your Account
   - The necessary IAM Policies and respective roles they're attached to should be laid out for your within the `etc` directory under `IAM Needs`. If the project is setup correctly, each one is modified to have your team name inserted into the policy and role names. This can be used to create all the roles and policies, or as a guide to update your existing ones.
   - It is recommended to follow a _Just-in-time_ approach and only configure Prod Permissions when your first Pipeline needs to promote to Production.

## Onboarding Your Team

To start using this Template within your own team, the below steps should be taken.

___CAVEAT:___ By Using this Repo as a Template and Leveraging this Repo, __you are assuming Ownership and Support of your Pipeline Resources.__  Authors of this Repo are not responsible to support Pipelines generated from this Repo as a Template.

1. Generate your Team's version of this Pipeline Template using the `Use This Template` button next to the `Clone or Download` button
   - It is recommended to use a similar naming convention but replace the `av-cp-<Team>` prefix with your team's Application Prefix, i.e. `<prefix>-ecs-pipeline-template`
2. Check and Validate resource(s) naming convention against your team's naming conventions.
   - In the etc Directory:
     - Confrim all IAM policies/roles/ and resources meet your teams naming standards
     - Create a PR with the new policies or roles to be created (or updated if you have them within AWS.)
     - Create your CICD services s3 bucket by updating the parameter values set in the `etc/S3 Resources/samconfig.toml`
     - Confirm Default values are correct in `etc/S3 Resources/template.yaml`. Update as you see fit.
   

## Setup

With the Prerequisites and Onboarding steps completed above, your Pipeline Template should be ready to publish to Non-Prod!  To begin using your new Pipeline Template within your team for CloudFormation Deployments, follow the below steps:
1. `gossemer3` into your Team Console Role
2. Deploy the necessary supporting resources in Non-Prod
    ```
    cd etc/S3 Resources
    # sam deploy will use the samconfig.toml config
    # This will create an s3 bucket that will store all supporting template files and all artifacts that are built through this pipeline. 
    sam deploy
    ```
3. Deploy a Pipeline for your new template Repo to track and sync changes your team makes to your Pipeline Template
    ```
    cd /etc/Template-Repo-Monitoring
    # sam deploy will use the samconfig.toml config
    sam deploy -t setup-pipeline.yaml
    ```
4. Merge the changes you've made to this repo to Master (this kicks of build and pushes the actual pipeline template files to s3!)

## Usage

Your Pipeline Template is now available in your Non-Prod Account!  To use this new Pipeline in your CloudFormation-based Repos, create a Pipeline CloudFormation Script and BuildSpec to define your Build Steps and Deploy a Pipeline via CloudFormation.  Examples of these files can be found in this directory at `/example-usage`
- [nonprod-pipeline-usage-example.yaml](/example-usage/nonprod-pipeline-template.yaml)

## Local Development

This is a simple cloud formation template that gets copied to s3. There is nothing to run/debug locally.


## Pipeline
Right after creating this repository in Github the CICD pipeline was created using AWS's CodeBuild/CodeDeploy serivces. The Pipeline will listen and respond to changes in this repository by pulling down the code base and running the commands in the `buildspec.yaml`. The `buildspec.yaml` performs builds and tests the application, ultimately placing the code and resultant cloudformation files up in `s3` which then triggers CodeDeploy to pick up the code and cloudformation and perform a deploy. 

See the [Pipline Definition File](/etc/pipeline/pipeline.yaml)

In order to create the Code* Pipeline the following command was executed while being gossamer'd in as `av-cp-techpubs-console`:

```bash
aws cloudformation deploy --template-file prod-pipeline-usage-example.yaml --stack-name av-cp-techpubs-dummy-service-pipeline --role-arn arn:aws:iam::850200683887:role/av-cp-techpubs-cloudformation --capabilities CAPABILITY_AUTO_EXPAND
```
