# 仿 WeChat 大型即时通讯系统

## Overview

xxx

## Project Structure

- `wechat-dev`: Root module for managing and aggregating all submodules.

- `wechat-common`: Contains common dependencies and configurations used across the project. 
- `wechat-pojo`: Contains data objects and validation dependencies. 
- `api`: Contains submodules for different services:  
  - `auth-service`: Authentication service. 
  - `file-service`: File handling service.  
  - `main-service`: Main application service.  
  - `base-service`: Base service with common functionalities.  
  - `chat-server`: Chat server for handling messaging. 
- `gateway`: API Gateway module for routing and forwarding requests.

## Getting Started

### Prerequisites

To build and run the project, ensure you have:

- `JDK`：[openjdk-17_windows-x64_bin](https://download.java.net/java/GA/jdk17/0d483333a00540d886896bac774ff48b/35/GPL/openjdk-17_windows-x64_bin.zip)
- `Maven`：[apache-maven-3.9.2-bin](https://archive.apache.org/dist/maven/maven-3/3.9.2/binaries/)
- `Docker`：https://www.docker.com/products/docker-desktop/

### Build and Run

`mysql:8.3.0`：

```shell
$ docker pull mysql:8.3.0
$ docker run -p 4406:3306 --name mysql8-imooc \
-v /Volumes/docker/mysql8.3.0/log:/var/log/mysql \
-v /Volumes/docker/mysql8.3.0/data:/var/lib/mysql \
-v /Volumes/docker/mysql8.3.0/conf:/etc/mysql/conf.d \
-v /Volumes/docker/mysql8.3.0/mysql-files:/var/lib/mysql-files \
-e MYSQL_ROOT_PASSWORD=root \
-d mysql:8.3.0 \
--character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
```

`redis:7.2.4`：

```shell
$ docker pull redis:7.2.4
$ docker run -p 6379:6379 --name redis \
-v /Volumes/docker/redis7/data:/data \
-v /Volumes/docker/redis7/conf/redis.conf:/etc/redis/redis.conf \
-d redis:7.2.4 \
redis-server /etc/redis/redis.conf
```

**redis.conf**：

```PowerShell
requirepass redis
appendonly yes
```

`nacos/nacos-server:v2.3.2`：

```shell
$ docker pull nacos/nacos-server:v2.3.2
$ docker run --name nacos \
-e MODE=standalone \
-p 8848:8848 \
-p 9848:9848 \
-p 9849:9849 \
-p 7848:7848 \
-d nacos/nacos-server:v2.3.2
```

**minio/minio:latest**：

```shell
$ docker pull minio/minio
$ docker run -p 9000:9000 -p 9001:9001 --name minio \
-d --restart=always \
-e MINIO_ROOT_USER=solisamicus \
-e MINIO_ROOT_PASSWORD=miniowechat \
-v /Volumes/docker/minio/wechat/data:/data \
minio/minio server /data --console-address ":9001" --address ":9000"
```

## Contributing

xxx

## License

xxx