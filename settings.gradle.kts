pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "EnglishApp"
include(":app")
include(":libs:di")
include(":libs:imageloader:api")
include(":libs:imageloader:coil")
include(":features:main:api")
include(":features:main:impl")
include(":features:addword:api")
include(":features:addword:impl")
include(":features:editword:api")
include(":features:editword:impl")
include(":features:wordsstudy:api")
include(":features:wordsstudy:impl")
