/*定义一些可能多个项目or多个流水线构建可能公用的参数*/
/*----------------------参数定义区-----------------------------start-----------*/
/*凭据配置*/
env.HARBOR_CRED = 'jenkins-harbor-creds'//持续交付中心上产物仓库登录凭据
env.SONAR_CRED = 'jenkins-sonar-creds'//持续交付中心上静态分析服务端登录凭据

/*网络环境相关配置*/
env.IMAGE_PULL_SECRET="harsecret"  //产物仓库凭据名称，与运营实施中心配置的名称一致
env.HARBOR_NAME = "devregistry.yssredefinecloud.com"// 产物仓库的域名
env.HARBOR_SSH = "https"//产物仓库是什么协议的，只可以是http或者https
env.SONAR_SERVER = "sonarqube"//持续交付中心上配置的sonar的组件的名称
//env.NRM_LIB = "lugia:http://192.168.102.79:5001"//自定义npm私服
env.NRM_LIB = "lugia:https://registry.npm.taobao.org/"//自定义npm私服
env.CONTAINER_JVM_FLAGS="" //

/*单元测试阈值*/
env.classCoverage = "90"// 单元测试类覆盖率
env.methodCoverage = "90"// 单元测试方法覆盖率
env.branchCoverage = "90"// 单元测试分支覆盖率
env.lineCoverage = "90"// 单元测试行覆盖率

/*不同阶段邮件收件人*/
env.MAILTO_DEV = "512832170@qq.com"
env.MAILTO_TEST = "512832170@qq.com"
env.MAILTO_RELEASE = "512832170@qq.com"
env.MAILTO_PRO = "512832170@qq.com"

//企业微信消息接收者，多个用|分隔，如"[webhook1]|[webhook2]"
env.WEIXIN_DEV = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=64bac3b7-d527-48e6-ad15-52394a34a82b"
env.WEIXIN_TEST = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=64bac3b7-d527-48e6-ad15-52394a34a82b"
env.WEIXIN_RELEASE = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=64bac3b7-d527-48e6-ad15-52394a34a82b"
env.WEIXIN_PRO = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=64bac3b7-d527-48e6-ad15-52394a34a82b"
/*
钉钉机器人webhook和密钥设置。
webhook和密钥之间用“|”分割，一个webhook链接和对应的密钥为一组；
可以配置多个钉钉机器人，每组配置之间用“,”分隔，例如“[webhook1]|[secret1],[webhook2]|[secret2],......”
 */
env.DINGTALK_TO_DEV="https://oapi.dingtalk.com/robot/send?access_token=700600e3260795c67193035e58fe730304f0a6727d5f2be2bca538f3ee4f93ad|SEC504985265af1eee0cabfdd27afd9d8302271ae24fb6ebfa155e86713a26a28e7"
env.DINGTALK_TO_TEST="https://oapi.dingtalk.com/robot/send?access_token=700600e3260795c67193035e58fe730304f0a6727d5f2be2bca538f3ee4f93ad|SEC504985265af1eee0cabfdd27afd9d8302271ae24fb6ebfa155e86713a26a28e7"
env.DINGTALK_TO_RELEASE ="https://oapi.dingtalk.com/robot/send?access_token=700600e3260795c67193035e58fe730304f0a6727d5f2be2bca538f3ee4f93ad|SEC504985265af1eee0cabfdd27afd9d8302271ae24fb6ebfa155e86713a26a28e7"
env.DINGTALK_TO_PRO="https://oapi.dingtalk.com/robot/send?access_token=700600e3260795c67193035e58fe730304f0a6727d5f2be2bca538f3ee4f93ad|SEC504985265af1eee0cabfdd27afd9d8302271ae24fb6ebfa155e86713a26a28e7"

//更新日志显示的范围
env.TAG_COUNT=1  //release流水线构建时默认获取提交记录的tag数量。表示生成的记录包含多少个tag间隔，默认为1
env.DAY_COUNT=3  //dev, test分支构建时获取提交记录的天数，默认为1天，表示获取当前时间前一天的提交记录
/*----------------------参数定义区-----------------------------end-----------*/