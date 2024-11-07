pluginManagement {
    repositories {
        maven("https://maven.aliyun.com/repository/gradle-plugin/")
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://maven.aliyun.com/repository/public/")
        maven("https://api.xposed.info/")
        google()
        mavenCentral()
    }
}

include(":app")
rootProject.name = "FuckBlued"

