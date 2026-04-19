pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        IMAGE_NAME = 'qa-backend'
        CONTAINER_NAME = 'qa-backend-container'
        API_BASE_URL = 'http://localhost:8081'
        API_PORT = '8081'
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

        stage('Start Backend Container') {
            steps {
                sh '''
                    docker rm -f $CONTAINER_NAME || true

                    docker run -d \
                      --name $CONTAINER_NAME \
                      -p $API_PORT:8081 \
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
                        echo "Backend is up on $API_BASE_URL"
                        exit 0
                      fi
                      echo "Waiting for backend... attempt $i"
                      sleep 2
                    done

                    echo "Backend did not start in time"
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

                echo "=== STOP AND REMOVE CONTAINER ==="
                docker rm -f $CONTAINER_NAME || true
            '''

            archiveArtifacts artifacts: 'qa-api-tests/playwright-report/**', allowEmptyArchive: true
            archiveArtifacts artifacts: 'qa-api-tests/test-results/**', allowEmptyArchive: true
        }
    }
}