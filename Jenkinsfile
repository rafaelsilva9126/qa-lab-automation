pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        BACKEND_PORT = '8081'
        API_BASE_URL = 'http://localhost:8081'
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
                        echo "=== START BACKEND ==="
                        ls -la target

                        JAR_FILE=$(ls target/*.jar | grep -v '.original' | head -n 1)
                        echo "Using jar: $JAR_FILE"

                        nohup env JENKINS_NODE_COOKIE=dontKillMe \
                          java -jar "$JAR_FILE" --server.port=$BACKEND_PORT \
                          > backend.log 2>&1 < /dev/null &

                        echo $! > backend.pid

                        echo "Backend PID: $(cat backend.pid)"
                        sleep 8

                        echo "=== INITIAL BACKEND LOG ==="
                        cat backend.log || true

                        echo "=== PROCESS CHECK ==="
                        ps -p $(cat backend.pid) -o pid,cmd || true
                    '''
                }
            }
        }

        stage('Wait for Backend') {
            steps {
                sh '''
                    echo "=== WAIT FOR BACKEND ==="
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
                    exit 1
                '''
            }
        }

        stage('Run API Tests') {
            steps {
                dir('qa-api-tests') {
                    withEnv(["BASE_URL=${API_BASE_URL}"]) {
                        sh '''
                            echo "=== RUN API TESTS ==="
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
            script {
                if (fileExists('qa-backend/backend.log')) {
                    echo '=== FINAL BACKEND LOG ==='
                    sh 'cat qa-backend/backend.log || true'
                }

                if (fileExists('qa-backend/backend.pid')) {
                    sh '''
                        PID=$(cat qa-backend/backend.pid)
                        echo "Stopping backend PID: $PID"
                        kill $PID || true
                    '''
                }
            }

            archiveArtifacts artifacts: 'qa-backend/backend.log', allowEmptyArchive: true
            archiveArtifacts artifacts: 'qa-api-tests/playwright-report/**', allowEmptyArchive: true
            archiveArtifacts artifacts: 'qa-api-tests/test-results/**', allowEmptyArchive: true
        }
    }
}