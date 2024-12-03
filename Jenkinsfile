pipeline {
    agent any

    environment {
        JAR_NAME = 'build/libs/spring-keycloak-demo-1.0.0.jar'
        REMOTE_CONFIG = 'application'  // Имя конфигурации SSH-сервера в плагине
        TARGET_DIR = '/spring'    // Папка на сервере
    }

    stages {
        stage('Clone') {
            steps {
                git branch: 'master', url: 'https://github.com/PavelGaponenko/spring-keycloak-demo.git'
            }
        }

        stage('Tests') {
            steps {
                script {
                    sh './gradlew test'
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    sh './gradlew clean bootJar'
                }
            }
        }

        stage('Deploy') {
            steps {
                // Использование Publish Over SSH для копирования файла
                sshPublisher(
                    publishers: [
                        sshPublisherDesc(
                            configName: REMOTE_CONFIG,
                            transfers: [
                                sshTransfer(
                                    sourceFiles: "${JAR_NAME}", // Локальный путь
                                    remoteDirectory: TARGET_DIR             // Удаленная директория
                                )
                            ],
                            useWorkspaceInPromotion: true,
                            verbose: true
                        )
                    ]
                )

                // Execute commands
                sshPublisher(
                    publishers: [
                        sshPublisherDesc(
                            configName: REMOTE_CONFIG,
                            transfers: [
                                sshTransfer(
                                    execCommand: "sudo systemctl stop spring-keycloak-demo && sudo systemctl start spring-keycloak-demo"
                                )
                            ]
                        )
                    ]
                )
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished.'
        }
        success {
            echo 'Application successfully deployed!'
        }
        failure {
            echo 'Build or deployment failed. Check logs.'
        }
    }
}