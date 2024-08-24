# WeChat-like Large-scale Instant Messaging System

## Overview

This project is a large-scale instant messaging system inspired by WeChat. It consists of several microservices, a gateway, and various supporting components like MySQL, Redis, RabbitMQ, Zookeeper, MinIO, and Nacos. The system is designed to be highly scalable, with each service operating independently.

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

https://docs.docker.com/engine/install/centos/

Set up the repository:

```shell
$ sudo yum install -y yum-utils
$ sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
```
Install Docker Engine, containerd, and Docker Compose:

```shell
$ sudo yum install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

Start Docker：

```shell
$ sudo systemctl start docker
```

| Component | Version           | Port Mapping                                                 | Container Name |
| --------- | ----------------- | ------------------------------------------------------------ | -------------- |
| MySQL     | `8.3.0`           | `13306:3306`                                                 | mysql          |
| Redis     | `7.2.4`           | `16379:6379`                                                 | redis          |
| RabbitMQ  | 3.13.6-management | `5681:5671`<br>`5682:5672`<br>`4379:4369`<br>`15681:15671`<br>`15682:15672`<br>`25682:25672` | rabbitmq       |
| Zookeeper | `3.9.2`           | `2181:2181`                                                  | zookeeper      |
| MinIO     | latest            | `5681:5671`<br/>`5682:5672`                                  | minio          |
| Nacos     | `v2.3.2`          | `8848:8848`<br>`9848:9848`<br>`9849:9849`<br>`7848:7848`     | nacos          |

#### MySQL

```shell
$ docker pull mysql:8.3.0
$ docker run -p 13306:3306 --name mysql \
-v /Volumes/docker/mysql8.3.0/log:/var/log/mysql \
-v /Volumes/docker/mysql8.3.0/data:/var/lib/mysql \
-v /Volumes/docker/mysql8.3.0/conf:/etc/mysql/conf.d \
-v /Volumes/docker/mysql8.3.0/mysql-files:/var/lib/mysql-files \
-e MYSQL_ROOT_PASSWORD=mysql \
-d mysql:8.3.0 \
--character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
```

Create a new database named `wechat-dev` with character set `utf8mb4` and collation `utf8mb4_unicode_ci`.

```shell
$ docker exec -it mysql mysql -uroot -p
```

```shell
$ CREATE DATABASE `wechat-dev` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
$ SHOW DATABASES;
$ SHOW CREATE DATABASE `wechat-dev`;
```

Execute the `sql/wechat-dev.sql` script to initialize the `wechat-dev` database.

#### Redis

```shell
$ docker pull redis:7.2.4
$ docker run -p 16379:6379 --name redis \
-v /Volumes/docker/redis7/data:/data \
-v /Volumes/docker/redis7/conf/redis.conf:/etc/redis/redis.conf \
-d redis:7.2.4 \
redis-server /etc/redis/redis.conf
```

Modify the `redis.conf` configuration file.

```conf
requirepass redis
appendonly yes
```

#### RabbitMQ

```shell
$ docker pull rabbitmq:3.13.6-management
$ docker run --name rabbitmq \
-p 5681:5671 \
-p 5682:5672 \
-p 4379:4369 \
-p 15681:15671 \
-p 15682:15672 \
-p 25682:25672 \
--restart always \
-d rabbitmq:3.13.6-management
```

- `-p 5681:5671`: Maps the host's port 5681 to the container's port 5671. Port 5671 is used for RabbitMQ's SSL communication.
- `-p 5682:5672`: Maps the host's port 5682 to the container's port 5672. Port 5672 is used for RabbitMQ's non-encrypted communication.
- `-p 4379:4369`: Maps the host's port 4379 to the container's port 4369. Port 4369 is used by distributed Erlang for RabbitMQ's cluster communication.
- `-p 15681:15671`: Maps the host's port 15681 to the container's port 15671. Port 15671 is used for RabbitMQ's management plugin over SSL.
- `-p 15682:15672`: Maps the host's port 15682 to the container's port 15672. Port 15672 is used for RabbitMQ's management plugin HTTP Web UI.
- `-p 25682:25672`: Maps the host's port 25682 to the container's port 25672. Port 25672 is used for RabbitMQ's internal cluster communication.

```shell
$ docker exec -it rabbitmq /bin/bash
```
Create a new user, set their permissions, and assign a role in RabbitMQ.
```shell
$ rabbitmqctl add_user solisamicus rabbitmq
$ rabbitmqctl set_permissions -p wechat-dev solisamicus ".*" ".*" ".*"
$ rabbitmqctl set_user_tags solisamicus administrator
```

You can log in to the RabbitMQ management console (by default at `http://{your_server_ip}:15682`) and verify that the `solisamicus` user has been created with the correct permissions.

#### Zookeeper

```shell
$ docker pull zookeeper:3.9.2
$ docker run --name zookeeper \
-p 2181:2181 \
-v /Volumes/docker/zookeeper3.9.2/data:/data \
-v /Volumes/docker/zookeeper3.9.2/conf:/conf \
-v /Volumes/docker/zookeeper3.9.2/logs:/datalog \
--restart always \
-d zookeeper:3.9.2
```

Download and Extract Zookeeper Configuration Files：

```shell
$ wget https://www.apache.org/dyn/closer.lua/zookeeper/zookeeper-3.9.2/apache-zookeeper-3.9.2-bin.tar.gz
$ tar -xzvf apache-zookeeper-3.9.2-bin.tar.gz
$ cd apache-zookeeper-3.9.2-bin/conf
```

Copy Configuration Files to the Docker Container

```shell
$ docker cp zoo_sample.cfg zookeeper:/conf/zoo.cfg
$ docker cp logback.xml zookeeper:/conf/logback.xml
```

Restart the Zookeeper Container

```shell
$ docker restart zookeeper
```

#### MinIO

```shell
$ docker pull minio/minio
$ docker run -p 9000:9000 -p 9001:9001 --name minio \
-e MINIO_ROOT_USER=solisamicus \
-e MINIO_ROOT_PASSWORD=miniowechat \
-v /Volumes/docker/minio/wechat/data:/data \
-d --restart=always \
minio/minio server /data --console-address ":9001" --address ":9000"
```

- `-p 9000:9000`: Maps port 9000 on the host to port 9000 in the container, which is used for accessing the MinIO server.
- `-p 9001:9001`: Maps port 9001 on the host to port 9001 in the container, which is used for accessing the MinIO console.

Create a new bucket named `wechat` and set its access policy to public.

#### Nacos

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

It is recommended to use the following JVM optimization parameters to prevent memory usage from skyrocketing.

```shell
$ docker run --name nacos \
-e MODE=standalone \
-e JVM_XMS=128m \
-e JVM_XMX=128m \
-e JVM_XMN=64m \
-e JVM_MS=64m \
-e JVM_MMS=64m \
-p 8848:8848 \
-p 9848:9848 \
-p 9849:9849 \
-p 7848:7848 \
-d nacos/nacos-server:v2.3.2-slim
```
