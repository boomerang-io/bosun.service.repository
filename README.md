# Boomerang Repository Service

**Description**

The Repository service works with the Bosun service. It pulls metrics data from software tools including JFrog Xray and SonarQube.

**Prerequisites**

1. Java 1.11 
2. Springboot 2.1.8 
3. Maven
4. OpenPolicyAgent

## How to Run:

**Service**

1. `mvn package -Dversion.name=1.0`
2. `java -jar /target/services-repository-1.0.jar`

**OpenPolicyAgent**

`docker run -p 8181:8181 openpolicyagent/opa run --server --log-level debug`

**Postman**

Included is a Postman collection that can be used to pull detail reports and metrics from JFrog Xray and SonarQube

## OPA Policies

The policy rego documents are stored in the Helm chart

## Rest API

### SonarQube Test Coverage Detail Report API

Pull the detailed test coverage and metrics report for the component from SonarQube.

```
GET  /repository/sonarqube/report/detail/testcoverage?id=xxx&version=xxx
```

**Where**
 - `id` is the unique identifier for the component in SonarQube
 - `version` is the version name of the component in SonarQube
 
 **Response**

```
 {
    "paging": {
        "pageIndex": 1,
        "pageSize": 100,
        "total": 2
    },
    "baseComponent": {
        "key": "xxxx",
        "name": "xxxx",
        "qualifier": "TRK",
        "measures": [
            {
                "metric": "uncovered_lines",
                "value": "1124",
                "bestValue": false,
                "history": []
            },
            {
                "metric": "lines_to_cover",
                "value": "1124",
                "history": []
            },
            {
                "metric": "line_coverage",
                "value": "0.0",
                "bestValue": false,
                "history": []
            },
            {
                "metric": "coverage",
                "value": "0.0",
                "bestValue": false,
                "history": []
            }
        ]
    },
    "components": [
        {
            "key": "xxxx:src/main/java/net/boomerangplatform/xxx/xxx/xxx.java",
            "name": "xxx.java",
            "qualifier": "FIL",
            "measures": [
                {
                    "metric": "test_errors",
                    "value": "0",
                    "bestValue": true,
                    "history": []
                },
                {
                    "metric": "test_failures",
                    "value": "0",
                    "bestValue": true,
                    "history": []
                },
                {
                    "metric": "test_success_density",
                    "value": "100.0",
                    "bestValue": true,
                    "history": []
                },
                {
                    "metric": "skipped_tests",
                    "value": "0",
                    "bestValue": true,
                    "history": []
                }
            ],
            "language": "java",
            "path": "src/main/java/net/boomerangplatform/xxx/xxx/xxx.java"
        },
        {
            "key": "xxxx:src/main/java/net/boomerangplatform/xxx/xxx/xxx.java",
            "name": "xxx.java",
            "qualifier": "FIL",
            "measures": [
                {
                    "metric": "test_errors",
                    "value": "0",
                    "bestValue": true,
                    "history": []
                },
                {
                    "metric": "test_failures",
                    "value": "0",
                    "bestValue": true,
                    "history": []
                },
                {
                    "metric": "uncovered_lines",
                    "value": "476",
                    "bestValue": false,
                    "history": []
                },
                {
                    "metric": "lines_to_cover",
                    "value": "476",
                    "history": []
                },
                {
                    "metric": "test_success_density",
                    "value": "100.0",
                    "bestValue": true,
                    "history": []
                },
                {
                    "metric": "line_coverage",
                    "value": "0.0",
                    "bestValue": false,
                    "history": []
                },
                {
                    "metric": "coverage",
                    "value": "0.0",
                    "bestValue": false,
                    "history": []
                },
                {
                    "metric": "skipped_tests",
                    "value": "0",
                    "bestValue": true,
                    "history": []
                }
            ],
            "language": "java",
            "path": "src/main/java/net/boomerangplatform/xxx/xxx/xxx.java"
        }
    ]
}
```