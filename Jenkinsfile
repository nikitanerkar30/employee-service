pipeline {

    agent any

    tools {
        maven 'Maven-3.9.9'
    }

    environment {
        APP_NAME       = 'Employee Service'
        IMAGE_NAME     = 'nikitaharesh/employee-service'
        LATEST_TAG     = 'latest'
        CONTAINER_NAME = 'employee-service'
    }

    stages {

        stage('Checkout Source') {
            steps {
                echo "===== CHECKOUT SOURCE CODE ====="
                checkout scm
            }
        }


        stage('Display Git Information') {
            steps {
                sh '''
                echo "Branch : $BRANCH_NAME"
                git log -1 --oneline
                git log -1 --pretty=format:"Author : %an"
                echo
                git log -1 --pretty=format:"Commit : %H"
                echo
                git log -1 --pretty=format:"Message : %s"
                '''
            }
        }


        stage('Build Application') {
            steps {
                sh '''
                echo "===== BUILDING APPLICATION ====="
                mvn clean package -DskipTests
                '''
            }
        }


        stage('SonarQube Code Analysis') {

            steps {

                echo "===== SONARQUBE ANALYSIS ====="

                withSonarQubeEnv('sonarqube') {

                    sh '''
                    mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
                    -Dsonar.projectKey=employee-service \
                    -Dsonar.projectName="Employee Service"
                    '''

                }

            }

        }


        stage('Quality Gate') {

            steps {

                timeout(time: 5, unit: 'MINUTES') {

                    waitForQualityGate abortPipeline: true

                }

            }

        }


        stage('Verify Artifact') {
            steps {
                sh '''
                echo "===== VERIFY ARTIFACT ====="
                ls -lh target
                '''
            }
        }


        stage('Build Docker Image') {
            steps {
                sh '''
                docker build -t ${IMAGE_NAME}:${BUILD_NUMBER} .
                '''
            }
        }


        stage('Docker Hub Login') {

            steps {

                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-credential',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )
                ]) {

                    sh '''
                    echo "$DOCKER_PASS" | docker login \
                    -u "$DOCKER_USER" \
                    --password-stdin
                    '''
                }
            }
        }


        stage('Push Version Image') {

            steps {

                sh '''
                docker push ${IMAGE_NAME}:${BUILD_NUMBER}
                '''

            }
        }


        stage('Tag Latest Image') {

            steps {

                sh '''
                docker tag ${IMAGE_NAME}:${BUILD_NUMBER} ${IMAGE_NAME}:${LATEST_TAG}
                docker push ${IMAGE_NAME}:${LATEST_TAG}
                '''

            }
        }


        stage('Remove Existing Container') {

            when {
                branch 'main'
            }

            steps {

                sh '''
                docker rm -f ${CONTAINER_NAME} || true
                '''

            }
        }


        stage('Deploy Container') {

            when {
                branch 'main'
            }

            steps {

                sh '''
                docker run -d \
                --name ${CONTAINER_NAME} \
                -p 8085:8080 \
                ${IMAGE_NAME}:${BUILD_NUMBER}
                '''

            }
        }


        stage('Verify Container') {

            when {
                branch 'main'
            }

            steps {

                sh '''
                docker ps
                '''

            }
        }

    }


    post {

        success {

            echo "Pipeline completed successfully."

        }


        failure {

            echo "Pipeline failed."

        }


        always {

            sh '''

            docker images | grep employee-service || true

            docker ps || true

            docker logout || true

            '''

            cleanWs()

        }

    }

}