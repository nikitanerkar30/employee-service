
@Library('jenkins-shared-library') _


pipeline {


agent any


tools {

maven 'Maven-3.9.9'

}


environment {


APP_NAME='Employee Service'

IMAGE_NAME='nikitaharesh/employee-service'

CONTAINER_NAME='employee-service'

LATEST_TAG='latest'


}



stages {


stage('Checkout'){

steps{

checkoutCode()

}

}



stage('Git Information'){

steps{

displayGitInfo()

}

}



stage('Build'){

steps{

buildMaven()

}

}



stage('Docker Build'){

steps{

dockerBuild()

}

}



stage('Docker Login'){

steps{

dockerLogin()

}

}



stage('Docker Push'){

steps{

dockerPush()

}

}



stage('Deploy'){


when{

branch 'main'

}


steps{

deployDocker()

}


}



stage('Verify'){


steps{

verifyDeployment()

}


}



stage('Summary'){


steps{

buildSummary()

}


}


}



}