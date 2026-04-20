pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        IMAGE_NAME = 'qa-backend'
        CONTAINER_NAME = 'qa-backend-container'
        NETWORK_NAME = 'qa-network'
        API_BASE_URL = 'http://qa-backend-container:8081'
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
                sh '''
                    docker build -t $IMAGE_NAME ./qa-backend
                '''
            }
        }

        stage('Ensure Docker Network') {
            steps {
                sh '''
                    docker network inspect $NETWORK_NAME >/dev/null 2>&1 || docker network create $NETWORK_NAME
                '''
            }
        }

        stage('Start Backend Container') {
            steps {
                sh '''
                    docker rm -f $CONTAINER_NAME || true

                    docker run -d \
                      --name $CONTAINER_NAME \
                      --network $NETWORK_NAME \
                      -e PORT=8081 \
                      $IMAGE_NAME
                '''
            }
        }

        stage('Wait for Backend') {
            steps {
                sh '''
                    echo "Waiting on $API_BASE_URL"

                    for i in $(seq 1 60); do
                      if curl -sf "$API_BASE_URL/users" > /dev/null; then
                        echo "Backend is up"
                        exit 0
                      fi
                      echo "Waiting... $i"
                      sleep 2
                    done

                    echo "Backend failed to start"
                    docker logs $CONTAINER_NAME || true
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
                        sh '''
                            echo "Running tests against $BASE_URL"
                            npx playwright test
                        '''
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
            sh '''
                echo "=== CONTAINER LOGS ==="
                docker logs $CONTAINER_NAME || true

                echo "=== CLEANUP ==="
                docker rm -f $CONTAINER_NAME || true
            '''

            archiveArtifacts artifacts: 'qa-api-tests/playwright-report/**', allowEmptyArchive: true
            archiveArtifacts artifacts: 'qa-api-tests/test-results/**', allowEmptyArchive: true

            publishHTML([
                allowMissing: true,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'qa-api-tests/playwright-report',
                reportFiles: 'index.html',
                reportName: 'Playwright Report'
            ])
        }
    }
}