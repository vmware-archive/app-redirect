Redirect is a [SpringBoot](https://github.com/spring-projects/spring-boot) app that issues redirects based on rules configured in urlrewrite.xml.
It uses [UrlRewriteFilter](https://code.google.com/p/urlrewritefilter) for redirects.

Usage:

> git clone git@github.com:pivotal/app-redirect.git
> cd app-redirect

make changes in src/main/resources/urlrewrite.xml

> ./gradlew clean build
> cd scripts
> cf login
> ./deploy-blue-green.sh production
