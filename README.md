# Tiny Nginx

`Tiny Nginx` 는 [Nginx](https://www.nginx.com/) 를 모티브로 한 캐싱과 로드밸런싱 기능을 포함한 리버스 프록시 서버입니다.

본 프로젝트는 대규모의 트래픽을 처리하기 위하여 개발자들이 직면한 문제는 무엇인지, 그 문제점을 Nginx 는 어떻게 해결했는지에 대한 궁금증으로부터 시작되었습니다.

<br/>

## 프로젝트 목표

- 대규모의 트래픽의 분산 처리와 캐싱을 통해 부하를 줄이는 기능을 구현하는 것이 목표입니다.
- 단순한 기능 구현뿐만 아니라 직접 구현해보며 동작 원리를 이해하는 것이 목표입니다.
- 객체 지향 원리를 적용하여 유지 보수가 용이한 코드를 작성하는 것이 목표입니다.
- 효율적인 성능 테스트를 위한 CI/CD 파이프라인을 구축하여 자동화하는 것이 목표입니다.

<br/>

## 시스템 구성도

![Tiny Nginx Architecture](https://user-images.githubusercontent.com/51159167/176765439-af5f2257-30f6-4f52-92be-7b50245deb11.jpg)

- 빌드와 테스트를 자동화하여 개발 효율성을 높이기 위해 [CircleCi](https://circleci.com/) 를 활용하였습니다.
- 서버의 상태를 시각적으로 모니터링하기 위하여 [Prometheus](https://prometheus.io/) 와 [Grafana](https://grafana.com/) 를 활용였습니다.
- 부하 테스트를 통해 서버의 성능을 체크하기 위해 [Gatling](https://gatling.io/) 을 활용하였습니다. 이 도구는 CircleCi 및 Prometheus, Grafana 와의 높은 호환성에
  따라 [k6](https://k6.io/) 로 대체될 예정입니다.

<br/>

## 사용 기술 및 개발 환경

- Java11
- Maven
- Netty
- Google Cloud Platform(GCP)
- CircleCi
- Prometheus
- Grafana
- Gatling (k6로 대체될 예정)
- IntelliJ

<br/>

## Git Branch 전략

Workflow는 `feature`, `main`, `release`, `hotfix` 4가지의 브랜치로 나누어 작업하고 있고, 모든 브랜치에 대해 Pull Request를 통한 코드 리뷰 완료 후 Merge를 하고
있습니다.

> **Keyword**
> - `feature` : 기능의 구현을 담당하는 브랜치
> - `main` : 개발된 내용을 배포하기 위한 브랜치
> - `release` : 최종적으로 배포되는 브랜치
> - `hotfix` : 배포된 소스에서 버그가 발생하면 생성되는 브랜치

<br/>

브랜치 전략은 다음과 같습니다.

1. 구현할 기능을 Origin 레포지토리의 `feautre/{기능명}` 브랜치에서 개발을 하고 Commit log를 작성합니다.
2. 작업이 완료되면 Upstream 레포지토리의 `main` 브랜치에 Pull Request를 합니다.
3. 계획했던 기능이 모두 완성되고 이상이 없으면, `main` 브랜치에서 `release` 브랜치를 생성합니다.
4. `release` 브랜치에서 버그가 발생하면 `hotfix` 브랜치를 생성하여 수정합니다. 오류 수정이 완료되면 `release` 브랜치와 `main` 브랜치에 각각 merge 합니다.

<br/>

## Wiki

`Tiny Nginx` 에 대한 상세한 설명과 기술적인 고민들을 확인하실 수 있습니다.

- [Wiki Home](https://github.com/f-lab-edu/tiny-nginx/wiki)
- [기능 정의](https://github.com/f-lab-edu/tiny-nginx/wiki/%EA%B8%B0%EB%8A%A5-%EC%A0%95%EC%9D%98)