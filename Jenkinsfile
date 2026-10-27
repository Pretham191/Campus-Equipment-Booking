pipeline {
    agent any

    tools {
        jdk 'JDK17'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Source code is available in the Jenkins workspace.'
            }
        }

        stage('Check Java') {
            steps {
                sh 'java -version'
                sh 'javac -version'
                sh 'mvn -version'
            }
        }

        stage('Build and Test') {
            steps {
                sh 'mvn clean test'
            }
        }

        stage('Package Application') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t campus-equipment-booking:set-g .'
            }
        }
    }

    post {
        success {
            echo 'CI pipeline completed successfully!'
        }
        failure {
            echo 'CI pipeline failed. Check the stage logs.'
        }
        always {
            echo 'Pipeline execution finished.'
        }
    }
}