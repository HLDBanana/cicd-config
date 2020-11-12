# 持续交付中心项目配置库版本说明

## 【cicd-config-v2.3.2】
### 一、新功能

#### 1.支持ipvs负载均衡

#### 2.支持服务启动状态检查

### 二、参数更新

#### 1.删除流水线模板项目配置库（cicd-config）参数
> 删除参数：SH_URL, SH_BRANCH, SH_KEY

#### 2.即时通信配置方式变更
> 企业微信和钉钉改为机器人方式通信。

#### 3.增加后端项目JVM参数
> 增加参数：CONTAINER_JVM_FLAGS

#### 4.添加是否开启ipvs负载均衡的参数
> 增加参数：NEED_IPVS_LOADBALANCE

#### 5.删除外部访问IP，改为ipvs支持的虚拟IP
> 删除参数：EXTERNALIP
> 增加参数：LOADBALANCE_VIP


## 【cicd-config-v2.3.1】
### 一、新功能

####  1.支持服务远程调试

#### 2. 支持应用多域名多端口


## 【cicd-config-v2.3.0】
### 一、新功能
#### 1.后端项目在dev和test流水线支持exteranlIP 访问
> 在后端项目的开发（dev）和测试（test）流水线支持IP访问，增加EXTERNALIP配置项目，用户按照规则填写IP，即可使用IP访问后端服务。

#### 2.后端项目支持域名访问
> 后端项目增加MYDOMAIN参数，支持域名访问；
> 后端项目增加INGRESS_PATH参数，可以通过path:port方式给出的路由，访问后端服务。

#### 3.新增参数字典
> 增加参数字典paramsDict.md，用于快速参数查询。

### 二、参数更新
#### 1.模板合并
> 将开发（dev）、测试（test）、发布（release）、生产（production）流水线前后端合并为一个模板；

#### 2.路由参数改变
> 使用INGRESS_PATH参数代替HOST_REMOTE，用于定义服务路由。服务通过该参数暴露自身的接口，供其他服务调用。

## 【cicd-config-v2.2.0】
### 一、新功能
#### 1.前端项目支持用户自定义nginx配置
> 用户将自定义的nginx配置文件放入/nginx-conf目录，配置文件命名方式参考nginx-conf/README.md。这样用户能够使用自定义的nginx配置文件;

#### 2.支持前端项目访问容器外资源
> 添加EXTERNAL_STATIC_RESOURCE_PATH参数，指定前端项目需要访问的容器运行的宿主机资源路径，可以访问到该路径下的资源。


## 【cicd-config-v1.0.0】    
>引用Pipeline共享库的概念，在业务线配置参数，由共享库去完成实际的工作。

