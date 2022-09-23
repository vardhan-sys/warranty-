# av-cp-techpubs!

This code used for myGEAviation Document widget. Documents is a tool that allows users to view documents regarding their engines!
This code uses Aviation artifactory.  
To run this code:
1. Get access to Aviation artifactory.
    - You'll want to generate and copy an Artifactory API Key from your user settings.
    - Ensure your SSO is added to both the `av-docker-read`/`av-docker-write` groups within Artifactory.
2. [Install Docker](https://docs.docker.com/get-docker/) for your respective machine (Windows or Mac).
    - Set both "Web Server" and "Secure Web Server" to http://pitc-zscaler-americas-cincinnati3pr.proxy.corporate.ge.com/
    - Set the "Bypass Proxy" section to the following: `localhost,127.0.0.1,ge.com,av.ge.com,build.ge.com,gecdn.com`
3. Configure your machine's `.m2/settings.xml` file for maven.
   1. Particularly for the proxy settings and artifactory repos.
4. Log into Artifactory via CLI:
   1. `docker login -u <SSO> -p <ARTIFACTORY API KEY> av-docker.artifactory.av.ge.com`
   2. If this fails for any reason, confirm with Alex Cromer you have artifactory access.
5. If you don't already have one, generate a [Personal Access Token in GitHub](https://docs.github.com/en/github/authenticating-to-github/keeping-your-account-and-data-secure/creating-a-personal-access-token)
6. Set an environment variable for your SPRING_CLOUD_CONFIG_SERVER_GIT_PASSWORD. Add the following command to you `~/.bash_profile` file:    
   `export SPRING_CLOUD_CONFIG_SERVER_GIT_PASSWORD=<your token from step 3.>`
7. mvn clean install
8. `docker-compose up --build`
    - **Note for new M1 macs:** When before running `docker compose up`, in the `docker-compose.yaml` file, change `techpubs-config-service` image version from `1.3` to `1.4`. Make sure to not commit change.
    
These above steps should build and run a completely isolated docker-verse of all three necessary resources for local dev; Techpubs Service(s), local postgres database, and local configuration service. Any code change requires POM version to be incremented before deployment. This is required for ECS pipeline!


## Local Debugging

Once you have the services running locally, the next is to set up remote debugging our Techpubs docker container. Everything is setup for you within the docker container itself, but some configuration needs to be done within IntelliJ.
1. Open the Run/Debug configuration settings wtihin IntelliJ
2. Click the `+` symbol and find and select `Application`
3. Everything should be set correct by default, but if not, ensure the following sections are set.
    - `VM Options` --> `-Dspring.profiles.active=local`
      - If this section is not displaying, click the blue text that reads `Modify Options` and make sure the VM Options value is check marked.
      
Once the services are running, simply select that "configuration" you just created and click the `debug` button (with your break points set of course).
    

## How to run the AWS CloudFront signed cookies endpoint locally

av-cp-techpubs has an endpoint that generates a response that includes three signed cookies. These cookies are necessary to allow users to download dvds via an AWS CloudFront distribution.  In order to successfully interact with this endpoint locally (i.e using the local application.properties file) you need to ensure the following has been completed:

1. Install [GOSAMMER](https://gehosting.io/docs/identity/gossamer3)

2. Ensure you have access to the `av-cp-techpubs-console` IAM role for the aviation-dss-nonprod VPC.  If you don't have this role:  
   - Refer AWS Console Access section.

3. Set an environment variable for your SSO.  Add the following command to you `~/.bash_profile` file:    
   `export SSO=<your sso>`

4. Set an environment variable for your SSO password.  Add the following command to you `~/.bash_profile` file::
   `export SSO_PASSWORD=<your sso password>`

These environment variables are needed for the AWS SDK proxy which allows you to interact with AWS Secrets Manager. If   
you are running locally the Secrets Manager client will utilize your sso and sso password to set the java SDK's proxy.   
Without these settings you will receive a connection timeout error.  You will now be able to hit this endpoint and see   
that the three cookies are properly set.
  
--  

**Note** - After you complete the steps above you will only be able to hit the CloudFront endpoint   
from postman or the UI.  This will give you the capability to check that the cookies are created,   
however, you won't be able to download a dvd via the UI due to a few factors.
1. Cookies are `secure` which means they can only be transferred over a secure connection (SSL).
2. The CloudFront Distribution only accepts traffic from HTTPS requests.
3. The AWS WAF that sits in front of CloudFront only accepts traffic from requests that have referrer   
   header from our site (https://dev.my.geaviation.com or https://dev.my.gehonda.com)

## Importing and configuring your project in IntelliJ
1.  Open **IntelliJ**
2.  Click **Import Project**
3.  Browse to your workspace folder and select the service repo that you cloned and click **Open**
4.  Select **Maven** and click **Next**
5.  Check **Search for projects recursively** and **Import Maven projects automatically** and click **Next**
6.  Verify both **sonar-coverage** and **mygeaspring** are checked and click **Next**
7.  Click the **Select All** button to include all Maven projects and click **Next**
8.  Verify that the **1.8** SDK is selected and that the JDK home path are the same as what was installed during [Step 2: Developer Laptop Setup](https://devcloud.swcoe.ge.com/devspace/pages/viewpage.action?pageId=1413813832) and click **Next**
9.  Verify the Project name and Project file location and click **Next**
10. After IntelliJ has resolved its dependencies click **Cancel** when adding vcs.xml to GIT; We ignore all files in the .idea folder
11. Click `Run > Edit Configurations`
12. Click the **plus** above Templates
13. Select  **Application**
14. In **Name**, enter  `TechPubs`
15. In **Main Class**, enter `com.geaviation.techpubs.TechpubsServicesSBApp`
16. In **VM options**, enter `-Dspring.profiles.active=local`
17. In the **JRE** drop-down, select `1.8`
18. Click **OK**

## AWS Console Access
Link to AWS Console: http://sc.ge.com/*awsentlogin
1. Go to the IDM (http://idm.ge.com) > Manage My Groups > Join a Distribution List **- open in a new tab so you don't lose this page**
2. Search for `@GE AWS_av-cp-techpubs-console_048421397550`  for NON-PROD and `@GE AWS_av-cp-techpubs-console_850200683887` for PROD.
3. Click Join
4. Once approved, use the AWS console to select `av-cp-techpubs-console` and sign in

## How to sync a folder from S3
1. Set proxies
    - From the command line: `set https_proxy=http://pitc-zscaler-americas-cincinnati3pr.proxy.corporate.ge.com:80`

2. Configure gossamer and then run it to get an access token to AWS.<br>
    - set proxy up<br> `proxify`
    - Run login command<br> `gossamer3 login`

3. Create a directory and sync the folder
    - Create a folder  `data4` in `C:/Users/YOUR_SSO/Documents/`
    - Within `data4`, create a folder called `techpubs`
    - Navigate to the `techpubs` folder in a command line.
    - Run the following command to sync a folder from the bucket: `aws s3 sync s3://tech-pubs-dev/<GEK eg. gek108786>/<VERSION eg. 9.5> . --no-verify-ssl`

4.  Sync the corresponding `targetshadow` folder.
    - Create a directory within `C:\Users\YOUR_SSO\Documents\data4\techpubs` named`targetshadow`
    -  Navigate to the `targetshadow` folder in the command line and run the following command: `aws s3 sync s3://tech-pubs-dev/targetshadow/<GEK> . --no-verify-ssl`


## Setting up Postman
#### Import the TechPubs Collection
1.Download [TechPubs Postman Collections](https://github.build.ge.com/Portnostics/psvc-tpubs/tree/master/src/test/resources/postman)
2. Open **Postman**
3. Select **File > Import** and import the desired collection
4. Verify that TechPubs requests are listed in the **Collections** tab on the left.

#### Configure Local Environment Settings
5. Click the gear icon in the top right and click **Add** on the popup.
6.  In **Environment Name**, enter `local`
7.  Create the following variable
    - Variable: `host`
    - Initial Value: `http://localhost:8080`
    - Current Value: `http://localhost:8080`
        - Variable: `sm_ssoid`
          - Initial Value: `<your SSO>`
          - Current Value: `<your SSO>`
8. Click **Add**
9. Select **Local** from the Environment dropdown in the top right.

### Configure QA Environment Settings
5. Click the gear icon in the top right and click **Add** on the popup.
6.  In **Environment Name**, enter `QA`
7.  Create the following variables
    - Variable: `host`
      - Initial Value: `http://qa-psvc.av.ge.com`
      - Current Value: `http://qa-psvc.av.ge.com'`
    - Variable: `sm_ssoid`
      - Initial Value: `<your SSO>`
      - Current Value: `<your SSO>`
8. Click **Add**
9. Select **Local** from the Environment dropdown in the top right.

#### Configure Header Presets
10. Click **+** to open a new request.
11. Click the **Headers** tab under the URL bar, click **Presets > Manage Presets** using the dropdown on the right, and click **Add**
12. Enter `GE` in **Header Preset Name**
13. Add the following entries to the table:

| KEY | VALUE |
|--|--|
| SM_SSOID | <YOUR_SSO> |
| portal_id | CWC |

14. Click **Add** to save.
15. When you create new requests or make requests from the collection, make sure to set your presets using the Presets dropdown.
