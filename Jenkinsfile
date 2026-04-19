pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        API_BASE_URL = 'http://localhost:8081'
        CONTAINER_NAME = 'qa-backend-container'
        IMAGE_NAME = 'qa-backend'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Unit Test - Backend') {
            steps {
                dir('qa-backend') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean test'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'qa-backend/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package Backend') {
            steps {
                dir('qa-backend') {
                    sh './mvnw clean package -DskipTests'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $IMAGE_NAME ./qa-backend'
            }
        }

        stage('Start Backend Container') {
            steps {
                sh '''
                    docker rm -f $CONTAINER_NAME || true
                    docker run -d \
                      --name $CONTAINER_NAME \
                      -p 8081:8081 \
                      -e PORT=8081 \
                      $IMAGE_NAME
                '''
            }
        }

        stage('Wait for Backend') {
            steps {
                sh '''
                    for i in $(seq 1 60); do
                      if curl -sf "$API_BASE_URL/users" > /dev/null; then
                        echo "Backend is up on $API_BASE_URL"
                        exit 0
                      fi
                      echo "Waiting for backend... attempt $i"
                      sleep 2
                    done
                    echo "Backend did not start in time"
                    exit 1
                '''
            }
        }

        stage('Install API Test Dependencies') {
            steps {
                dir('qa-api-tests') {
                    sh 'npm ci'
                }
            }
        }

        stage('Run API Tests') {
            steps {
                dir('qa-api-tests') {
                    withEnv(["BASE_URL=${API_BASE_URL}"]) {
                        sh 'npx playwright test'
                    }
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'qa-api-tests/test-results/*.xml'
                }
            }
        }
    }

    post {
        always {
            sh 'docker logs $CONTAINER_NAME || true'
            sh 'docker rm -f $CONTAINER_NAME || true'

            archiveArtifacts artifacts: 'qa-api-tests/playwright-report/**', allowEmptyArchive: true
            archiveArtifacts artifacts: 'qa-api-tests/test-results/**', allowEmptyArchive: true
        }
    }
}