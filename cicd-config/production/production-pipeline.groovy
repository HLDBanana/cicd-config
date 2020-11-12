/*用于发布的脚本，命名规范遵循"项目名_production*/
/*=====================参数定义区start=========================*/
//要在页面显示参数，打开对应参数的注释就可以，实际执行的参数值是界面上填写的值，同时注释掉下面的所有行“map.put”
properties([
        parameters([
                choice(
                        choices: ['tag:pro-cluster'],
                        description: '必填，部署应用的K8S环境，格式要求【名称:tag】，tag要与对应集群的持续交付中心slave端tag一致',
                        name: 'K8S_API_SERVER'
                ),
                text(
                        defaultValue: 'devregistry.yssredefinecloud.com',
                        description: '必填，存放镜像的产物仓库域名',
                        name: 'HARBOR_NAME_PD'
                ),
                text(
                        defaultValue: 'lugia2.yssredefinecloud.com',
                        description: '选填，定义发布服务的第一个域名，如果不填写，会按产物包中定义的域名进行部署',
                        name: 'MYDOMIN1'
                ),
                text(
                        defaultValue: 'lugia2.yssredefinecloud.com',
                        description: '选填，定义发布服务的第二个域名，如果不填写，会按产物包中定义的域名进行部署',
                        name: 'MYDOMIN2'
                ),
                choice(
                        choices: ['true', 'false'],
                        description: '是否需要开启ipvs负载均衡。默认为true，表示开启ipvs负载均衡。',
                        name: 'NEED_IPVS_LOADBALANCE'
                ),
                /*
                【参数说明】
                ipvs负载均衡时访问应用的虚拟IP
                1.当NEED_IPVS_LOADBALANCE选择为true时生效。
                2.默认为空值，此时系统会自动分配虚拟IP；
                3.如果用户需要使用固定的IP，需要配置该参数。
                【注意】用户配置该参数时，需要在“运营实施中心”查看可用的虚拟IP。
                */
                text(
                        defaultValue: '',
                        description: 'ipvs负载均衡时访问应用的虚拟IP。置空时会自动分配；如果需要使用固定IP，需要用户配置。',
                        name: 'LOADBALANCE_VIP'
                ),
                choice(
                        choices: ['false', 'true'],
                        description: '可选，是否需要执行镜像扫描',
                        name: 'NEED_IMAGE_SCAN'
                )
        ])
])


env.BUILD_TYPE = "maven"//构建类型，区别前后端项目。前端项目为“npm”,后端项目为“maven”

def DOCKERGROUP = "businesstest"//产物仓库的项目名

def REPLICAS = "1"//副本数量

def NAMESPACE = "bcloud"//k8s命名空间

def GROUP = "com.yss"//用于区分不同业务线应用同名而设置的前缀

def PROJECTNAME = "springboot-15-d-demo" //项目名称，与应用项目pom.xml或者package.json中的name保持一致

def SECRETNAME1 = ""//使用MYDOMIN1，且使用应用独立证书时，在k8s集群中导入证书的名称。

def SECRETNAME2 = ""//使用MYDOMIN2，且使用应用独立证书时，在k8s集群中导入证书的名称。

/*
【参数说明】
1.后端项目应用端口，覆盖bootstrap.yaml配置文件的server.port参数；
2.前端项目对应http端口；
3.推荐使用40000-50000端口；
4.如果不填写，会按产物包中定义的域名进行部署。
 */
def CONTAINERPORT1 = "40003"

/*
【参数说明】
1.前端项目对应https访问端口；
2.后端项目扩展自定义监听端口，覆盖bootstrap.yaml配置文件的server.port2参数；
3.gRPC项目grpc访问端口；
4.推荐使用40000-50000端口；
5.如果不填写，会按产物包中定义的域名进行部署
 */
def CONTAINERPORT2 = "40004"

/*
【参数说明】服务部署节点标签设置
在kubernetes集群节点上添加标签，标签格式为cloud.app.yss.[key] = business。
用户在这里填入“key”值。演示项目可直接设置为“demo”。
 */
def NODE_LABEL = "demo"

/*
【参数说明】该配置用于指定前端项目需要使用容器外资源的目录，默认为空，表示不使用容器外资源。
该参数值需要与用户在nginx配置文件中使用的外部资源访问路径一致。
*/
def EXTERNAL_STATIC_RESOURCE_PATH = ""

/*===================参数定义区end============================*/

if (null == env.K8S_API_SERVER) {
    node {
        stage('任务参数配置') {
            echo "首次执行只会导入任务默认参数，请修改参数后再执行任务"
        }
    }
    return
}

def nodeTag = ""
if (null != env.K8S_API_SERVER && env.K8S_API_SERVER != "") {
    nodeTag = env.K8S_API_SERVER.split(":")[1]
    echo nodeTag
}

/*将参数放入需要传入共享库的map*/
def map = [:]

//map.put('CHART_VER', 'dev')//调试用的
map.put('DOCKERGROUP', DOCKERGROUP)
map.put('REPLICAS', REPLICAS)
map.put('NAMESPACE', NAMESPACE)
map.put('GROUP', GROUP)
map.put('PROJECTNAME', PROJECTNAME)
map.put('NODE', nodeTag)
map.put('PP_TYPE', "production")
map.put('EXTERNALIP', '')
map.put('NODE_LABEL', NODE_LABEL)
map.put('EXTERNAL_STATIC_RESOURCE_PATH', EXTERNAL_STATIC_RESOURCE_PATH)
map.put('SECRETNAME1', SECRETNAME1)
map.put('SECRETNAME2', SECRETNAME2)
map.put('CONTAINERPORT1', CONTAINERPORT1)
map.put('CONTAINERPORT2', CONTAINERPORT2)
//引用共享库
def cicdLib = "yss-cicd-basic"
map.put('CICDLIB', cicdLib)
library cicdLib
//执行生产部署
Production(map)