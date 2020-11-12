# 1.nginx-conf目录作用
> 前端项目使用nginx作为静态页面容器。
> 产物仓库已经内置了nginx基础镜像，该nginx基础镜像默认只能作为静态页面容器使用。不具有请求转发，反向代理服务器功能。
> 如果用户需要使用这些功能，可以在这里放置自己的配置文件。
> 如果nginx-conf目录未添加用户定义的nginx配置，则默认使用nginx基础镜像功能。
> 该目录下不支持创建子目录，所有的配置文件都放置再该目录下。

# 2.用户自定义配置文件命名规则
> 配置文件命令格式如下：
> \[ProjectName\]-\[PipelineType\]-nginx.conf
> ProjectName 是当前项目的名称，例如node.js类型前端项目，在package.json配置文件中“name”字段描述的项目名称；
> PipelineType 是构建当前项目的流水线类型。
> 参考cicd-config目录，当前系统支持dev(开发)，test(测试)，release(发布)，product(生产)四条流水线。
> 例如，前端项目名称为“ligia-site” ，执行开发流水线(dev)，用户自定义的nginx.conf文件名为：ligia-site-dev-nginx.conf。