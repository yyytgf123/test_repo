@Library('jenkins-shared-lib@main') _

pipeline {
    agent any

    environment {
        GRADLE_USER_HOME = "/var/jenkins_home/.gradle"

        AWS_REGION     = "ap-northeast-2"
        AWS_ACCOUNT_ID = "291176052382"
        ECR_REGISTRY   = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

        IMAGE_TAG = "${BUILD_NUMBER}-${GIT_COMMIT[0..7]}"
        SLACK_CHANNEL = "#jenkins-alerts"

        ECS_CLUSTER = "courm-cluster-prod"
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    stages {

        /* ================= CI ================= */

        stage('CI') {
            stages {

                stage('Detect Changes') {
                    steps {
                        script {
                            CHANGED_SERVICES = getChangedServices() ?: []
                            echo "Changed services: ${CHANGED_SERVICES}"
                        }
                    }
                }

                stage('Gradle Build') {
                    when {
                        expression { CHANGED_SERVICES && !CHANGED_SERVICES.isEmpty() }
                    }
                    steps {
                        sh """
                          ./gradlew \
                          ${CHANGED_SERVICES.collect { ":service:${it}:bootJar" }.join(' ')} \
                          --no-daemon
                        """
                    }
                }

                stage('Unit Test') {
                    when {
                        expression { CHANGED_SERVICES && !CHANGED_SERVICES.isEmpty() }
                    }
                    steps {
                        sh """
                          ./gradlew \
                          ${CHANGED_SERVICES.collect { ":service:${it}:test" }.join(' ')} \
                          --no-daemon
                        """
                    }
                }

                // ✅ 병렬 제거 + 스테이지 이름 변경
                stage('Docker Build') {
                    when {
                        expression { CHANGED_SERVICES && !CHANGED_SERVICES.isEmpty() }
                    }
                    steps {
                        script {
                            CHANGED_SERVICES.each { svc ->
                                echo "Docker build: ${svc}"
                                buildDockerImage(svc)
                            }
                        }
                    }
                }
            }
        }

        /* ================= CD ================= */

        stage('CD') {
            when {
                allOf {
                    branch 'main'
                    expression { CHANGED_SERVICES && !CHANGED_SERVICES.isEmpty() }
                }
            }

            stages {

                stage('ECR Login') {
                    steps {
                        sh '''
                          aws ecr get-login-password --region $AWS_REGION \
                          | docker login --username AWS --password-stdin $ECR_REGISTRY
                        '''
                    }
                }

                // ✅ 병렬 제거
                stage('Push Images') {
                    steps {
                        script {
                            CHANGED_SERVICES.each { svc ->
                                echo "Push image: ${svc}"
                                pushImage(svc)
                            }
                        }
                    }
                }

                // ✅ 병렬 제거
                stage('Deploy ECS (Update Service)') {
                    steps {
                        script {
                            CHANGED_SERVICES.each { svc ->
                                echo "Deploy service: ${svc}"
                                deployService(serviceName: svc)
                            }
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            slackNotify(
                status: 'SUCCESS',
                channel: SLACK_CHANNEL,
                services: CHANGED_SERVICES
            )
        }
        failure {
            slackNotify(
                status: 'FAILURE',
                channel: SLACK_CHANNEL,
                services: CHANGED_SERVICES
            )
        }
        always {
            archiveArtifacts artifacts: 'trivy-reports/*.json', allowEmptyArchive: true
        }
    }
}
