pipeline{
    agent any

    tools {
        maven 'Maven-3.9.9'
    }

    options{
        timestamps()
        disableConcurrentBuilds()

        buildDiscarder(logRotator(
            numToKeepStr: '20',
            artifactNumToKeepStr: '10'
        ))

        timeout(time:30, unit: 'MINUTES')
    }

    environment {

        APP_NAME = 'Employee Service'
        IMAGE_NAME = 'nikitaharesh/employee-service'
        CONTAINER_NAME = 'employee-service'
        LATEST_TAG   = 'latest'
    }


    parameters {

    choice(
        name: 'DEPLOY_ENV',
        choices: ['DEV', 'QA', 'UAT', 'PROD'],
        description: 'Select deployment environment'
    )

    booleanParam(
        name: 'DEPLOY',
        defaultValue: true,
        description: 'Deploy application?'
    )

    booleanParam(
        name: 'PUSH_IMAGE',
        defaultValue: true,
        description: 'Push Docker image to Docker Hub?'
    )

    booleanParam(
        name: 'RUN_SONAR',
        defaultValue: true,
        description: 'Run SonarQube analysis?'
    )

    string(
        name: 'CUSTOM_TAG',
        defaultValue: '',
        description: 'Optional custom Docker tag'
    )
}

    stages{
        stage('Checkout Source Code'){
            steps{
                echo "=====CHECKOUT SOURCE ======"
                checkout scm
            }
        }

        stage('Display Git Information'){
            steps {
                script {
                    env.GIT_COMMIT_SHORT= sh(
                        script: 'git rev-parse --short HEAD',
                        returnStdout: true

                    ).trim()

                    env.IMAGE_TAG = "${BUILD_NUMBER}-${GIT_COMMIT_SHORT}"

                }

                sh '''
                echo "=============GIT INFORMATION=========="
                echo "Branch  : $BRANCH_NAME"

                git log -1 --pretty=format:"Commit   :%H"
                echo
                git log -1 --pretty=format:"Author   :%an"
                echo

                git log -1 --pretty=format:"Messahe  :%s"
                echo


                echo "Short Commit: $GIT_COMMIT_SHORT"

                '''
            }
        }

        stage('Build Application'){
            steps{
                echo "=======BUILD APPLICATION======"

                sh'''
                mvn clean package
                '''
            }
        }

        stage('Publish Test Results'){
            steps{
                junit allowEmptyResults: true,
                     testResults: 'target/surefire-reports/*.xml'
            }
        }

        stage('Archive JAR Artifact'){

            steps{

                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true

                sh'''
                echo "=========TARGET========"
                ls -lh target
                '''
            }

        }

        stage('SonarQube Analysis'){
            when {
        expression {
            params.RUN_SONAR
             }
          }

            steps{
                echo "======SONARQUBE ANALYSIS======"

                withSonarQubeEnv('sonarqube'){
                sh'''
                mvn org.sonarsource.scanner.maven:sonar-maven-plugin:5.7.0.6970:sonar \
                -Dsonar.projectKey=employee-service \
                -Dsonar.projectName="Employee Service"
                '''
                }


            }
        }

        stage('Quality Gate'){

            steps{
                timeout(time: 5, unit: 'MINUTES'){

                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Build Docker Image'){
             steps{
                sh'''
                docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
                docker tag ${IMAGE_NAME}:${IMAGE_TAG}  ${IMAGE_NAME}:${LATEST_TAG}
                '''
             }
        }

        stage('Verify Docker Images'){

            steps{

                sh'''
                docker images | grep employee-service
                '''
            }


        }

        stage('Docker Hub Login'){
            steps{
                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credential',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]){
                    sh'''
                    echo "$DOCKER_PASS" | docker login \
                    -u "$DOCKER_USER" \
                    --password-stdin
                    '''
                }
            }
        }

        stage('Push Version Image'){

             when {
            expression {
                params.PUSH_IMAGE
            }
        }

            steps{
                retry(3){
                    sh'''
                    docker push ${IMAGE_NAME}:${IMAGE_TAG}
                    '''
                }
            }
        }

        stage('Push Latest Image'){
            steps{

                retry(3){
                  sh'''
                  docker push ${IMAGE_NAME}:${LATEST_TAG}
                  '''
                }
            }
        }

        stage('Remove Old Container'){

            when{
                branch 'main'
            }

            steps{

                sh'''
                docker rm -f ${CONTAINER_NAME} || true
                '''
            }

        }

        stage('Deploy Docker Container'){

          when {

            allOf {

                branch 'main'

                expression {
                    params.DEPLOY
                }
            }
          }


            steps{
                sh'''
                docker run -d \
                --name ${CONTAINER_NAME} \
                -p 8085:8080 \
                ${IMAGE_NAME}:${IMAGE_TAG}
                '''
            }
        }

        stage('Verify Deployment'){
            when {
                branch 'main'
            }

            steps {
                sh'''

                echo "========RUNNING CONTAINERS======"
                docker ps

                echo

                docker inspect ${CONTAINER_NAME}  \
                --format='{{.State.Status}}'
                '''
            }

        
        }

        stage ('Build Summary'){
            steps {

                echo "Deployment Environment : ${params.DEPLOY_ENV}"
                echo "Deploy Enabled         : ${params.DEPLOY}"
                echo "Push Docker Image      : ${params.PUSH_IMAGE}"
                echo "Run SonarQube          : ${params.RUN_SONAR}"
                echo "Custom Tag             : ${params.CUSTOM_TAG}"
                echo "========================================="

                sh '''
                echo ""
                echo "========================================="
                echo "Application     : ${APP_NAME}"
                echo "Branch          : ${BRANCH_NAME}"
                echo "Build Number    : ${BUILD_NUMBER}"
                echo "Git Commit      : ${GIT_COMMIT_SHORT}"
                echo "Docker Image    : ${IMAGE_NAME}:${IMAGE_TAG}"
                echo "Latest Tag      : ${IMAGE_NAME}:${LATEST_TAG}"
                echo "Workspace       : ${WORKSPACE}"
                echo "Node            : ${NODE_NAME}"
                echo "Build URL       : ${BUILD_URL}"
                echo "========================================="

                '''
            }
        }
    }

        post {

        success {

            echo "================================="
            echo "PIPELINE COMPLETED SUCCESSFULLY"
            echo "================================="

        }

        failure {

            echo "================================="
            echo "PIPELINE FAILED"
            echo "================================="

        }

        always {

            sh '''

            echo "========= DOCKER IMAGES ========="

            docker images | grep employee-service || true

            echo

            echo "========= RUNNING CONTAINERS ========="

            docker ps || true

            docker logout || true

            '''

            cleanWs()

        }

    }


}