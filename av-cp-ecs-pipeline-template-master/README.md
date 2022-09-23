# av-cp-ecs-service-template

## How To

- Ensure you have Python3 installed.
- Install cookiecutter

```bash
pip install cookiecutter
```

## Use Template

- Make sure you know the location of the template, but do not switch directories to it.
- Run cookiecutter

```bash
cookiecutter av-cp-techpubs-ecs-cicd-template
```

- Complete the scaffolding questions. These are used when generating the template and will replace directory/file names.
- Questions:
  - product_prefix -> This is your product's name. e.g. av-cp-`<team>`
  - product_resource_identifier -> Sets the name of some AWS resources
  - aws_account_id -> Sets the aws account id e.g. 048421397550
  - env_non-prod_prod : non-prod or prod
  - ecs_pipeline_project -> This is the name of the template project for your team. Follows team naming standards.
  - project_kms_key_id -> Product's KMS key ID. This will get set in some IAM policies and .yaml files.
  - project_creator -> SSO of the person executing the cookiecutter process for this project. This gets used in various IAM tags.

Example:

```bash
ecs_pipeline_project [av-cp-techpubs-ecs-cicd-template]: av-cp-techpubs-ecs-pipeline-template
team_name [av-cp-techpubs]: av-cp-techpubs
project_creator [212409106]: 212409106
nonprod_kms_key_id [ez123456-5757-5dc4-9a67-1234bedf62c2] ef2c7853-4797-4fc4-9a67-9934bedf62c2
prod_kms_key_id [ez123456-5757-5dc4-9a67-1234bedf62c2] 7304d724-011c-483e-8612-2b36bdf485eb
team_resource_identifier [AvCpTechpubs]: AvCpTechpubs
```

You can then switch to your new project:

Example:

```bash
cd av-cp-techpubs-ecs-cicd-template
```

Then, `git init` and push to Github!
