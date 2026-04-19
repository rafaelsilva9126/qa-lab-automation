pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        BACKEND_PORT = '8081'
        BASE_URL = 'http://localhost:8081'
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
                    junit 'qa-backend/target/surefire-reports/*.xml'
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

        stage('Install API Test Dependencies') {
            steps {
                dir('qa-api-tests') {
                    sh 'npm ci'
                }
            }
        }

        stage('Start Backend') {
            steps {
                dir('qa-backend') {
                    sh '''
                        nohup java -jar target/*.jar --server.port=$BACKEND_PORT > backend.log 2>&1 &
                        echo $! > backend.pid
                    '''
                }
            }
        }

        stage('Wait for Backend') {
            steps {
                sh '''
                    for i in {1..30}; do
                      if curl -sf $BASE_URL/users > /dev/null; then
                        echo "Backend is up!"
                        exit 0
                      fi
                      echo "Waiting for backend on $BASE_URL ..."
                      sleep 2
                    done
                    echo "Backend did not start in time"
                    exit 1
                '''
            }
        }

        stage('Run API Tests') {
            steps {
                dir('qa-api-tests') {
                    sh '''
                        export BASE_URL=$BASE_URL
                        npx playwright test
                    '''
                }
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'qa-api-tests/test-results/*.xml'
            archiveArtifacts artifacts: 'qa-backend/backend.log', allowEmptyArchive: true
            archiveArtifacts artifacts: 'qa-api-tests/playwright-report/**', allowEmptyArchive: true
            archiveArtifacts artifacts: 'qa-api-tests/test-results/**', allowEmptyArchive: true

            script {
                if (fileExists('qa-backend/backend.pid')) {
                    sh '''
                        PID=$(cat qa-backend/backend.pid)
                        kill $PID || true
                    '''
                }
            }
        }
    }
}