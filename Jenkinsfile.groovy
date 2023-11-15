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
  }
  stages{
stage('build to docker images') {
    steps {
      script {
        sh 'docker build -t kshrdautopilot/autopilot-api:${CURRENT_DATETIME} .'
        sh 'docker push kshrdautopilot/autopilot-api:${CURRENT_DATETIME}'
        echo "build images successfully"
      }
    }
  }
  stage('go to repository') {

    steps {
      script {
        // Clone the GitHub repository
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
      }
    }
  }
  stage('Modify File') {
    steps {
      script {
        // Modify the content of the file
        sh "sed -i 's+kshrdautopilot/autopilot-api.*+kshrdautopilot/autopilot-api:${CURRENT_DATETIME}+g' app/deployment-api.yaml"
        sh "cat app/deployment-api.yaml"
      }
    }
  }
  stage('Commit and Push') {
    steps {
      script {
        // Commit the changes
        sh 'git add .'
        sh 'git commit -m "Updated images version" || true'

        // Push the changes back to GitHub
        sh 'git push https://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@github.com/ksga-11th-generation-advance-course/auto-pilot-infra.git HEAD:main'
      }
    }
  }
  }
}