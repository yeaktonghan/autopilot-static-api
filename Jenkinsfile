pipeline {
  agent {
    node {
      label 'worker1'
    }
  }
  tools {
    gradle 'gradle'
  }
  environment {
    CURRENT_DATETIME = new Date().format("yyyy-MM-dd-HH-mm-ss")
    TELEGRAM_BOT_TOKEN = credentials('telegramToken')
    TELEGRAM_CHAT_ID = credentials('telegramChatid')
  }
  stages {
    stage('Build to Docker Images') {
      steps {
        script {
          sh 'gradle build'
          sh 'ls ./build'
          sh 'docker build -t kshrdautopilot/autopilot-api:${CURRENT_DATETIME} .'
          sh 'docker push kshrdautopilot/autopilot-api:${CURRENT_DATETIME}'
          echo "Build images successfully"
          sendTelegramMessage("✅ Build to Docker Images stage passed successfully 🚀.")
        }
      }
      post {
        failure {
          script {
            sendTelegramMessage("❌ Build to Docker Images stage failed.")
          }
        }
      }
    }

    stage('Go to Repository') {
      steps {
        script {
          checkout([$class: 'GitSCM',
            branches: [
              [name: '*/main']
            ],
            doGenerateSubmoduleConfigurations: false,
            extensions: [
              [$class: 'CleanBeforeCheckout'],
              [$class: 'CloneOption', depth: 0, noTags: false, reference: '', shallow: true]
            ],
            submoduleCfg: [],
            userRemoteConfigs: [
              [url: "https://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@github.com/ksga-11th-generation-advance-course/auto-pilot-infra.git"]
            ]
          ])
          sendTelegramMessage("✅ Go to Repository stage passed successfully.")
        }
      }
      post {
        failure {
          script {
            sendTelegramMessage("❌ Go to Repository stage failed.")
          }
        }
      }
    }

    stage('Modify File') {
      steps {
        script {
          sh "sed -i 's+kshrdautopilot/autopilot-api.*+kshrdautopilot/autopilot-api:${CURRENT_DATETIME}+g' app/deployment-api.yaml"
          sh "cat app/deployment-api.yaml"
          sendTelegramMessage("✅ Modify File stage passed successfully.")
        }
      }
      post {
        failure {
          script {
            sendTelegramMessage("❌ Modify File stage failed.")
          }
        }
      }
    }

    stage('Commit and Push') {
      steps {
        script {
          sh 'git add .'
          sh 'git commit -m "Updated images version" || true'
          sh 'git push https://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@github.com/ksga-11th-generation-advance-course/auto-pilot-infra.git HEAD:main'
          sendTelegramMessage("✅ Commit and Push stage passed successfully.")
        }
      }
      post {
        failure {
          script {
            sendTelegramMessage("❌ Commit and Push stage failed.")
          }
        }
      }
    }
  }

  // Function to send Telegram message
  def sendTelegramMessage(message) {
    script {
      def apiUrl = "https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/sendMessage"
      def curlCommand = "curl -s -X POST $apiUrl -d chat_id=${TELEGRAM_CHAT_ID} -d text='${message}'"

      sh curlCommand
    }
  }
}
