var aws = require('aws-sdk');
var bluebird = require('bluebird')
var fs = require('fs')
var proxy = require('proxy-agent')
var versionCompare = require('compare-versions');
const { parseString } = require('xml2js');
global.Promise = bluebird

// Should be only necessary for local Development
// aws.config.update({
//     httpOptions: { agent: proxy('http://pitc-zscaler-americas-cincinnati3pr.proxy.corporate.ge.com') },
//     region: 'us-east-1'
// });

var ecsService = process.argv.slice(2)[0]

var serviceParams = {
    cluster: 'Portnostics-dev',
    services: [ecsService]
}
var taskDefinitionInfo = {
    taskDefinition: ''
}

const ecs = new aws.ECS();

//This function will gather the metadata about the 'local' (a.k.a repo files) and build an object of file metadata
function getLocalServiceInfo() {
    var xmlContent = fs.readFileSync('pom.xml', 'utf-8').trim()
    
    parseString(xmlContent, function(err, result) {
        var fileInfo = {
            name: result.project.name[0],
            version: result.project.version[0]
        }
        localFileMetadata = fileInfo
    })
    return localFileMetadata
}

function getRunningTaskDefInfo() {
    return ecs.describeServices(serviceParams)
        .promise()
        .then(function(data) {
            
            taskDefinitionInfo.taskDefinition = data.services[0].taskDefinition
            return taskDefinitionInfo
        })

}

function getDeployedImageVersionFromTaskDefinition(taskDef) {
    return ecs.describeTaskDefinition(taskDef)
        .promise()
        .then(data => {
            return {
                ...taskDefinitionInfo,
                deployedVersion: data.taskDefinition.containerDefinitions[0].image.split(':')[1].split('-')[0]
            }

        })
}

//Runs in order to get the version of the service from the local instance of the service.
getLocalServiceInfo()

function versionCheck(localObj, runningTaskDefObj) {
    console.log(`LOCAL`, localObj.version)
    console.log(`DEPLOYED`, runningTaskDefObj.deployedVersion)

    if(versionCompare.compare(localObj.version, runningTaskDefObj.deployedVersion, '<=')) {
        endBuildProcess()
    } else {
        console.log(`Version Check succesfully completed.`)
    }
}

function endBuildProcess() {
    console.log('==========================================================================================================================')
    console.error(`Version found in PR is either equal to or less than the version currently deplyed in ECS. Please version bump accordingly.`)
    console.log('==========================================================================================================================')
    process.exit(1)
}


getRunningTaskDefInfo()
    .then(runningTaskDefData => getDeployedImageVersionFromTaskDefinition(runningTaskDefData))
    .then(deployedVersionData => versionCheck(getLocalServiceInfo(), deployedVersionData))