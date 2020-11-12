/*用于开发的脚本，命名规范遵循"项目名_dev*/
/*=======================参数定义区start======================*/
/*
需要在页面显示的参数
实际执行的参数值是界面上填写的值。若需要在页面显示参数，打开对应参数的注释，同时注释掉下面的所有行“map.put”
 */
properties([
        parameters([
                choice(
                        choices: ['master'],
                        description: '流水线类型',
                        name: 'PP_TYPE'
                ),
                text(defaultValue: 'https://github.com/HLDBanana/eureka_server.git',
                        description: '必填，执行部署的项目源代码仓库地址',
                        name: 'PJ_URL'
                ),
                text(
                        defaultValue: 'master',
                        description: '必填，执行部署的目标源代码项目仓库的分支名称',
                        name: 'PJ_BRANCH'
                ),
                credentials(
                        defaultValue: 'hld_harbor',
                        description: '必填，登录项目仓库使用的凭据ID，在持续交付中心凭据管理页面创建',
                        name: 'PJ_KEY',
                        required: true,
                        credentialType: 'com.cloudbees.plugins.credentials.common.StandardCredentials'
                ),
                choice(
                        choices: ['tag:kubernetes'],
                        description: '必填，应用部署的K8S集群名称，格式要求【名称:tag】，tag要与对应集群的持续交付中心slave端tag一致',
                        name: 'K8S_API_SERVER'
                ),
                choice(
                        choices: ['false', 'true'],
                        description: '仅执行部署，选择true以后：不会进行编译打包，选项：NEED_SCA、NEED_JUNIT不生效，需在执行过程选择chart版本；false会执行从源码到部署',
                        name: 'ONLY_DEPLOY'
                ),
                choice(
                        choices: ['false', 'true'],
                        description: '是否需要服务调试。默认为false，表示当前服务不开启服务调试',
                        name: 'NEED_SERVICE_DEBUG'
                ),
//---------------------------部署前测试--------------------------
                choice(
                        choices: ['false', 'true'],
                        description: '是否需要执行静态代码检查，true需要，false不需要',
                        name: 'NEED_SCA'
                ),
                choice(
                        choices: ['false', 'true'],
                        description: '后端项目是否需要执行Junit单元测试，true需要，false不需要',
                        name: 'NEED_JUNIT'
                ),
                choice(
                        choices: ['false', 'true'],
                        description: '是否需要执行镜像扫描',
                        name: 'NEED_IMAGE_SCAN'
                ),
//---------------------------部署后测试--------------------------
                choice(choices: ['false', 'true'], description: '是否需要执行ZAP安全测试', name: 'NEED_SAFE_TEST'),
                choice(choices: ['High', 'Medium', 'Low'], description: '执行ZAP安全测试的等级', name: 'ATK_LEVEL'),
//---------------------------私服配置--------------------------
                //空值时使用CommonEnv.groovy中的配置。如果项目有自己的npm私服，在这里配置私服地址。可支持配置多个地址。
                choice(
                        choices: ['', 'lugia:https://registry.npm.taobao.org/'],
                        description: '前端项目自定义npm私服',
                        name: 'NRM_LIB'
                )
        ]),
//---------------------------自动触发配置（可选）--------------------------
        /*
		确定放入不显示区域能否执行
       webhook配置：该配置实现在gitlab端代码有提交，则使用提交的配置信息重新构建项目
       pipelineTriggers的使用参考：https://github.com/jenkinsci/gitlab-plugin#job-trigger-configuration
       */
        pipelineTriggers([
                [
                        $class                        : 'GitLabPushTrigger',
                        branchFilterType              : 'All',
                        triggerOpenMergeRequestOnPush : "never",
                        skipWorkInProgressMergeRequest: true,
                        secretToken                   : '44fcdbc7c1e0c77c607b7f63c67458de',
                        branchFilterType              : "NameBasedFilter",
                        includeBranchesSpec           : "master", //这个分支push才会触发webhook，与PJ_BRANCH参数的值要一致
                        excludeBranchesSpec           : "",
                ]
        ])
])
