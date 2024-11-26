pipeline {
    agent any

    environment {
        DOCKER_COMPOSE_FILE = 'docker-compose.yaml'
        SERVER_PORT = '8081'
    }

    stages {
        stage('Tests') {
            steps {
                script {
                    // Проверка пользователя
                    sh 'whoami'
                }
            }
        }


        stage('Clone Repository') {
            steps {
                git branch: 'master', url: 'https://github.com/PavelGaponenko/spring-keycloak-demo.git' // Укажите ваш репозиторий
            }
        }

        stage('Start Keycloak with Docker Compose') {
            steps {
                script {
                    // Поднимаем Keycloak
                    sh 'sudo docker compose -f $DOCKER_COMPOSE_FILE down'
                    sh 'sudo docker compose -f $DOCKER_COMPOSE_FILE up -d'
                }
            }
        }

        stage('Build Java Project') {
            steps {
                script {
                    // Сборка jar-файла
                    sh './gradlew clean bootJar'
                }
            }
        }

        stage('Deploy Application') {
            steps {
                script {
                    // Убиваем предыдущее приложение, если запущено
                    sh 'fuser -k $SERVER_PORT/tcp || true'

                    // Запуск нового jar-файла
                    sh 'java -jar build/libs/spring-keycloak-demo-1.0.0.jar'
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished'
        }
        success {
            echo 'Application successfully deployed!'
        }
        failure {
            echo 'Build or deployment failed. Check logs.'
        }
    }
}