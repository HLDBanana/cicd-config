/*用于发布的脚本，命名规范遵循"项目名_release*/
/*==================参数定义区start==============*/
//要在页面显示参数，打开对应参数的注释就可以，实际执行的参数值是界面上填写的值，同时注释掉下面的所有行“map.put”
properties([
        parameters([
                choice(
                        choices: ['release'],
                        description: '流水线类型,仅支持master分支发布，如需修改发布分支，请联系管理员',
                        name: 'PP_TYPE'
                ),
                text(
                        defaultValue: 'http://[ip:port]/[path].git',
                        description: '必填，执行部署的项目源代码仓库地址',
                        name: 'PJ_URL'
                ),
                text(
                        defaultValue: 'master',
                        description: '执行部署的目标源代码项目仓库的分支名称。release流水线必须使用master分支',
                        name: 'PJ_BRANCH'
                ),
                credentials(
                        defaultValue: '',
                        credentialType: 'com.cloudbees.plugins.credentials.common.StandardCredentials',
                        description: '必填，登录项目仓库使用的凭据ID，在持续交付中心创建',
                        name: 'PJ_KEY',
                        required: true
                ),
                text(
                        defaultValue: 'http://[ip:port]/[path].git',
                        description: '后端项目测试脚本及测试用例git地址。如果是前端项目，不填',
                        name: 'TEST_SH_URL'
                ),
                text(
                        defaultValue: '',
                        description: '测试脚本及测试用例项目仓库的分支。后端项目必填',
                        name: 'TEST_SH_BRANCH'
                ),
                credentials(
                        defaultValue: '',
                        credentialType: 'com.cloudbees.plugins.credentials.common.StandardCredentials',
                        description: '测试脚本及测试用例项目仓库登录使用的持续交付中心登录。后端项目必填',
                        name: 'TEST_SH_KEY',
                        required: true
                ),
                choice(
                        choices: ['dev:release-cluster'],
                        description: '要部署应用的K8S环境，格式要求【名称:tag】，tag要与对应集群的持续交付中心slave端tag一致',
                        name: 'K8S_API_SERVER'
                ),
                choice(
                        choices: ['false', 'true'],
                        description: '仅执行部署，选择true以后：不会进行编译打包，选项：NEED_SCA、NEED_JUNIT不生效，需在执行过程选择chart版本；false会执行从源码到部署',
                        name: 'ONLY_DEPLOY'
                ),
//---------------------------部署前测试--------------------------
                choice(
                        choices: ['false', 'true'],
                        description: '后端项目是否需要执行静态代码检查，true需要，false不需要',
                        name: 'NEED_SCA'
                ),
                choice(
                        choices: ['false', 'true'],
                        description: '后端项目是否需要执行Junit单元测试，true需要，false不需要',
                        name: 'NEED_JUNIT'
                ),
                choice(
                        choices: ['false', 'true'],
                        description: '是否需要执行打包的image扫描',
                        name: 'NEED_IMAGE_SCAN'
                ),
//---------------------------部署后测试--------------------------
                choice(
                        choices: ['false', 'true'],
                        description: '后端项目是否需要执行接口测试，true需要，false不需要',
                        name: 'NEED_API_TEST'
                ),
                text(
                        defaultValue: 'APITestCase.xlsx',
                        description: '后端项目在代码仓库的测试用例文件名。后端项目必填',
                        name: 'APITestCase'
                ),
                choice(
                        choices: ['false', 'true'],
                        description: '后端项目是否需要执行性能测试，true需要，false不需要',
                        name: 'NEED_PM_TEST'
                ),
                text(
                        defaultValue: 'performanceCaseDemo.xlsx',
                        description: '后端项目在代码仓库的测试用例文件名。后端项目必填',
                        name: 'pmCaseFile'
                ),
                choice(
                        choices: ['false', 'true'],
                        description: '是否需要执行ZAP安全测试，true需要，false不需要',
                        name: 'NEED_SAFE_TEST'
                ),
                choice(
                        choices: ['High', 'Medium', 'Low'],
                        description: '执行ZAP安全测试的等级',
                        name: 'ATK_LEVEL'
                ),
//---------------------------私服配置--------------------------
                choice(
                        choices: ['', 'lugia:http://192.168.102.79:5001'],
                        description: '前端项目自定义npm私服',
                        name: 'NRM_LIB'
                ),
        ]),
        /*
        webhook配置
       该配置实现在gitlab端代码有提交，则使用提交的配置信息重新构建项目
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
                        includeBranchesSpec           : "master",  //这个分支push才会触发webhook，与PJ_BRANCH参数的值要一致
                        excludeBranchesSpec           : "",
                ]

        ])
])
//============================以下参数在页面不显示=================================
def DOCKERGROUP = "businesstest"//产物仓库的项目名

def REPLICAS = "1"//副本数量

def NAMESPACE = "bcloud"//k8s命名空间

def GROUP = "com.yss"//用于区分不同业务线应用同名而设置的前缀

def RELEASEDIR = "dist"//前端项目编译之后的目录，不建议修改

def CONTAINER_JVM_FLAGS = "" //后端项目JVM运行参数，如果为空值则使用默认配置
//---------------------------项目访问配置--------------------------
/*
【参数说明】
是否需要开启ipvs负载均衡。
默认为true，表示开启ipvs负载均衡。
 */
def NEED_IPVS_LOADBALANCE = "true"

/*
【参数说明】
ipvs负载均衡时访问应用的虚拟IP
1.当NEED_IPVS_LOADBALANCE选择为true时生效。
2.默认为空值，此时系统会自动分配虚拟IP；
3.如果用户需要使用固定的IP，需要配置该参数。
【注意】用户配置该参数时，需要在“运营实施中心”查看可用的虚拟IP。
 */
def LOADBALANCE_VIP = ""

/*
【参数说明】
1.部署前后端项目对应的域名；
2.如果后端服务需要使用https方式访问，需要与k8s集群中导入的证书绑定的域名一致；
 */
def MYDOMAIN1 = "demo-springbootv1demo.com"

/*
【参数说明】
1.前端项目时置空；
2.后端项目不使用证书时，为空值；
3.后端项目使用应用自签名证书时，需要将证书导入集群，并配置该参数，引用导入证书的名称。（导入证书方法参考《运营实施中心使用手册》5.5.2章节。）
4.后端项目使用泛域名证书时，不配置该参数，为空值。
 */
def SECRETNAME1 = ""

/*
【参数说明】
1.前端项目使用https协议访问时的域名，推荐和MYDOMAIN1相同；
2.后端项目使用扩展自定义监听端口（CONTAINERPORT2）时使用的域名，推荐使用与MYDOMAIN1不同的域名；
3.后端项目如果需要使用https方式访问，且使用应用独立证书，需要与k8s集群中导入的证书绑定的域名一致；
 */
def MYDOMAIN2 = "demo-springbootv1demo.com"

/*
【参数说明】
1.前后端项目使用自定义自签名证书时，需要将证书导入集群，并配置该参数，引用导入证书的名称。（导入证书方法参考《运营实施中心使用手册》5.5.2章节。）
2.使用云平台自动生成共有证书时，不配置该参数，为空值。
3.使用公有证书时，不配置该参数，为空值。
4.前端项目使用https方式访问时，传入在k8s集群中导入的证书名称；前端项目可以通过IP:PORT方式、域名方式访问；
【注意】后端项目推荐不使用证书访问，或者使用泛域名证书访问。
 */
def SECRETNAME2 = ""

/*
【参数说明】
1.后端项目应用端口，覆盖bootstrap.yaml配置文件的server.port参数；
2.前端项目对应http端口；
3.推荐使用40000-50000端口。
 */
def CONTAINERPORT1 = "40003"

/*
【参数说明】
1.前端项目对应https访问端口；
2.后端项目扩展自定义监听端口，覆盖bootstrap.yaml配置文件的server.port2参数；
3.gRPC项目grpc访问端口；
4.推荐使用40000-50000端口。
 */
def CONTAINERPORT2 = "40004"

/*
【参数说明】服务部署节点标签设置
在kubernetes集群节点上添加标签，标签格式为cloud.app.yss.[key] = business。
用户在这里填入“key”值。演示项目可直接设置为“demo”。
 */
def NODE_LABEL = "demo"

/*
【参数说明】配置中心配置文件标识
1.后端项目使用配置中心的配置文件标识；
2.如果该参数为空就为当前流水线类型，当前流水线有dev, test, release,类型；
3.例如：CONFIG_CENTER_PROFILE = "xxx"，使用配置中心配置文件为：[项目名]-xxx.yaml。
 */
def CONFIG_CENTER_PROFILE = "customer1-profile"

/*
【参数说明】后端项目执行接口测试，性能测试，安全测试使用的服务名称（前端项目仅涉及安全测试）。
参数范围：gateway网关服务名称或者服务自身的服务名。
 */
def TEST_SERVICE = "serviceName"

/*
【参数说明】执行就绪检查的接口
部署的应用中可以用于执行就绪检查的访问接口。
前端项目为根路径，即：“/”；
后端项目为可用的路径，用户自定义。
 */
def HEALTH_TEST_PATH = "/index"

/*
【参数说明】该配置用于指定前端项目需要使用容器外资源的目录，默认为空，表示不使用容器外资源。
该参数值需要与用户在nginx配置文件中使用的外部资源访问路径一致。
*/
def EXTERNAL_STATIC_RESOURCE_PATH = "/user/defined/path"
/*===================参数定义区end============================*/


def nodeTag = ""
if (null != env.K8S_API_SERVER && env.K8S_API_SERVER != "") {
    nodeTag = env.K8S_API_SERVER.split(":")[1]
    echo nodeTag
}

if (null == env.PP_TYPE) {
    node {
        stage('任务参数配置') {
            echo "首次执行只会导入任务默认参数，请修改参数后再执行任务"
        }
    }
    return
}

/*将参数放入需要传入共享库的map*/
def map = [:]
/*
SPRING_PROFILES_ACTIVE：本地应用开发使用的配置文件标识(不推荐使用)
需要与项目resources目录下bootstrap.yml文件的一致，如：bootstrap-dev.yml，就填写dev。
默认为空值，默认使用bootstrap.yml。
 */
map.put('SPRING_PROFILES_ACTIVE', '')
map.put('CONFIG_CENTER_PROFILE', CONFIG_CENTER_PROFILE)
//map.put('CHART_VER','dev')//不选，按流水线类型,输入了就按输入的（灵活后门）
map.put('PP_TYPE', "${PP_TYPE}")
map.put('DOCKERGROUP', DOCKERGROUP)
map.put('REPLICAS', REPLICAS)
map.put('NAMESPACE', NAMESPACE)
map.put('GROUP', GROUP)
map.put('NODE', nodeTag)
map.put('EXTERNALIP', '')
map.put('NEED_IPVS_LOADBALANCE', NEED_IPVS_LOADBALANCE)
map.put('LOADBALANCE_VIP', LOADBALANCE_VIP)
map.put('MYDOMAIN1', MYDOMAIN1)
map.put('SECRETNAME1', SECRETNAME1)
map.put('MYDOMAIN2', MYDOMAIN2)
map.put('SECRETNAME2', SECRETNAME2)
map.put('CONTAINERPORT2', CONTAINERPORT2)
map.put('CONTAINERPORT1', CONTAINERPORT1)
map.put('RELEASEDIR', RELEASEDIR)
map.put('NODE_LABEL', NODE_LABEL)
//以下APPNAME参数待扩展，暂不支持使用。
def APPNAME = ""//子项目名称，应用于maven子工程
map.put('APPNAME', APPNAME)
map.put('TEST_SERVICE', TEST_SERVICE)
map.put('HEALTH_TEST_PATH', HEALTH_TEST_PATH)
map.put('EXTERNAL_STATIC_RESOURCE_PATH', EXTERNAL_STATIC_RESOURCE_PATH)
map.put('NEED_IMAGE_SCAN', false)
map.put('CONTAINER_JVM_FLAGS', CONTAINER_JVM_FLAGS)
map.put('NEED_MAVENDEPOLY', false)
//引用共享库
def cicdLib = "yss-cicd-basic"
map.put('CICDLIB', cicdLib)
library cicdLib
//第一个参数代码执行哪个流水线，目前支持：开发/测试/发布/生产dev/test/release/product
BasicLib("${PP_TYPE}", map)
